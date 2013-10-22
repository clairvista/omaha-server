package com.clairvista.liveexpert.omaha.server.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Element;

import com.clairvista.liveexpert.omaha.server.constants.APIElementNames;
import com.clairvista.liveexpert.omaha.server.constants.ApplicationAttrs;
import com.clairvista.liveexpert.omaha.server.dao.ApplicationVersionRequestDAO;
import com.clairvista.liveexpert.omaha.server.model.ApplicationVersion;
import com.clairvista.liveexpert.omaha.server.model.ApplicationVersionRequest;
import com.clairvista.liveexpert.omaha.server.model.Request;
import com.clairvista.liveexpert.omaha.server.model.UpdateCheck;
import com.clairvista.liveexpert.omaha.server.response.AppChildResponse;
import com.clairvista.liveexpert.omaha.server.response.ApplicationResponse;
import com.clairvista.liveexpert.omaha.server.response.EventResponse;
import com.clairvista.liveexpert.omaha.server.response.PingResponse;
import com.clairvista.liveexpert.omaha.server.response.UnknownResponse;
import com.clairvista.liveexpert.omaha.server.util.RequestElementValidationException;
import com.clairvista.liveexpert.omaha.server.util.XMLUtils;

@Service
@Transactional
public class ApplicationVersionRequestServiceImpl implements ApplicationVersionRequestService {
   
   private static Logger LOGGER = Logger.getLogger(ApplicationVersionRequestServiceImpl.class);

   @Autowired
   private ApplicationVersionRequestDAO appVersionRequestDAO;
   
   @Autowired
   private PingService pingService;
   
   @Autowired
   private EventService eventService;
   
   @Autowired
   private UpdateCheckService updateCheckService;
   
   public boolean validateApplicationVersionRequest(Element appElem) throws RequestElementValidationException {
      return true;
   }

   public ApplicationVersionRequest recordApplicationVersionRequest(Request request,
         ApplicationVersion appVersion, Element appElem) throws RequestElementValidationException {
      // Validate Inputs:
      if(!validateApplicationVersionRequest(appElem)) {
         throw new RequestElementValidationException("Application Version Request validation failed.",
               "appValidationFailed");
      }

      // Extract Inputs:
      String nextVersion = XMLUtils.parseString(appElem, ApplicationAttrs.NEXT_VERSION);
      String language = XMLUtils.parseString(appElem, ApplicationAttrs.LANGUAGE);
      String brand= XMLUtils.parseString(appElem, ApplicationAttrs.BRAND);
      String client = XMLUtils.parseString(appElem, ApplicationAttrs.CLIENT);
      String additionalParams = XMLUtils.parseString(appElem, ApplicationAttrs.ADDITIONAL_PARAMS);
      String experiments = XMLUtils.parseString(appElem, ApplicationAttrs.EXPERIMENTS);
      String installID = XMLUtils.parseString(appElem, ApplicationAttrs.INSTALL_ID);
      Integer installAge = XMLUtils.parseInteger(appElem, ApplicationAttrs.INSTALL_AGE);

      // Create Application Version Request:
      ApplicationVersionRequest appRequest = new ApplicationVersionRequest();
      
      appRequest.setRequest(request);
      appRequest.setApplicationVersion(appVersion);
      appRequest.setNextVersion(nextVersion);
      appRequest.setLanguage(language);
      appRequest.setBrand(brand);
      appRequest.setClient(client);
      appRequest.setAdditionalParams(additionalParams);
      appRequest.setExperiments(experiments);
      appRequest.setInstallID(installID);
      appRequest.setInstallAge(installAge);
      
      appVersionRequestDAO.addApplicationVersionRequest(appRequest);
      
      return appRequest;
   }

   public ApplicationResponse processApplicationVersionRequest(ApplicationVersionRequest appRequest,
         List<Element> actionElems) {
      // Initialize Response:
      ApplicationVersion appVersion = appRequest.getApplicationVersion();
      ApplicationResponse response;
      if(appVersion != null) {
         response = new ApplicationResponse(appVersion.getApplication().getAppID());
      } else {
         response = new ApplicationResponse("");
      }

      // Validate Request:
      if(actionElems.isEmpty()) {
         response.setStatus("error-noActionsRequested");
         return response;
      }
      
      // Process Request:
      for(Element actionElem : actionElems) {
         AppChildResponse actionResponse = processAction(appRequest, actionElem);
         response.addActionResponse(actionResponse);
      }
      
      return response;
   }

   private AppChildResponse processAction(ApplicationVersionRequest appRequest, Element actionElem) {
      String actionName = actionElem.getNodeName();
      AppChildResponse response;

      if(APIElementNames.PING.equals(actionName)) {
         pingService.recordPing(appRequest, actionElem);
         response = new PingResponse();
      } else if(APIElementNames.EVENT.equals(actionName)) {
         response = new EventResponse();
         try {
            eventService.recordEvent(appRequest, actionElem);
         } catch (RequestElementValidationException reve) {
            LOGGER.warn("Event validation failure.", reve);
            response.setStatus("error-validationFailure");
            response.setErrorDetails(reve.getResponseErrorDetails());
         }
      } else if(APIElementNames.UPDATE_CHECK.equals(actionName)) {
         UpdateCheck updateCheck = updateCheckService.recordUpdateCheck(appRequest, actionElem);
         response = updateCheckService.processUpdateCheck(updateCheck);
      } else {
         LOGGER.warn("Received unknown action element: " + actionName);
         response = new UnknownResponse();
      }
      
      return response;
   }

}
