package com.clairvista.liveexpert.omaha.server.response;

import static com.clairvista.liveexpert.omaha.server.constants.APIElementNames.PACKAGES;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class PackagesResponse {

   private List<PackageResponse> packages;

   public PackagesResponse() {
      packages = new ArrayList<PackageResponse>();
   }
   
   public void addPackage(PackageResponse packageResponse) {
      packages.add(packageResponse);
   }

   public Element toXML(Document doc) {
      Element responseElem = doc.createElement(PACKAGES);
      
      for(PackageResponse packageResponse : packages) {
         Element packageElem = packageResponse.toXML(doc);
         responseElem.appendChild(packageElem);
      }
      
      return responseElem;
   }
}
