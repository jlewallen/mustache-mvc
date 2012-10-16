package com.page5of4.mustache;

import java.io.File;

import javax.servlet.ServletContext;

import org.springframework.core.io.Resource;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.ServletContextResourcePatternResolver;

public class ServletContextTemplateSourceLoader extends ClassPathTemplateSourceLoader implements ServletContextAware {
   public ServletContextTemplateSourceLoader() {
      super();
      setBasePath(File.separator + "WEB-INF" + File.separator + "views" + File.separator);
   }

   @Override
   protected String getClientSideKeyForResource(Resource resource) {
      return super.getClientSideKeyForResource(resource);
   }

   @Override
   public void setServletContext(ServletContext servletContext) {
      setResolver(new ServletContextResourcePatternResolver(servletContext));
   }
}
