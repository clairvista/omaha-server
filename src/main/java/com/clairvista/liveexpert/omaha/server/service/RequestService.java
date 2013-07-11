package com.clairvista.liveexpert.omaha.server.service;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.clairvista.liveexpert.omaha.server.model.Request;

public interface RequestService {

   public boolean validateRequest(Element requestElem);
   public Request recordRequest(Element requestElem);
   public boolean processRequest(Request request, NodeList appElems);

}
