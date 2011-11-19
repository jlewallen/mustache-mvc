package com.page5of4.mustache;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.RequestContext;

import com.samskivert.mustache.Mustache;

@Service
public class I18nLambdaFactory {

   @Autowired
   private MessageSource messageSource;

   @Autowired
   private HttpServletRequest request;

   public Mustache.Lambda getI18nLambda() {
      final Locale locale = new RequestContext(request).getLocale();
			return new Mustache.Lambda() {

         @Override
         public String apply(String message) {
            if(messageSource != null) {
               return messageSource.getMessage(message, null, locale);
            }
            return "";
         }
      };
   }
}
