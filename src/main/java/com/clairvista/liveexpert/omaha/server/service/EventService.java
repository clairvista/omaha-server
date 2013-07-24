package com.clairvista.liveexpert.omaha.server.service;

import org.w3c.dom.Element;

import com.clairvista.liveexpert.omaha.server.model.ApplicationVersionRequest;
import com.clairvista.liveexpert.omaha.server.model.Event;
import com.clairvista.liveexpert.omaha.server.util.RequestElementValidationException;

public interface EventService {

   public boolean validateEvent(Element eventElem) throws RequestElementValidationException;
   public Event recordEvent(ApplicationVersionRequest appRequest, Element eventElem)
         throws RequestElementValidationException;

}
