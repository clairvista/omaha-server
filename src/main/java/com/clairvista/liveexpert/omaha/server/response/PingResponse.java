package com.clairvista.liveexpert.omaha.server.response;

import static com.clairvista.liveexpert.omaha.server.constants.ApplicationAttrs.ERROR_DETAILS;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.clairvista.liveexpert.omaha.server.constants.APIElementNames;
import com.clairvista.liveexpert.omaha.server.constants.ApplicationAttrs;

public class PingResponse extends AppChildResponse {

   @Override
   public Element toXML(Document doc) {
      Element responseElem = doc.createElement(APIElementNames.PING);
      responseElem.setAttribute(ApplicationAttrs.STATUS, status);
      responseElem.setAttribute(ERROR_DETAILS, errorDetails);
      
      return responseElem;
   }
   
}
