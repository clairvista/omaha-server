package com.clairvista.liveexpert.omaha.server.service;

import org.w3c.dom.Element;

import com.clairvista.liveexpert.omaha.server.model.Request;

public interface OperatingSystemService {

   public boolean validateOperatingSystem(Element operatingSystemElem);
   public Request recordOperatingSystem(Request request, Element operatingSystemElem);

}
