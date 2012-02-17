package com.page5of4.mustache;

import com.samskivert.mustache.Mustache;

public class LayoutViewModel {
   protected final ApplicationModel applicationModel;
   protected final LayoutBodyFunction layoutBodyFunction;
   protected final LayoutHeadersFunction layoutHeadersFunction;
   protected final Object bodyModel;
   protected final Object layoutModel;
   protected final String bodyModelAsJSON;
   protected final Mustache.Lambda i18nLambda;

   public ApplicationModel getApplicationModel() {
      return applicationModel;
   }

   public String getBody() {
      if(layoutBodyFunction == null) {
         throw new RuntimeException("{{body}} is only valid from within a Layout.");
      }
      return layoutBodyFunction.getBody();
   }

   public String getHeaders() {
      if(layoutHeadersFunction == null) {
         throw new RuntimeException("{{headers}} is only valid from within a Layout.");
      }
      return layoutHeadersFunction.getHeaders();
   }

   public Object getBodyModel() {
      return bodyModel;
   }

   protected LayoutBodyFunction getLayoutBodyFunction() {
      return layoutBodyFunction;
   }

   protected LayoutHeadersFunction getLayoutHeadersFunction() {
      return layoutHeadersFunction;
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

   public LayoutViewModel(ApplicationModel applicationModel, Object bodyModel, String bodyModelAsJSON, Object layoutModel, LayoutBodyFunction layoutBodyFunction,
         LayoutHeadersFunction layoutHeadersFunction, Mustache.Lambda i18nLambda) {
      super();
      this.applicationModel = applicationModel;
      this.bodyModel = bodyModel;
      this.bodyModelAsJSON = bodyModelAsJSON;
      this.layoutModel = layoutModel;
      this.layoutBodyFunction = layoutBodyFunction;
      this.layoutHeadersFunction = layoutHeadersFunction;
      this.i18nLambda = i18nLambda;
   }
}
