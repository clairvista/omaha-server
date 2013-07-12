package com.clairvista.liveexpert.omaha.server.service;

import org.w3c.dom.Element;

import com.clairvista.liveexpert.omaha.server.model.Application;
import com.clairvista.liveexpert.omaha.server.model.ApplicationVersion;
import com.clairvista.liveexpert.omaha.server.model.Request;
import com.clairvista.liveexpert.omaha.server.response.ApplicationResponse;

public interface ApplicationService {

   public Application lookupApplication(Element appElem);
   public ApplicationVersion lookupApplicationVersion(Application app, Element appElem);
   public ApplicationResponse processApplication(Application app, Request request, Element appElem);

}
