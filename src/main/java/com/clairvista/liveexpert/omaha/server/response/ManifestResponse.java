package com.clairvista.liveexpert.omaha.server.response;

import static com.clairvista.liveexpert.omaha.server.constants.APIElementNames.MANIFEST;
import static com.clairvista.liveexpert.omaha.server.constants.ManifestAttrs.VERSION;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ManifestResponse {

   private String version;
   private PackagesResponse packages;
   private ActionsResponse actions;
   
   public ManifestResponse(String version) {
      this.version = version;
   }
   
   public void addPackage(PackageResponse packageResponse) {
      packages.addPackage(packageResponse);
   }
   
   public void addAction(ActionResponse action) {
      actions.addAction(action);
   }

   public Element toXML(Document doc) {
      Element responseElem = doc.createElement(MANIFEST);
      responseElem.setAttribute(VERSION, version);

      if(packages != null) {
         Element packagesElem = packages.toXML(doc);
         responseElem.appendChild(packagesElem);
      }
      
      if(actions != null) {
         Element actionsElem = actions.toXML(doc);
         responseElem.appendChild(actionsElem);
      }
      
      return responseElem;
   }

}
