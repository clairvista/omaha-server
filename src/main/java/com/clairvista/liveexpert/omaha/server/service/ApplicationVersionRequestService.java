package com.clairvista.liveexpert.omaha.server.service;

import java.util.List;

import org.w3c.dom.Element;

import com.clairvista.liveexpert.omaha.server.model.ApplicationVersion;
import com.clairvista.liveexpert.omaha.server.model.ApplicationVersionRequest;
import com.clairvista.liveexpert.omaha.server.model.Request;
import com.clairvista.liveexpert.omaha.server.response.ApplicationResponse;

public interface ApplicationVersionRequestService {

   public boolean validateApplicationVersionRequest(Element appElem);
   public ApplicationVersionRequest recordApplicationVersionRequest(Request request, 
         ApplicationVersion appVersion, Element appElem);
   public ApplicationResponse processApplicationVersionRequest(ApplicationVersionRequest appRequest, 
         List<Element> actionElems);

}
