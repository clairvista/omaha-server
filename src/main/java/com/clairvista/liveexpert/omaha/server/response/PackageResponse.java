package com.clairvista.liveexpert.omaha.server.response;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static com.clairvista.liveexpert.omaha.server.constants.APIElementNames.*;
import static com.clairvista.liveexpert.omaha.server.constants.PackageAttrs.*;

public class PackageResponse {

   private String name;
   private String hash;
   private Boolean required;
   private Integer size;

   public void setName(String name) {
      this.name = name;
   }

   public void setHash(String hash) {
      this.hash = hash;
   }

   public void setRequired(Boolean required) {
      this.required = required;
   }

   public void setSize(Integer size) {
      this.size = size;
   }

   public Element toXML(Document doc) {
      Element responseElem = doc.createElement(PACKAGE);
      responseElem.setAttribute(NAME, name);
      responseElem.setAttribute(HASH, hash);
      responseElem.setAttribute(REQUIRED, required.toString());
      responseElem.setAttribute(SIZE, size.toString());
      
      return responseElem;
   }
}
