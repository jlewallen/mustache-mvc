package com.page5of4.mustache;

import java.util.Map;

import javax.servlet.ServletConfig;

import org.springframework.web.context.ServletConfigAware;

public class DefaultLayoutViewModelFactory implements LayoutViewModelFactory, ServletConfigAware {

   private ServletConfig servletConfig;

   @Override
   public LayoutViewModel createLayoutViewModel(Map<String, Object> model) {
      return createLayoutViewModel(model, null);
   }

   @Override
   public LayoutViewModel createLayoutViewModel(Map<String, Object> model, LayoutBodyFunction bodyFunction) {
      final ApplicationModel applicationModel = createApplicationModel();
      model.put(SingleModelAndView.APPLICATION_MODEL_NAME, applicationModel);
      return new LayoutViewModel(applicationModel, SingleModelAndView.getBodyModel(model), SingleModelAndView.getLayoutModel(model), bodyFunction);
   }

   private ApplicationModel createApplicationModel() {
      return new ApplicationModel(servletConfig.getServletContext().getContextPath());
   }

   @Override
   public void setServletConfig(ServletConfig servletConfig) {
      this.servletConfig = servletConfig;
   }

}
