package com.clairvista.liveexpert.omaha.server.response;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.clairvista.liveexpert.omaha.server.constants.APIElementNames;
import com.clairvista.liveexpert.omaha.server.constants.DayStartAttrs;
import com.clairvista.liveexpert.omaha.server.constants.ResponseAttrs;

public class ResponseRoot {

   private String protocol;
   private String server;
   private long secondsSinceMidnight;
   private List<ApplicationResponse> applicationResponses;
   
   public ResponseRoot(String protocol, String server) {
      this.protocol = protocol;
      this.server = server;
      this.applicationResponses = new ArrayList<ApplicationResponse>();
      
      Calendar now = Calendar.getInstance();
      Calendar lastMidnight = Calendar.getInstance();
      lastMidnight.set(Calendar.HOUR_OF_DAY, 0);
      lastMidnight.set(Calendar.MINUTE, 0);
      lastMidnight.set(Calendar.SECOND, 0);
      lastMidnight.set(Calendar.MILLISECOND, 0);

      secondsSinceMidnight = now.getTimeInMillis() - lastMidnight.getTimeInMillis() ;
   }
   
   public void addApplication(ApplicationResponse appResponse) {
      applicationResponses.add(appResponse);
   }
   
   public Element toXML(Document doc) {
      Element responseElem = doc.createElement(APIElementNames.RESPONSE);
      responseElem.setAttribute(ResponseAttrs.PROTOCOL, protocol);
      responseElem.setAttribute(ResponseAttrs.SERVER, server);
      
      Element dayStartElem = doc.createElement(APIElementNames.DAY_START);
      dayStartElem.setAttribute(DayStartAttrs.ELAPSED_SECONDS, Long.toString(secondsSinceMidnight));
      responseElem.appendChild(dayStartElem);
      
      for(ApplicationResponse appResponse : applicationResponses) {
         Element appElem = appResponse.toXML(doc);
         responseElem.appendChild(appElem);
      }
      
      return responseElem;
   }
   
}
