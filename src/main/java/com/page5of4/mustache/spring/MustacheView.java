package com.page5of4.mustache.spring;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

import com.page5of4.mustache.MustacheViewEngine;

public class MustacheView extends AbstractUrlBasedView {
   private static Logger logger = LoggerFactory.getLogger(MustacheView.class);
   private static final String DEFAULT_LAYOUT_NAME = "layouts/default";
   public static final String DEFAULT_CONTENT_TYPE = "text/html;charset=UTF-8";
   private MustacheViewEngine engine;

   public MustacheViewEngine getEngine() {
      return engine;
   }

   @Autowired
   public void setEngine(MustacheViewEngine engine) {
      this.engine = engine;
   }

   public MustacheView() {
      super();
   }

   public MustacheView(String url) {
      super(url);
   }

   @Override
   public boolean checkResource(final Locale locale) throws Exception {
      LayoutAndView lav = LayoutAndView.getLayoutAndView(getUrl(), DEFAULT_LAYOUT_NAME, false);
      return engine.containsView(lav.getView());
   }

   @Override
   protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
      try {
         boolean forceNoLayout = isAjaxRequest(request);
         LayoutAndView lav = LayoutAndView.getLayoutAndView(getUrl(), DEFAULT_LAYOUT_NAME, forceNoLayout);
         if(response.getContentType() == null) {
            response.setContentType(DEFAULT_CONTENT_TYPE);
         }
         engine.render(lav, model, response.getWriter());
      }
      catch(Exception error) {
         throw new RuntimeException(String.format("Error rendering %s", getUrl()), error);
      }
   }

   private boolean isAjaxRequest(HttpServletRequest request) {
      final String XML_HTTP_REQUEST = "XMLHttpRequest";
      final String REQUESTED_WITH_HEADER = "X-Requested-With";
      return XML_HTTP_REQUEST.equals(request.getHeader(REQUESTED_WITH_HEADER));
   }
}
