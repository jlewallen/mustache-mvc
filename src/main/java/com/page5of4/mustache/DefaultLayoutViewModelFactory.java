package com.page5of4.mustache;

import java.util.Map;

import javax.servlet.ServletConfig;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ServletConfigAware;

public class DefaultLayoutViewModelFactory implements LayoutViewModelFactory, ServletConfigAware {

   private static final Logger logger = LoggerFactory.getLogger(DefaultLayoutViewModelFactory.class);
   private ServletConfig servletConfig;
   private final ObjectMapper objectMapper = new ObjectMapper();

   @Override
   public LayoutViewModel createLayoutViewModel(Map<String, Object> model) {
      return createLayoutViewModel(model, null);
   }

   @Override
   public LayoutViewModel createLayoutViewModel(Map<String, Object> model, LayoutBodyFunction bodyFunction) {
      final ApplicationModel applicationModel = createApplicationModel();
      model.put(SingleModelAndView.APPLICATION_MODEL_NAME, applicationModel);
      String bodyModelAsJSON = getBodyModelAsJSON(SingleModelAndView.getBodyModel(model));
      return new LayoutViewModel(applicationModel, SingleModelAndView.getBodyModel(model), bodyModelAsJSON, SingleModelAndView.getLayoutModel(model), bodyFunction);
   }

   @Override
   public void setServletConfig(ServletConfig servletConfig) {
      this.servletConfig = servletConfig;
   }

   protected String getBodyModelAsJSON(Object bodyModel) {
      try {
         return objectMapper.writeValueAsString(bodyModel);
      }
      catch(Exception e) {
         logger.error("Error converting BodyModel to JSON", e);
         return null;
      }
   }

   protected ApplicationModel createApplicationModel() {
      return new ApplicationModel(servletConfig.getServletContext().getContextPath());
   }

   protected String encodeURL(String url) {
      return servletConfig.getServletContext().getContextPath() + "/" + url;
   }

}