package com.page5of4.mustache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samskivert.mustache.Mustache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.util.Map;
import java.util.concurrent.Callable;

public class DefaultLayoutViewModelFactory implements LayoutViewModelFactory, ServletContextAware {
   private static final Logger logger = LoggerFactory.getLogger(DefaultLayoutViewModelFactory.class);
   private ServletContext servletContext;
   private final ObjectMapper objectMapper = new ObjectMapper();

   @Value("${application.base.url.override:}")
   private String applicationBaseOverride;

   @Autowired
   private ApplicationModelFactory applicationModelFactory;

   @Autowired(required = false)
   private I18nLambdaFactory localizationFactory;

   public void setApplicationModelFactory(ApplicationModelFactory applicationModelFactory) {
      this.applicationModelFactory = applicationModelFactory;
   }

   @Override
   public LayoutViewModel createLayoutViewModel(Map<String, Object> model) {
      return createLayoutViewModel(model, null);
   }

   @Override
   public DefaultLayoutViewModel createLayoutViewModel(Map<String, Object> model, LayoutBodyFunction bodyFunction) {
      return createLayoutViewModel(model, bodyFunction, null);
   }

   @Override
   public DefaultLayoutViewModel createLayoutViewModel(final Map<String, Object> model, LayoutBodyFunction bodyFunction, LayoutHeadersFunction headersFunction) {
      final ApplicationModel applicationModel = createApplicationModel();
      model.put(SingleModelAndView.APPLICATION_MODEL_NAME, applicationModel);
      Callable<String> bodyModelAsJsonFunction = new Callable<String>() {
         @Override
         public String call() throws Exception {
            return getBodyModelAsJSON(SingleModelAndView.getBodyModel(model));
         }
      };
      Mustache.Lambda i18nLambda = null;
      if(localizationFactory != null) {
         i18nLambda = localizationFactory.getI18nLambda();
      }
      return new DefaultLayoutViewModel(applicationModel, SingleModelAndView.getBodyModel(model), bodyModelAsJsonFunction, SingleModelAndView.getLayoutModel(model), bodyFunction, headersFunction, i18nLambda);
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
      return applicationModelFactory.create();
   }

   protected String encodeURL(String url) {
      return servletContext.getContextPath() + "/" + url;
   }
}
