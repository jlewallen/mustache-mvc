package com.page5of4.mustache;

import java.util.Map;

import javax.servlet.ServletConfig;

import org.springframework.web.context.ServletConfigAware;

public class DefaultLayoutViewModelFactory implements LayoutViewModelFactory, ServletConfigAware {

   private ServletConfig servletConfig;

   @Override
   public LayoutViewModel createLayoutViewModel(Map<String, Object> bodyModel, LayoutBodyFunction bodyFunction) {
      final ApplicationModel applicationModel = createApplicationModel();
      bodyModel.put("applicationModel", applicationModel);
      return new LayoutViewModel(applicationModel, bodyModel, bodyFunction);
   }

   private ApplicationModel createApplicationModel() {
      return new ApplicationModel(servletConfig.getServletContext().getContextPath());
   }

   @Override
   public void setServletConfig(ServletConfig servletConfig) {
      this.servletConfig = servletConfig;
   }

}
