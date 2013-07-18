package com.clairvista.liveexpert.omaha.server.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Element;

import com.clairvista.liveexpert.omaha.server.constants.APIElementNames;
import com.clairvista.liveexpert.omaha.server.constants.ApplicationAttrs;
import com.clairvista.liveexpert.omaha.server.constants.RequestAttrs;
import com.clairvista.liveexpert.omaha.server.dao.ApplicationDAO;
import com.clairvista.liveexpert.omaha.server.dao.ClientVersionDAO;
import com.clairvista.liveexpert.omaha.server.dao.ProtocolDAO;
import com.clairvista.liveexpert.omaha.server.dao.RequestDAO;
import com.clairvista.liveexpert.omaha.server.dao.SessionDAO;
import com.clairvista.liveexpert.omaha.server.dao.UserDAO;
import com.clairvista.liveexpert.omaha.server.model.Application;
import com.clairvista.liveexpert.omaha.server.model.ClientVersion;
import com.clairvista.liveexpert.omaha.server.model.Protocol;
import com.clairvista.liveexpert.omaha.server.model.Request;
import com.clairvista.liveexpert.omaha.server.model.Session;
import com.clairvista.liveexpert.omaha.server.model.User;
import com.clairvista.liveexpert.omaha.server.response.ApplicationResponse;
import com.clairvista.liveexpert.omaha.server.response.ResponseRoot;
import com.clairvista.liveexpert.omaha.server.util.XMLUtils;

@Service
@Transactional
public class RequestServiceImpl implements RequestService {
   
   private static final String[] REQUIRED_REQUEST_ATTRIBUTES = new String[]
         { RequestAttrs.PROTOCOL, RequestAttrs.VERSION, RequestAttrs.IS_MACHINE, 
           RequestAttrs.REQUEST_ID, RequestAttrs.SESSION_ID };

   private static Logger LOGGER = Logger.getLogger(RequestServiceImpl.class);

   @Autowired
   private RequestDAO requestDAO;

   @Autowired
   private ProtocolDAO protocolDAO;

   @Autowired
   private ClientVersionDAO clientVersionDAO;
   
   @Autowired
   private SessionDAO sessionDAO;
   
   @Autowired
   private UserDAO userDAO;

   @Autowired
   private ApplicationDAO applicationDAO;
   
   @Autowired
   private ApplicationService applicationService;
   
   public boolean validateRequest(Element requestElem) {
      if(!APIElementNames.REQUEST.equals(requestElem.getNodeName())) {
         LOGGER.warn("INVALID REQUEST -- Request not properly named. Name provided: " + requestElem);
         return false;
      }
      
      List<String> missingAttributes = XMLUtils.validateRequiredAttributes(requestElem, REQUIRED_REQUEST_ATTRIBUTES);
      if(!missingAttributes.isEmpty()) {
         LOGGER.warn("INVALID REQUEST -- Missing required request attributes. " +
         		"Missing attributes: " + missingAttributes);
         return false;
      }
      
      String requestProtocol = XMLUtils.parseString(requestElem, RequestAttrs.PROTOCOL);
      if(requestProtocol == null || !"3.0".equals(requestProtocol)) {
         LOGGER.warn("INVALID REQUEST -- Unsupported request protocol. Protocol provided: " + requestProtocol);
         return false;
      }
      
      return true;
   }

   @SuppressWarnings("unused")
   public Request recordRequest(Element requestElem) {
      // Validate Inputs:
      if(!validateRequest(requestElem)) {
         return null;
      }
      
      // Extract Inputs:
      String protocolID = XMLUtils.parseString(requestElem, RequestAttrs.PROTOCOL);
      String clientVersionID = XMLUtils.parseString(requestElem,  RequestAttrs.VERSION);
      Boolean isMachine = XMLUtils.parseBoolean(requestElem, RequestAttrs.IS_MACHINE);
      String requestID = XMLUtils.parseString(requestElem,  RequestAttrs.REQUEST_ID);
      String sessionID = XMLUtils.parseString(requestElem,  RequestAttrs.SESSION_ID);
      String userID = XMLUtils.parseString(requestElem,  RequestAttrs.USER_ID);
      String installSource = XMLUtils.parseString(requestElem,  RequestAttrs.INSTALL_SOURCE);
      String originURL = XMLUtils.parseString(requestElem,  RequestAttrs.ORIGIN_URL);
      String testSource = XMLUtils.parseString(requestElem,  RequestAttrs.TEST_SOURCE);
      String dedup = XMLUtils.parseString(requestElem,  RequestAttrs.DEDUP);

      // Construct/Identify Associations:
      Protocol protocol = protocolDAO.findProtocol(protocolID);
      if(protocol == null) {
         LOGGER.error("Failed to find protocol with ID: " + protocolID);
         return null;
      }

      ClientVersion clientVersion = clientVersionDAO.findOrCreateClientVersion(clientVersionID);
      if(clientVersion == null) {
         LOGGER.error("Failed to find or create client version with ID: " + clientVersionID);
         return null;
      }

      Session session = sessionDAO.findOrCreateSession(sessionID);
      if(session == null) {
         LOGGER.error("Failed to find or create session with ID: " + sessionID);
         return null;
      }
      
      User user = null;
      if(userID != null) {
         user = userDAO.findOrCreateUser(userID);
         if(user == null) {
            LOGGER.error("Failed to find or create user with ID: " + userID);
            return null;
         }
      }
      
      // Create Request:
      Request request = new Request();
      
      request.setRequestID(requestID);
      request.setProtocol(protocol);
      request.setClientVersion(clientVersion);
      request.setIsMachine(isMachine);
      request.setSession(session);
      request.setUser(user);
      request.setInstallSource(installSource);
      request.setOriginURL(originURL);
      
      requestDAO.addRequest(request);
      
      return request;
   }

   public ResponseRoot processRequest(Request request, List<Element> appElems) {
      // Construct Response:
      ResponseRoot response = new ResponseRoot("3.0", "todo");
      
      for(Element appElem : appElems) {
         // Lookup Application:
         Application app = applicationService.lookupApplication(appElem);
         ApplicationResponse appResponse;
         if(app == null) {
            LOGGER.warn("No application found. Application content: " + XMLUtils.elementToString(appElem));
            appResponse = new ApplicationResponse(appElem.getAttribute(ApplicationAttrs.APPLICATION_ID));
            appResponse.setStatus("error-unknownApplication");
         } else {
            appResponse = applicationService.processApplication(app, request, appElem);
         }
         
         response.addApplication(appResponse);
      }
      
      return response;
   }

}
