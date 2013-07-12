package com.clairvista.liveexpert.omaha.server.response;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class AppChildResponse {

   protected String status;
   
   public AppChildResponse() {
      status = "ok";
   }
   
   public abstract Element toXML(Document doc);
   
}
