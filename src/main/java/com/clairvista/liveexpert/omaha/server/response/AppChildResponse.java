package com.clairvista.liveexpert.omaha.server.response;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class AppChildResponse {

   protected String status;
   protected String errorDetails;
   
   public AppChildResponse() {
      status = "ok";
      errorDetails = null;
   }
   
   public void setStatus(String status) {
      this.status = status;
   }

   public void setErrorDetails(String errorDetails) {
      this.errorDetails = errorDetails;
   }

   public abstract Element toXML(Document doc);
   
}
