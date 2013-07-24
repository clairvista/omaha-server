package com.clairvista.liveexpert.omaha.server.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Element;

import com.clairvista.liveexpert.omaha.server.constants.PingAttrs;
import com.clairvista.liveexpert.omaha.server.dao.PingDAO;
import com.clairvista.liveexpert.omaha.server.model.ApplicationVersionRequest;
import com.clairvista.liveexpert.omaha.server.model.Ping;
import com.clairvista.liveexpert.omaha.server.util.XMLUtils;

@Service
@Transactional
public class PingServiceImpl implements PingService {

   @SuppressWarnings("unused")
   private static Logger LOGGER = Logger.getLogger(PingServiceImpl.class);

   @Autowired
   private PingDAO pingDAO;
   
   public Ping recordPing(ApplicationVersionRequest appRequest, Element pingElem) {
      // Extract Inputs:
      Boolean wasActive = XMLUtils.parseBoolean(pingElem, PingAttrs.WAS_ACTIVE);
      Integer lastActive = XMLUtils.parseInteger(pingElem, PingAttrs.LAST_ACTIVE);
      Integer lastPresent = XMLUtils.parseInteger(pingElem, PingAttrs.LAST_PRESENT);
      
      // Create Event:
      Ping ping = new Ping();

      ping.setApplicationVersionRequest(appRequest);
      ping.setWasActive(wasActive);
      ping.setLastActive(lastActive);
      ping.setLastPresent(lastPresent);
      
      pingDAO.addPing(ping);
      
      return ping;
   }

}
