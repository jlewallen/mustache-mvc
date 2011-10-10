package com.page5of4.mustache.spring;

import org.springframework.web.servlet.view.UrlBasedViewResolver;

import com.page5of4.mustache.MustacheViewEngine;

public class MustacheViewResolver extends UrlBasedViewResolver {

   private MustacheViewEngine engine;

   public MustacheViewEngine getEngine() {
      return engine;
   }

   public void setEngine(MustacheViewEngine engine) {
      this.engine = engine;
   }

   @Override
   protected MustacheView buildView(String viewName) throws Exception {
      MustacheView view = (MustacheView)super.buildView(viewName);
      view.setEngine(engine);
      return view;
   }

}
