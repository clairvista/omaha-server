package com.clairvista.liveexpert.omaha.server.response;

import static com.clairvista.liveexpert.omaha.server.constants.APIElementNames.ACTION;
import static com.clairvista.liveexpert.omaha.server.constants.ActionAttrs.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ActionResponse {
   
   private String event;
   private String run;
   private String arguments;
   private String successURL;
   private String terminateAllBrowsers;
   private String successAction;
   
   public void setEvent(String event) {
      this.event = event;
   }
   public void setRun(String run) {
      this.run = run;
   }
   public void setArguments(String arguments) {
      this.arguments = arguments;
   }
   public void setSuccessURL(String successURL) {
      this.successURL = successURL;
   }
   public void setTerminateAllBrowsers(String terminateAllBrowsers) {
      this.terminateAllBrowsers = terminateAllBrowsers;
   }
   public void setSuccessAction(String successAction) {
      this.successAction = successAction;
   }
   
   public Element toXML(Document doc) {
      Element responseElem = doc.createElement(ACTION);
      responseElem.setAttribute(EVENT, event);
      responseElem.setAttribute(RUN, run);
      responseElem.setAttribute(ARGUMENTS, arguments);
      responseElem.setAttribute(SUCCESS_URL, successURL);
      responseElem.setAttribute(TERMINATE_ALL_BROWSERS, terminateAllBrowsers);
      responseElem.setAttribute(SUCCESS_ACTION, successAction);
      
      return responseElem;
   }
   
}
