package com.page5of4.mustache;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.RequestContext;

import com.google.common.base.Function;

@Service
public class I18nLambdaFactory {

   @Autowired
   private MessageSource messageSource;

   @Autowired
   private HttpServletRequest request;

   public Function<String, String> getI18nLambda() {
      return new Function<String, String>() {

         private Locale locale;

         private Locale getLocale() {
            if(locale == null) {
               locale = new RequestContext(request).getLocale();
            }
            return locale;
         }

         @Override
         public String apply(String message) {
            if(messageSource != null) {
               return messageSource.getMessage(message, null, getLocale());
            }
            return "";
         }
      };
   }
}
