package com.clairvista.liveexpert.omaha.server.service;

import java.util.List;

import org.w3c.dom.Element;

import com.clairvista.liveexpert.omaha.server.model.Request;
import com.clairvista.liveexpert.omaha.server.response.ResponseRoot;

public interface RequestService {

   public boolean validateRequest(Element requestElem);
   public Request recordRequest(Element requestElem);
   public ResponseRoot processRequest(Request request, List<Element> appElems);

}
