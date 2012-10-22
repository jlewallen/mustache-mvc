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
      String partialPath;
      if(!path.startsWith("/")) {
         File file = new File(getBasePath(), view);
         partialPath = (file.getParent() + File.separator + path);
      }
      else {
         partialPath = new File(getBasePath(), path).toString();
      }
      return getSource(partialPath.replace(getBasePath(), ""));
   }

   @Override
   public void setServletContext(ServletContext servletContext) {
      setResolver(new ServletContextResourcePatternResolver(servletContext));
   }
}
