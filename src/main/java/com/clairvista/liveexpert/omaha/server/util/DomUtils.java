package com.clairvista.liveexpert.omaha.server.util;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

public class DomUtils {

   public static List<String> validateRequiredAttributes(Element element, String[] requiredRequestAttributes) {
      List<String> missingAttributes = new ArrayList<String>();
      for(String key : requiredRequestAttributes) {
         String value = element.getAttribute(key);
         if(value == null || value.trim().isEmpty()) {
            missingAttributes.add(key);
         }
      }
      return missingAttributes;
   }

   public static Boolean parseBoolean(Element element, String key) {
      String value = element.getAttribute(key);
      if(value != null && !value.isEmpty()) {
         if("1".equals(value) || "true".equals(value)) {
            return true;
         } else {
            return false;
         }
      }
      return null;
   }

   public static String parseString(Element element, String key) {
      String value = element.getAttribute(key);
      if(value != null && !value.isEmpty()) {
         return value;
      }
      return null;
   }

}
