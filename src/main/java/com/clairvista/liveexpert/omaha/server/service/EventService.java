package com.clairvista.liveexpert.omaha.server.service;

import org.w3c.dom.Element;

import com.clairvista.liveexpert.omaha.server.model.ApplicationVersionRequest;
import com.clairvista.liveexpert.omaha.server.model.Event;

public interface EventService {

   public boolean validateEvent(Element eventElem);
   public Event recordEvent(ApplicationVersionRequest appRequest, Element eventElem);

}
