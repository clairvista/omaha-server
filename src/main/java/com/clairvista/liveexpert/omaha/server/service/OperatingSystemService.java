package com.clairvista.liveexpert.omaha.server.service;

import org.w3c.dom.Element;

import com.clairvista.liveexpert.omaha.server.model.OperatingSystem;
import com.clairvista.liveexpert.omaha.server.model.Request;
import com.clairvista.liveexpert.omaha.server.util.RequestElementValidationException;

public interface OperatingSystemService {

   public boolean validateOperatingSystem(Element operatingSystemElem) 
         throws RequestElementValidationException;
   public OperatingSystem recordOperatingSystem(Request request, Element operatingSystemElem) 
         throws RequestElementValidationException;

}
