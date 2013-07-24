package com.clairvista.liveexpert.omaha.server.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public class ServletUtils {
   
   private static Logger LOGGER = Logger.getLogger(ServletUtils.class);
   
   public static Map<String, String> getPostParameters(HttpServletRequest request) {
      // Include any query string 
      String queryString = request.getQueryString();
      if (queryString == null) {
         queryString = "";
      }
      
      String rawPostParamString = getRawPostData(request);
      if(rawPostParamString == null) {
         rawPostParamString = "";
      }
      
      String fullRawParamString;
      if(!queryString.isEmpty()) {
         if(!rawPostParamString.isEmpty()) {
            fullRawParamString = queryString + "&" + rawPostParamString;
         } else {
            fullRawParamString = queryString;
         }
      } else {
         fullRawParamString = rawPostParamString;
      }
      
      return parseUrlEncodedParameters(fullRawParamString);
   }

   public static String getRawPostData(HttpServletRequest request) {
      String rawParameterString = "";

      try {
         ServletInputStream inputStream = request.getInputStream();
         if(inputStream != null) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = rd.readLine()) != null) {
               rawParameterString += line;
            }
            rd.close();
         }
      } catch (IOException ioe) {
         LOGGER.warn("Failed to proccess POST request data: ", ioe);
      }
      
      return rawParameterString;
   }
   
   private static Map<String, String> parseUrlEncodedParameters(String urlEncodedParamString) {
      HashMap<String, String> paramMap = new HashMap<String, String>();
         
      if (urlEncodedParamString != null) {
         String[] params = urlEncodedParamString.split("&");
         for (String param : params) {
            String[] paramPair = param.split("=");
            try {
               if (paramPair.length > 1) {
                  String key = URLDecoder.decode(paramPair[0], "UTF-8");
                  String value = URLDecoder.decode(paramPair[1], "UTF-8");
                  paramMap.put(key, value);
               } else if (paramPair.length > 0) {
                  String key = URLDecoder.decode(paramPair[0], "UTF-8");
                  paramMap.put(key, "");
               }
            } catch(UnsupportedEncodingException uee) {
               LOGGER.warn("Unsupported encoding for input parameter: " + param, uee);
            }
         }
      }
         
      return paramMap;
   }
}
