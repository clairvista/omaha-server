package com.clairvista.liveexpert.omaha.server.service;

import org.w3c.dom.Element;

import com.clairvista.liveexpert.omaha.server.model.OperatingSystem;
import com.clairvista.liveexpert.omaha.server.model.Request;

public interface OperatingSystemService {

   public boolean validateOperatingSystem(Element operatingSystemElem);
   public OperatingSystem recordOperatingSystem(Request request, Element operatingSystemElem);

}
