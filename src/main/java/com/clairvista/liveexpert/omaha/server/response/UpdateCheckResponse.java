package com.clairvista.liveexpert.omaha.server.response;

import static com.clairvista.liveexpert.omaha.server.constants.APIElementNames.UPDATE_CHECK;
import static com.clairvista.liveexpert.omaha.server.constants.ApplicationAttrs.STATUS;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class UpdateCheckResponse extends AppChildResponse {

   private UrlsResponse urls;
   private ManifestResponse manifest;
   
   public UpdateCheckResponse() {
      super();
      this.urls = new UrlsResponse();
   }
   
   public void setStatus(String status) {
      this.status = status;
   }
   
   public void addUrl(UrlResponse url) {
      this.urls.addUrl(url);
   }
   
   public void setManifest(ManifestResponse manifest) {
      this.manifest = manifest;
   }

   @Override
   public Element toXML(Document doc) {
      Element responseElem = doc.createElement(UPDATE_CHECK);
      responseElem.setAttribute(STATUS, status);
      
      if(urls != null) {
         Element urlsElem = urls.toXML(doc);
         responseElem.appendChild(urlsElem);
      }
      
      if(manifest != null) {
         Element manifestElem = manifest.toXML(doc);
         responseElem.appendChild(manifestElem);
      }
      
      return responseElem;
   }

}
