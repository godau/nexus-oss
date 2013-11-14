package com.director.core;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Author: Simone Ricciardi
 * Date: 8-giu-2010
 * Time: 11.53.47
 */
public abstract class AbstractProvider implements Provider {

   private static final Log LOG = LogFactory.getLog(AbstractProvider.class);

   private String id;
   private String namespace;
   private ProviderType type;
   private DirectConfiguration configuration;

   protected AbstractProvider(String id, String namespace, ProviderType type, DirectConfiguration configuration) {
      this.id = id;
      this.namespace = namespace;
      this.type = type;
      this.configuration = configuration;
   }

   public String getUrl() {
      String providerParamName = this.configuration.getProviderParamName();
      return String.format("/nexus/service/rapture/direct?%s=%s", providerParamName, this.id);
   }

   public String getId() {
      return this.id;
   }

   public String getNamespace() {
      return this.namespace;
   }

   public String getType() {
      return this.type.getTypeName();
   }

   public void process(HttpServletRequest request, HttpServletResponse response) {
      try {
         DirectContext.init(request, response, this.configuration);
         this.handleProcess();
         this.handleResponse();
      } finally {
         DirectContext.dispose();
      }
   }

   private void handleProcess() {
      try {
         this.doProcess();
      } catch(Throwable e) {
         LOG.error("Provider error, provider id " + this.id + " error executing direct request ", e);
         DirectContext.get().pushEvent(new DirectExceptionEvent(e));
      }
   }

   private void handleResponse() {
      try {
         this.formatForOutput();
      } catch(Exception e) {
         LOG.error("Provider error, provider id " + this.id + " error formatting direct request output", e);
      }
   }

   protected abstract void doProcess() throws Throwable;

   protected abstract void formatForOutput() throws IOException;
}
