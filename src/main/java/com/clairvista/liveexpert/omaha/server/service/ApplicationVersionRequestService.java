package com.clairvista.liveexpert.omaha.server.service;

import java.util.List;

import org.w3c.dom.Element;

import com.clairvista.liveexpert.omaha.server.model.ApplicationVersion;
import com.clairvista.liveexpert.omaha.server.model.ApplicationVersionRequest;
import com.clairvista.liveexpert.omaha.server.model.Request;
import com.clairvista.liveexpert.omaha.server.response.ApplicationResponse;
import com.clairvista.liveexpert.omaha.server.util.RequestElementValidationException;

public interface ApplicationVersionRequestService {

   public boolean validateApplicationVersionRequest(Element appElem) throws RequestElementValidationException;
   public ApplicationVersionRequest recordApplicationVersionRequest(Request request, 
         ApplicationVersion appVersion, Element appElem) throws RequestElementValidationException;
   public ApplicationResponse processApplicationVersionRequest(ApplicationVersionRequest appRequest, 
         List<Element> actionElems);

}
