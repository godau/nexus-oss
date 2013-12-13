package org.sonatype.nexus.rapture.internal;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sonatype.nexus.rapture.direct.DirectResource;

import com.director.core.DirectAction;
import com.director.core.DirectConfiguration;
import com.director.core.DirectContext;
import com.director.core.DirectException;
import com.director.core.DirectMethod;
import com.director.core.DirectTransactionData;
import com.director.core.DirectTransactionResult;
import com.director.core.ExecutorAdapter;
import com.director.core.Provider;
import com.director.core.json.JsonParser;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Key;
import org.eclipse.sisu.BeanEntry;
import org.eclipse.sisu.inject.BeanLocator;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @since 2.7
 */
@Named
@Singleton
public class DirectServlet
    extends HttpServlet
    implements ExecutorAdapter
{

  private static final String ENCODING = "UTF-8";

  private final DirectConfiguration configuration;

  private final BeanLocator beanLocator;

  @Inject
  public DirectServlet(final BeanLocator beanLocator) {
    this.configuration = new DirectConfiguration();
    this.beanLocator = checkNotNull(beanLocator);
  }

  @Override
  public void init(final ServletConfig config) throws ServletException {
    super.init(config);
    configuration.registerAdapter(this);
    Iterable<? extends BeanEntry<Annotation, DirectResource>> entries = beanLocator.locate(
        Key.get(DirectResource.class)
    );
    List<Class<?>> apiClasses = Lists.newArrayList(
        Iterables.transform(entries, new Function<BeanEntry<Annotation, DirectResource>, Class<?>>()
        {
          @Nullable
          @Override
          public Class<?> apply(final BeanEntry<Annotation, DirectResource> input) {
            return input.getImplementationClass();
          }
        })
    );
    for (Class<?> entry : apiClasses) {
      configuration.registerClass(
          entry, "NX.direct", entry.getSimpleName().replace("DirectResource", "")
      );
    }
  }

  @Override
  protected void service(final HttpServletRequest request, final HttpServletResponse response)
      throws ServletException, IOException
  {
    response.setContentType("text/plain");
    response.setCharacterEncoding(ENCODING);
    response.setHeader("Content-Disposition", "inline");

    String providerId = request.getParameter(configuration.getProviderParamName());
    if (providerId != null) {
      Provider provider = configuration.getProvider(providerId);
      provider.process(request, response);
    }
    else {
      String api = configuration.getFormattedApi();
      response.getWriter().write(api);
    }
  }

  @Override
  public DirectTransactionResult execute(final DirectAction directAction,
                                         final DirectMethod directMethod,
                                         final DirectTransactionData data) throws DirectException
  {
    try {
      Iterable<BeanEntry<Annotation, Object>> actionInstance = beanLocator.locate(
          Key.get(directAction.getActionClass())
      );
      Object actionClassInstance = actionInstance.iterator().next().getValue();
      Object result = directMethod.getMethod().invoke(actionClassInstance, directMethod.parseParameters(data));
      JsonParser parser = DirectContext.get().getConfiguration().getParser();
      return parser.buildResult(directMethod, result);
    }
    catch (InvocationTargetException e) {
      String message = "Cannot invoke the action method " + directMethod + " of the direct class " + directAction;
      throw new DirectException(message, e.getTargetException());
    }
    catch (Throwable e) {
      String message = "Cannot invoke the action method " + directMethod + " of the direct class " + directAction;
      throw new DirectException(message, e);
    }
  }

}
