package com.clairvista.liveexpert.omaha.server.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.clairvista.liveexpert.omaha.server.constants.OperatingSystemAttrs;
import com.clairvista.liveexpert.omaha.server.constants.RequestElementNames;
import com.clairvista.liveexpert.omaha.server.constants.RequestAttrs;
import com.clairvista.liveexpert.omaha.server.dao.ClientVersionDAO;
import com.clairvista.liveexpert.omaha.server.dao.OperatingSystemDAO;
import com.clairvista.liveexpert.omaha.server.dao.ProtocolDAO;
import com.clairvista.liveexpert.omaha.server.dao.RequestDAO;
import com.clairvista.liveexpert.omaha.server.dao.SessionDAO;
import com.clairvista.liveexpert.omaha.server.dao.UserDAO;
import com.clairvista.liveexpert.omaha.server.model.ClientVersion;
import com.clairvista.liveexpert.omaha.server.model.OperatingSystem;
import com.clairvista.liveexpert.omaha.server.model.Protocol;
import com.clairvista.liveexpert.omaha.server.model.Request;
import com.clairvista.liveexpert.omaha.server.model.Session;
import com.clairvista.liveexpert.omaha.server.model.User;
import com.clairvista.liveexpert.omaha.server.util.DomUtils;

@Service
@Transactional
public class OperatingSystemServiceImpl implements OperatingSystemService {
   
   private static final String[] REQUIRED_OPERATING_SYSTEM_ATTRIBUTES = new String[]
         { OperatingSystemAttrs.PLATFORM, OperatingSystemAttrs.VERSION };

   private static Logger LOGGER = Logger.getLogger(OperatingSystemServiceImpl.class);

   @Autowired
   private OperatingSystemDAO operatingSystemDAO;

   public boolean validateOperatingSystem(Element operatingSystemElem) {
      List<String> missingAttributes = 
            DomUtils.validateRequiredAttributes(operatingSystemElem, REQUIRED_OPERATING_SYSTEM_ATTRIBUTES);
      if(!missingAttributes.isEmpty()) {
         LOGGER.warn("INVALID REQUEST -- Missing required operating system attributes. " +
         		"Missing attributes: " + missingAttributes);
         return false;
      }
      
      return false;
   }
   
   @SuppressWarnings("unused")
   public Request recordOperatingSystem(Request request, Element operatingSystemElem) {
      // Validate Inputs:
      if(!validateOperatingSystem(operatingSystemElem)) {
         return null;
      }
      
      // Extract Inputs:
      String platform = DomUtils.parseString(operatingSystemElem, OperatingSystemAttrs.PLATFORM);
      String version = DomUtils.parseString(operatingSystemElem, OperatingSystemAttrs.VERSION);
      String servicePack = DomUtils.parseString(operatingSystemElem, OperatingSystemAttrs.SERVICE_PACK);
      String architecture = DomUtils.parseString(operatingSystemElem, OperatingSystemAttrs.ARCHITECTURE);
      
      // Find/Create Operating System:
      OperatingSystem operatingSystem = operatingSystemDAO.findOrCreate(platform, version, servicePack, architecture);
      
      return request;
   }

}
