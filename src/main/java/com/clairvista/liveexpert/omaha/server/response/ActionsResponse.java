package com.clairvista.liveexpert.omaha.server.response;

import static com.clairvista.liveexpert.omaha.server.constants.APIElementNames.ACTIONS;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ActionsResponse {

   private List<ActionResponse> actions;
   
   public ActionsResponse() {
      actions = new ArrayList<ActionResponse>();
   }
   
   public void addAction(ActionResponse action) {
      actions.add(action);
   }
   
   public Element toXML(Document doc) {
      Element responseElem = doc.createElement(ACTIONS);
      
      for(ActionResponse action : actions) {
         Element actionElem = action.toXML(doc);
         responseElem.appendChild(actionElem);
      }
      
      return responseElem;
   }
}
