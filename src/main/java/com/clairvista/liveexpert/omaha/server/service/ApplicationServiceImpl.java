package com.clairvista.liveexpert.omaha.server.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import com.clairvista.liveexpert.omaha.server.constants.ApplicationAttrs;
import com.clairvista.liveexpert.omaha.server.dao.ApplicationDAO;
import com.clairvista.liveexpert.omaha.server.dao.ApplicationVersionDAO;
import com.clairvista.liveexpert.omaha.server.model.Application;
import com.clairvista.liveexpert.omaha.server.model.ApplicationVersion;
import com.clairvista.liveexpert.omaha.server.model.ApplicationVersionRequest;
import com.clairvista.liveexpert.omaha.server.model.Request;
import com.clairvista.liveexpert.omaha.server.response.ApplicationResponse;
import com.clairvista.liveexpert.omaha.server.util.RequestElementValidationException;
import com.clairvista.liveexpert.omaha.server.util.XMLUtils;

@Service
@Transactional
public class ApplicationServiceImpl implements ApplicationService {

   private static Logger LOGGER = Logger.getLogger(ApplicationServiceImpl.class);

   @Autowired
   private ApplicationDAO applicationDAO;

   @Autowired
   private ApplicationVersionDAO applicationVersionDAO;
   
   @Autowired
   private ApplicationVersionRequestService applicationVersionRequestService;

   public Application lookupApplication(Element appElem) {
      // Extract Application ID:
      String applicationID = XMLUtils.parseString(appElem, ApplicationAttrs.APPLICATION_ID);
      if(applicationID == null) {
         LOGGER.warn("Application element missing application ID attribute." +
               " Application content: " + XMLUtils.elementToString(appElem));
         return null;
      }
      
      // Lookup Application:
      Application app = applicationDAO.findApplication(applicationID);
      if(app == null) {
         LOGGER.warn("No application found with Application ID: " + applicationID +
               " Application content: " + XMLUtils.elementToString(appElem));
         return null;
      }
      
      return app;
   }

   public ApplicationVersion lookupApplicationVersion(Application app, Element appElem) {
      String version = XMLUtils.parseString(appElem, ApplicationAttrs.VERSION);
      if(version == null) {
         version = "0.0.0.0";
      }
      
      return applicationVersionDAO.findByVersionForApplication(app, version);
   }

   public ApplicationResponse processApplication(Application app, Request request, Element appElem) {
      ApplicationResponse appResponse = new ApplicationResponse(app.getAppID());

      // Lookup Application Version:
      ApplicationVersion appVersion = lookupApplicationVersion(app, appElem);
      if(appVersion == null) {
         LOGGER.warn("Version not found for application. Application content: " + XMLUtils.elementToString(appElem));
         appResponse.setStatus("error-unknownVersion");
         return appResponse;
      }

      // Create Application Version Request:
      ApplicationVersionRequest appRequest = null;
      try {
         appRequest = applicationVersionRequestService.recordApplicationVersionRequest(request, appVersion, appElem);
      } catch(RequestElementValidationException reve) {
         LOGGER.warn("Request validation failure.", reve);
         appResponse.setStatus("error-validationFailure");
         appResponse.setErrorDetails(reve.getResponseErrorDetails());
         return appResponse;
      }
      if(appRequest == null) {
         LOGGER.error("Failed to record application request." +
               " Full application content: " + XMLUtils.elementToString(appElem));
         appResponse.setStatus("error-serverFailure");
         return appResponse;
      }

      // Process Application Version Request:
      List<Element> actions = DomUtils.getChildElements(appElem);
      return applicationVersionRequestService.processApplicationVersionRequest(appRequest, actions);
   }

}
