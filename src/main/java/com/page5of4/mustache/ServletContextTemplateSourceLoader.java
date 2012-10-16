package com.page5of4.mustache;

import java.io.File;

import javax.servlet.ServletContext;

import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.ServletContextResourcePatternResolver;

public class ServletContextTemplateSourceLoader extends ClassPathTemplateSourceLoader implements ServletContextAware {
   public ServletContextTemplateSourceLoader() {
      super();
      setBasePath(File.separator + "WEB-INF" + File.separator + "views" + File.separator);
   }

   @Override
   public String getPartial(String view, String path) {
      File file = new File(getBasePath(), view);
      String partialPath = (file.getParent() + File.separator + path).replace(getBasePath(), "");
      return getSource(partialPath);
   }

   @Override
   public void setServletContext(ServletContext servletContext) {
      setResolver(new ServletContextResourcePatternResolver(servletContext));
   }
}
