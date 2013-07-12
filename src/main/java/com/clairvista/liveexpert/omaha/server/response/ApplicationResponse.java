package com.clairvista.liveexpert.omaha.server.response;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.clairvista.liveexpert.omaha.server.constants.APIElementNames;
import com.clairvista.liveexpert.omaha.server.constants.ApplicationAttrs;

public class ApplicationResponse {
   
   private String applicationID;
   private String status;
   private List<AppChildResponse> actions;
   
   public ApplicationResponse(String applicationID) {
      this.applicationID = applicationID;
      this.status = "ok";
      actions = new ArrayList<AppChildResponse>();
   }
   
   public void setStatus(String status) {
      this.status = status;
   }
   
   public void addActionResponse(AppChildResponse action) {
      actions.add(action);
   }

   public Element toXML(Document doc) {
      Element responseElem = doc.createElement(APIElementNames.APPLICATION);
      responseElem.setAttribute(ApplicationAttrs.APPLICATION_ID, applicationID);
      responseElem.setAttribute(ApplicationAttrs.STATUS, status);
      
      for(AppChildResponse action : actions) {
         Element actionElem = action.toXML(doc);
         responseElem.appendChild(actionElem);
      }
      
      return responseElem;
   }
   
}
