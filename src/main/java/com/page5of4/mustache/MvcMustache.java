package com.page5of4.mustache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sampullara.mustache.Mustache;
import com.sampullara.mustache.MustacheException;
import com.sampullara.mustache.MustacheTrace;

public class MvcMustache extends Mustache {

   private static final Logger logger = LoggerFactory.getLogger(MvcMustache.class);
   private MustacheViewEngine viewEngine;

   public void setViewEngine(MustacheViewEngine viewEngine) {
      this.viewEngine = viewEngine;
   }

   @Override
   protected Mustache compilePartial(String name) throws MustacheException {
      MustacheTrace.Event event = null;
      if(trace) {
         event = MustacheTrace.addEvent("compile partial: " + name, getRoot() == null ? "classpath" : getRoot().getName());
      }
      Mustache mustache = viewEngine.createMustache(name);
      mustache.setMustacheJava(mj);
      mustache.setRoot(getRoot());
      if(trace) {
         event.end();
      }
      return mustache;
   }

}
