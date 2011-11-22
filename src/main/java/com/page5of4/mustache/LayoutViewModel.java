package com.page5of4.mustache;

import com.samskivert.mustache.Mustache;

public class LayoutViewModel {
   private final ApplicationModel applicationModel;
   private final LayoutBodyFunction layoutBodyFunction;
   private final Object bodyModel;
   private final Object layoutModel;
   private final String bodyModelAsJSON;
   private final Mustache.Lambda i18nLambda;

   public ApplicationModel getApplicationModel() {
      return applicationModel;
   }

   public String getBody() {
      if(layoutBodyFunction == null) {
         throw new RuntimeException("{{body}} is only valid from within a Layout.");
      }
      return layoutBodyFunction.getBody();
   }

   public Object getBodyModel() {
      return bodyModel;
   }

   public String getBodyModelAsJSON() {
      return bodyModelAsJSON;
   }

   public Object getLayoutModel() {
      return layoutModel;
   }

   public Mustache.Lambda geti18n() {
      return i18nLambda;
   }

   public Mustache.Lambda getM() {
      return i18nLambda;
   }

   public LayoutViewModel(ApplicationModel applicationModel, Object bodyModel, String bodyModelAsJSON, Object layoutModel, LayoutBodyFunction layoutBodyFunction, Mustache.Lambda i18nLambda) {
      super();
      this.applicationModel = applicationModel;
      this.bodyModel = bodyModel;
      this.bodyModelAsJSON = bodyModelAsJSON;
      this.layoutModel = layoutModel;
      this.layoutBodyFunction = layoutBodyFunction;
      this.i18nLambda = i18nLambda;
   }
}
