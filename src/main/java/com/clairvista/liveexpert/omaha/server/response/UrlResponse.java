package com.clairvista.liveexpert.omaha.server.response;

import static com.clairvista.liveexpert.omaha.server.constants.UrlAttrs.CODEBASE;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.clairvista.liveexpert.omaha.server.constants.APIElementNames;

public class UrlResponse {

   private String codebase;

   public UrlResponse(String codebase) {
      this.codebase = codebase;
   }

   public void setCodebase(String codebase) {
      this.codebase = codebase;
   }

   public Element toXML(Document doc) {
      Element responseElem = doc.createElement(APIElementNames.URL);
      responseElem.setAttribute(CODEBASE, codebase);
      
      return responseElem;
   }
}
