package com.clairvista.liveexpert.omaha.server.service;

import org.w3c.dom.Element;

import com.clairvista.liveexpert.omaha.server.model.ApplicationVersionRequest;
import com.clairvista.liveexpert.omaha.server.model.Ping;

public interface PingService {

   public Ping recordPing(ApplicationVersionRequest appRequest, Element pingElem);

}
