package com.clairvista.liveexpert.omaha.server.response;

import static com.clairvista.liveexpert.omaha.server.constants.ApplicationAttrs.APPLICATION_ID;
import static com.clairvista.liveexpert.omaha.server.constants.ApplicationAttrs.ERROR_DETAILS;
import static com.clairvista.liveexpert.omaha.server.constants.ApplicationAttrs.STATUS;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.clairvista.liveexpert.omaha.server.constants.APIElementNames;

public class ApplicationResponse {
   
   private String applicationID;
   private String status;
   private String errorDetails;
   private List<AppChildResponse> actions;
   
   public ApplicationResponse(String applicationID) {
      this.applicationID = applicationID;
      this.status = "ok";
      this.errorDetails = null;
      actions = new ArrayList<AppChildResponse>();
   }
   
   public void setStatus(String status) {
      this.status = status;
   }
   
   public void addActionResponse(AppChildResponse action) {
      actions.add(action);
   }

   public void setErrorDetails(String errorDetails) {
      this.errorDetails = errorDetails;
   }

   public Element toXML(Document doc) {
      Element responseElem = doc.createElement(APIElementNames.APPLICATION);
      responseElem.setAttribute(APPLICATION_ID, applicationID);
      responseElem.setAttribute(STATUS, status);
      responseElem.setAttribute(ERROR_DETAILS, errorDetails);
      
      for(AppChildResponse action : actions) {
         Element actionElem = action.toXML(doc);
         responseElem.appendChild(actionElem);
      }
      
      return responseElem;
   }
   
}
