package com.page5of4.mustache;

import java.util.Map;

import javax.servlet.ServletContext;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ServletContextAware;

import com.samskivert.mustache.Mustache;

public class DefaultLayoutViewModelFactory implements LayoutViewModelFactory, ServletContextAware {
   private static final Logger logger = LoggerFactory.getLogger(DefaultLayoutViewModelFactory.class);
   private ServletContext servletContext;
   private final ObjectMapper objectMapper = new ObjectMapper();

   @Override
   public LayoutViewModel createLayoutViewModel(Map<String, Object> model) {
      return createLayoutViewModel(model, null);
   }

   @Autowired(required = false)
   private I18nLambdaFactory localizationFactory;

   @Override
   public DefaultLayoutViewModel createLayoutViewModel(Map<String, Object> model, LayoutBodyFunction bodyFunction) {
      return createLayoutViewModel(model, bodyFunction, null);
   }

   @Override
   public DefaultLayoutViewModel createLayoutViewModel(Map<String, Object> model, LayoutBodyFunction bodyFunction, LayoutHeadersFunction headersFunction) {
      final ApplicationModel applicationModel = createApplicationModel();
      model.put(SingleModelAndView.APPLICATION_MODEL_NAME, applicationModel);
      String bodyModelAsJSON = getBodyModelAsJSON(SingleModelAndView.getBodyModel(model));
      Mustache.Lambda i18nLambda = null;
      if(localizationFactory != null) {
         i18nLambda = localizationFactory.getI18nLambda();
      }
      return new DefaultLayoutViewModel(applicationModel, SingleModelAndView.getBodyModel(model), bodyModelAsJSON, SingleModelAndView.getLayoutModel(model), bodyFunction, headersFunction, i18nLambda);
   }

   @Override
   public void setServletContext(ServletContext servletContext) {
      this.servletContext = servletContext;
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
      return new ApplicationModel(servletContext.getContextPath());
   }

   protected String encodeURL(String url) {
      return servletContext.getContextPath() + "/" + url;
   }
}
