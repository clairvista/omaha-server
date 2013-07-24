package com.clairvista.liveexpert.omaha.server.util;

@SuppressWarnings("serial")
public class RequestElementValidationException extends Exception {
   
   private String responseErrorDetails;

   public RequestElementValidationException(String message, String responseErrorDetails) {
      super(message);
      this.responseErrorDetails = responseErrorDetails;
   }
   
   public String getResponseErrorDetails() {
      return responseErrorDetails;
   }
}
