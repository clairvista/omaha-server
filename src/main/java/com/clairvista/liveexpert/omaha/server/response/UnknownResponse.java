package com.clairvista.liveexpert.omaha.server.response;

import static com.clairvista.liveexpert.omaha.server.constants.ApplicationAttrs.ERROR_DETAILS;
import static com.clairvista.liveexpert.omaha.server.constants.ApplicationAttrs.STATUS;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.clairvista.liveexpert.omaha.server.constants.APIElementNames;

public class UnknownResponse extends AppChildResponse {

   @Override
   public Element toXML(Document doc) {
      Element responseElem = doc.createElement(APIElementNames.UNKNOWN);
      responseElem.setAttribute(STATUS, status);
      responseElem.setAttribute(ERROR_DETAILS, errorDetails);
      
      return responseElem;
   }

}
