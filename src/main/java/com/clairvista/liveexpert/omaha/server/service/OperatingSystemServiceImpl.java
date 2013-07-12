package com.clairvista.liveexpert.omaha.server.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Element;

import com.clairvista.liveexpert.omaha.server.constants.OperatingSystemAttrs;
import com.clairvista.liveexpert.omaha.server.dao.OperatingSystemDAO;
import com.clairvista.liveexpert.omaha.server.dao.RequestDAO;
import com.clairvista.liveexpert.omaha.server.dao.UserDAO;
import com.clairvista.liveexpert.omaha.server.model.OperatingSystem;
import com.clairvista.liveexpert.omaha.server.model.Request;
import com.clairvista.liveexpert.omaha.server.model.User;
import com.clairvista.liveexpert.omaha.server.util.XMLUtils;

@Service
@Transactional
public class OperatingSystemServiceImpl implements OperatingSystemService {
   
   private static final String[] REQUIRED_OPERATING_SYSTEM_ATTRIBUTES = new String[]
         { OperatingSystemAttrs.PLATFORM, OperatingSystemAttrs.VERSION };

   private static Logger LOGGER = Logger.getLogger(OperatingSystemServiceImpl.class);

   @Autowired
   private OperatingSystemDAO operatingSystemDAO;
   
   @Autowired
   private RequestDAO requestDAO;

   @Autowired
   private UserDAO userDAO;

   public boolean validateOperatingSystem(Element operatingSystemElem) {
      List<String> missingAttributes = 
            XMLUtils.validateRequiredAttributes(operatingSystemElem, REQUIRED_OPERATING_SYSTEM_ATTRIBUTES);
      if(!missingAttributes.isEmpty()) {
         LOGGER.warn("INVALID REQUEST -- Missing required operating system attributes. " +
         		"Missing attributes: " + missingAttributes);
         return false;
      }
      
      return true;
   }
   
   public OperatingSystem recordOperatingSystem(Request request, Element operatingSystemElem) {
      // Validate Inputs:
      if(!validateOperatingSystem(operatingSystemElem)) {
         return null;
      }
      
      // Extract Inputs:
      String platform = XMLUtils.parseString(operatingSystemElem, OperatingSystemAttrs.PLATFORM);
      String version = XMLUtils.parseString(operatingSystemElem, OperatingSystemAttrs.VERSION);
      String servicePack = XMLUtils.parseString(operatingSystemElem, OperatingSystemAttrs.SERVICE_PACK);
      String architecture = XMLUtils.parseString(operatingSystemElem, OperatingSystemAttrs.ARCHITECTURE);
      
      // Find/Create Operating System:
      OperatingSystem operatingSystem = 
            operatingSystemDAO.findOrCreateOperatingSystem(platform, version, servicePack, architecture);
      
      if(operatingSystem == null) {
         LOGGER.error("Failed to find or create operating system with platform: " + platform + 
               " and version: " + version + " and service pack: " + servicePack +
               " and architecture: " + architecture);
         return null;
      }
      
      // Set the Request association.
      request.setOperatingSystem(operatingSystem);
      requestDAO.updateRequest(request);
      
      // Update the User association, if necessary.
      User user = request.getUser();
      if(user != null) {
         OperatingSystem currentOperatingSystem = user.getOperatingSystem();
         if(currentOperatingSystem != null && currentOperatingSystem.getId() != operatingSystem.getId()) {
            LOGGER.warn("Change of Operating System detected for user " + user.getId() + ". " +
            		"Changed from record " + currentOperatingSystem.getId() + " to record " + operatingSystem.getId());

            user.setOperatingSystem(operatingSystem);
            userDAO.updateUser(user);
         }
      }
      
      return operatingSystem;
   }

}
