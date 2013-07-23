package com.clairvista.liveexpert.omaha.server.service;

import java.util.List;

import org.w3c.dom.Element;

import com.clairvista.liveexpert.omaha.server.model.Request;
import com.clairvista.liveexpert.omaha.server.response.ResponseRoot;
import com.clairvista.liveexpert.omaha.server.util.RequestElementValidationException;

public interface RequestService {

   public boolean validateRequest(Element requestElem) throws RequestElementValidationException;
   public Request recordRequest(Element requestElem) throws RequestElementValidationException;
   public ResponseRoot processRequest(Request request, List<Element> appElems);

}
