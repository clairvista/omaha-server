package com.clairvista.liveexpert.omaha.server.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.clairvista.liveexpert.omaha.server.constants.RequestElementNames;
import com.clairvista.liveexpert.omaha.server.constants.RequestAttrs;
import com.clairvista.liveexpert.omaha.server.dao.ClientVersionDAO;
import com.clairvista.liveexpert.omaha.server.dao.ProtocolDAO;
import com.clairvista.liveexpert.omaha.server.dao.RequestDAO;
import com.clairvista.liveexpert.omaha.server.dao.SessionDAO;
import com.clairvista.liveexpert.omaha.server.dao.UserDAO;
import com.clairvista.liveexpert.omaha.server.model.ClientVersion;
import com.clairvista.liveexpert.omaha.server.model.Protocol;
import com.clairvista.liveexpert.omaha.server.model.Request;
import com.clairvista.liveexpert.omaha.server.model.Session;
import com.clairvista.liveexpert.omaha.server.model.User;
import com.clairvista.liveexpert.omaha.server.util.DomUtils;

@Service
@Transactional
public class RequestServiceImpl implements RequestService {
   
   private static final String[] REQUIRED_REQUEST_ATTRIBUTES = new String[]
         { RequestAttrs.PROTOCOL, RequestAttrs.VERSION, RequestAttrs.IS_MACHINE, 
           RequestAttrs.REQUEST_ID, RequestAttrs.SESSION_ID };

   private static Logger LOGGER = Logger.getLogger(RequestServiceImpl.class);

   @Autowired
   private RequestDAO appDAO;

   @Autowired
   private ProtocolDAO protocolDAO;

   @Autowired
   private ClientVersionDAO clientVersionDAO;
   
   @Autowired
   private SessionDAO sessionDAO;

   @Autowired
   private RequestDAO requestDAO;

   @Autowired
   private UserDAO userDAO;
   
   public boolean validateRequest(Element requestElem) {
      if(!RequestElementNames.REQUEST.equals(requestElem.getNodeName())) {
         LOGGER.warn("INVALID REQUEST -- Request not properly named. Name provided: " + requestElem);
         return false;
      }
      
      List<String> missingAttributes = DomUtils.validateRequiredAttributes(requestElem, REQUIRED_REQUEST_ATTRIBUTES);
      if(!missingAttributes.isEmpty()) {
         LOGGER.warn("INVALID REQUEST -- Missing required request attributes. " +
         		"Missing attributes: " + missingAttributes);
         return false;
      }
      
      String requestProtocol = DomUtils.parseString(requestElem, RequestAttrs.PROTOCOL);
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
      String protocolID = DomUtils.parseString(requestElem, RequestAttrs.PROTOCOL);
      String clientVersionID = DomUtils.parseString(requestElem,  RequestAttrs.VERSION);
      Boolean isMachine = DomUtils.parseBoolean(requestElem, RequestAttrs.IS_MACHINE);
      String requestID = DomUtils.parseString(requestElem,  RequestAttrs.REQUEST_ID);
      String sessionID = DomUtils.parseString(requestElem,  RequestAttrs.SESSION_ID);
      String userID = DomUtils.parseString(requestElem,  RequestAttrs.USER_ID);
      String installSource = DomUtils.parseString(requestElem,  RequestAttrs.INSTALL_SOURCE);
      String originURL = DomUtils.parseString(requestElem,  RequestAttrs.ORIGIN_URL);
      String testSource = DomUtils.parseString(requestElem,  RequestAttrs.TEST_SOURCE);
      String dedup = DomUtils.parseString(requestElem,  RequestAttrs.DEDUP);

      // Construct/Identify Associations:
      Protocol protocol = protocolDAO.getProtocol(protocolID);
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

      User user = userDAO.findOrCreateUser(userID);
      if(user == null) {
         LOGGER.error("Failed to find or create user with ID: " + userID);
         return null;
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

   public boolean processRequest(Request request, NodeList appElems) {
      // TODO Auto-generated method stub
      return true;
   }

}
