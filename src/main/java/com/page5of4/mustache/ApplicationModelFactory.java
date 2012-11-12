package com.page5of4.mustache;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.ServletContextAware;

public class ApplicationModelFactory implements ServletContextAware {
   @Value("${application.base.url.override:}")
   private String applicationBaseOverride;
   private ServletContext servletContext;

   public String getContextPath() {
      if(StringUtils.isNotEmpty(applicationBaseOverride)) {
         return applicationBaseOverride;
      }
      return servletContext.getContextPath();
   }

   public ApplicationModel create() {
      return new ApplicationModel(getContextPath());
   }

   @Override
   public void setServletContext(ServletContext servletContext) {
      this.servletContext = servletContext;
   }
}
