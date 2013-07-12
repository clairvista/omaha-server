package com.clairvista.liveexpert.omaha.server.response;

import static com.clairvista.liveexpert.omaha.server.constants.APIElementNames.URLS;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class UrlsResponse {

   private List<UrlResponse> urls;
   
   public UrlsResponse() {
      urls = new ArrayList<UrlResponse>();
   }
   
   public void addUrl(UrlResponse url) {
      urls.add(url);
   }

   public Element toXML(Document doc) {
      Element responseElem = doc.createElement(URLS);
      
      for(UrlResponse url : urls) {
         Element urlElem = url.toXML(doc);
         responseElem.appendChild(urlElem);
      }
      
      return responseElem;
   }
}
