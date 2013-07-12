package com.clairvista.liveexpert.omaha.server.response;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.clairvista.liveexpert.omaha.server.constants.APIElementNames;
import com.clairvista.liveexpert.omaha.server.constants.ApplicationAttrs;

public class EventResponse extends AppChildResponse {

   @Override
   public Element toXML(Document doc) {
      Element responseElem = doc.createElement(APIElementNames.EVENT);
      responseElem.setAttribute(ApplicationAttrs.STATUS, status);
      
      return responseElem;
   }

}
