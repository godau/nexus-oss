package org.sonatype.nexus.rapture.internal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.nexus.web.WebResource;
import org.sonatype.nexus.web.WebResourceBundle;
import org.sonatype.sisu.goodies.template.TemplateEngine;
import org.sonatype.sisu.goodies.template.TemplateParameters;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Rapture web-resources.
 *
 * @since 2.7
 */
@Named
@Singleton
public class WebResources
    implements WebResourceBundle
{
  private static final Logger log = LoggerFactory.getLogger(WebResources.class);

  private final TemplateEngine templateEngine;

  private final List<WebResourceBundle> resourceBundles;

  @Inject
  public WebResources(final TemplateEngine templateEngine,
                      final List<WebResourceBundle> resourceBundles)
  {
    this.templateEngine = checkNotNull(templateEngine);
    this.resourceBundles = checkNotNull(resourceBundles);
  }

  @Override
  public List<WebResource> getResources() {
    return Arrays.<WebResource>asList(
        new AppJs()
    );
  }

  /**
   * Renders app.js with {@code NX.app.pluginConfigClassNames} set to the list of detected
   * {@code NX._package_.app.PluginConfig} extjs classes.
   */
  private class AppJs
      implements WebResource
  {
    public static final String NS_PREFIX = "/static/rapture/NX/";

    public static final String PLUGIN_CONFIG_SUFFIX = "/app/PluginConfig.js";

    @Override
    public String getPath() {
      return "/static/rapture/app.js";
    }

    @Override
    public String getContentType() {
      return "application/x-javascript";
    }

    @Override
    public long getSize() {
      return -1;
    }

    @Override
    public Long getLastModified() {
      return System.currentTimeMillis();
    }

    @Override
    public InputStream getInputStream() throws IOException {
      List<String> classNames = getPluginConfigClassNames();
      if (classNames.isEmpty()) {
        log.warn("Did not detect any rapture plugin configurations");
      }
      else {
        log.debug("Plugin config class names: {}", classNames);
      }

      URL template = WebResources.class.getResource("app.vm");
      checkState(template != null, "Missing app.vm template");

      return new ByteArrayInputStream(templateEngine.render(
          this, template, new TemplateParameters().set("pluginConfigClassNames", join(classNames))).getBytes()
      );
    }

    /**
     * Returns a list of all {@code NX._package_.app.PluginConfig} extjs classes.
     */
    private List<String> getPluginConfigClassNames() {
      List<String> classNames = Lists.newArrayList();
      for (WebResourceBundle bundle : resourceBundles) {
        for (WebResource resource : bundle.getResources()) {
          String path = resource.getPath();
          if (path.startsWith(NS_PREFIX) && path.endsWith(PLUGIN_CONFIG_SUFFIX)) {
            // rebuild the class name which has NX. prefix and minus the .js suffix
            String name = path.substring(NS_PREFIX.length() - "NX/".length(), path.length() - ".js".length());
            // convert path to class-name
            name = name.replace("/", ".");
            classNames.add(name);
          }
        }
      }
      return classNames;
    }

    /**
     * Joins the list of strings quoted for javascript list members (in side of [ ... ]).
     */
    private String join(final List<String> list) {
      StringBuilder buff = new StringBuilder();
      Iterator<String> iter = list.iterator();
      while (iter.hasNext()) {
        String pkg = iter.next();
        buff.append("'").append(pkg).append("'");
        if (iter.hasNext()) {
          buff.append(",");
        }
      }
      return buff.toString();
    }
  }
}
