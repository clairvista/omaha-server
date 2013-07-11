package com.clairvista.liveexpert.omaha.server.controller;

import java.io.IOException;
import java.io.StringReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.clairvista.liveexpert.omaha.server.constants.RequestElementNames;
import com.clairvista.liveexpert.omaha.server.model.Request;
import com.clairvista.liveexpert.omaha.server.service.RequestService;
import com.clairvista.liveexpert.omaha.server.util.ServletUtils;

@Controller
public class UpdateServlet {
   
   private static Logger LOGGER = Logger.getLogger(UpdateServlet.class);

   @Autowired
   private RequestService requestService;
   
   @RequestMapping(value = "/update", method = RequestMethod.POST)
   public @ResponseBody String processOmahaRequest(HttpServletRequest httpRequest, HttpServletResponse response) 
         throws ServletException, IOException {
      String postString = ServletUtils.getRawPostData(httpRequest);
      LOGGER.debug("Post data provided: " + postString);
      
      // Process request input data.
      Document doc = null;
      try {
         DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
         InputSource postInput = new InputSource(new StringReader(postString));
         doc = docBuilder.parse(postInput);
      } catch (ParserConfigurationException pce) {
         LOGGER.error("SERVER ERROR -- Failed to initialize XML Parser.", pce);
         response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
         return "error";
      } catch (SAXException saxe) {
         LOGGER.warn("INVALID REQUEST -- Failed to parse input: " + postString, saxe);
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         return "invalidRequest";
      } catch (IOException ioe) {
         LOGGER.warn("INVALID REQUEST -- Failed to parse input: " + postString, ioe);
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         return "invalidRequest";
      }
      
      if(doc == null) {
         LOGGER.error("SERVER ERROR -- Failed to create parse input.");
         response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
         return "error";
      }
      
      Element requestElem = doc.getDocumentElement();
      
      // Record request.
      Request omahaRequest = requestService.recordRequest(requestElem);

      if(omahaRequest == null) {
         LOGGER.warn("Request creation failed for input: " + postString);
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         return "invalidRequest";
      }
      
      // Record operating system.
      
      // Process application directives.
      requestService.processRequest(omahaRequest, requestElem.getElementsByTagName(RequestElementNames.APPLICATION));
      
      
      response.setStatus(HttpServletResponse.SC_OK);
      return "success: " + postString;
   }
   
}
