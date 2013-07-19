package com.clairvista.liveexpert.omaha.server.util;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

public class XMLUtils {
   
   private static Logger LOGGER = Logger.getLogger(XMLUtils.class);

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

   public static String parseString(Element element, String key) {
      String value = element.getAttribute(key);
      if(value != null && !value.isEmpty()) {
         return value;
      }
      return null;
   }

   public static Integer parseInteger(Element element, String key) {
      String value = element.getAttribute(key);
      if(value != null && !value.isEmpty()) {
         try {
            return Integer.parseInt(value);
         } catch (NumberFormatException nfe) {
            LOGGER.warn("Failed to parse integer input from: " + value, nfe);
         }
      }
      return null;
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

   public static String elementToString(Element element) {
      Document document = element.getOwnerDocument();
      DOMImplementationLS domImplLS = (DOMImplementationLS) document.getImplementation();
      LSSerializer serializer = domImplLS.createLSSerializer();

      // Client assumes/requires UTF-8 response.
      LSOutput lsOutput = domImplLS.createLSOutput();
      lsOutput.setEncoding("UTF-8");

      StringWriter stringWriter = new StringWriter();
      lsOutput.setCharacterStream(stringWriter);
      serializer.write(element, lsOutput);
      
      return stringWriter.toString();
   }

   public static String documentToString(Document doc) {
      DOMImplementationLS domImplLS = (DOMImplementationLS) doc.getImplementation();
      LSSerializer serializer = domImplLS.createLSSerializer();
      return serializer.writeToString(doc);
   }

}
