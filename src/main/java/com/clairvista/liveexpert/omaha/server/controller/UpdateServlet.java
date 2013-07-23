package com.clairvista.liveexpert.omaha.server.controller;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.xml.DomUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.clairvista.liveexpert.omaha.server.constants.APIElementNames;
import com.clairvista.liveexpert.omaha.server.model.Request;
import com.clairvista.liveexpert.omaha.server.response.ResponseRoot;
import com.clairvista.liveexpert.omaha.server.service.OperatingSystemService;
import com.clairvista.liveexpert.omaha.server.service.RequestService;
import com.clairvista.liveexpert.omaha.server.util.RequestElementValidationException;
import com.clairvista.liveexpert.omaha.server.util.ServletUtils;
import com.clairvista.liveexpert.omaha.server.util.XMLUtils;

@Controller
public class UpdateServlet {
   
   private static Logger LOGGER = Logger.getLogger(UpdateServlet.class);

   @Autowired
   private RequestService requestService;

   @Autowired
   private OperatingSystemService operatingSystemService;
   
   /**
    * Design Decisions:
    *  - The application is always fully reinstalled (i.e. patch updates are not supported).
    *  - Application versions will be ordered by version ID descending when finding the current version.
    *  - Each application uses a single installer binary.
    *  - The download server does not need to be the same machine as the update server.
    *  - The download server should respond to HEAD requests for all files that it contains.
    *  - The size and hashes for each installer is known ahead of time and written to the DB when a 
    *      new version record is created for an application
    *  - Data actions are not used or recorded.
    */
   
   @RequestMapping(value = "/update", method = RequestMethod.POST)
   public @ResponseBody String processOmahaRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse) 
         throws ServletException, IOException {
      String postString = ServletUtils.getRawPostData(httpRequest);
      LOGGER.debug("Post data provided: " + postString);
      
      DocumentBuilder docBuilder = createXmlDocBuilder();
      if(docBuilder == null) {         
         httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
         return "error";
      }

      Document doc = createXmlDoc(postString, docBuilder);
      if(doc == null) {
         httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         return "invalidRequest-failedToParse";
      }

      // Record request.
      Element requestElem = doc.getDocumentElement();
      Request omahaRequest = null;
      try {
         omahaRequest = requestService.recordRequest(requestElem);
      } catch(RequestElementValidationException reve) {
         LOGGER.error("Failed to record request.", reve);
         return "invalidRequest-" + reve.getResponseErrorDetails();
      }
      if(omahaRequest == null) {
         LOGGER.warn("Request creation failed for input: " + postString);
         httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         return "invalidRequest";
      }
      
      // Record operating system.
      List<Element> operatingSystemElems = 
            DomUtils.getChildElementsByTagName(requestElem, APIElementNames.OPERATING_SYSTEM);
      if(operatingSystemElems.size() > 1) {
         String requestContent = XMLUtils.elementToString(requestElem);
         LOGGER.warn("Multiple operating system nodes found in request. Conut: " + operatingSystemElems.size() +
               " Request Content: " + requestContent);
      } else if(!operatingSystemElems.isEmpty()) {
         Element operatingSystemNode = operatingSystemElems.get(0);
         try {
            operatingSystemService.recordOperatingSystem(omahaRequest, operatingSystemNode); 
         } catch (RequestElementValidationException reve) {
            LOGGER.warn("Failed to record Operating System.", reve);
         }
      } else {
         LOGGER.warn("No operating system node provided with request.");
      }
      
      // Process application directives.
      List<Element> applicationElems = DomUtils.getChildElementsByTagName(requestElem, APIElementNames.APPLICATION);
      ResponseRoot omahaResponse = requestService.processRequest(omahaRequest, applicationElems);
      
      // Produce Response.
      Document responseDoc = docBuilder.newDocument();
      Element responseElem = omahaResponse.toXML(responseDoc);
      String responseContent = XMLUtils.elementToString(responseElem);
      httpResponse.setStatus(HttpServletResponse.SC_OK);
      return responseContent;
   }

   private DocumentBuilder createXmlDocBuilder() {
      // Setup XML Processing Infrastructure.
      DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
      try {
         return docBuilderFactory.newDocumentBuilder();
      } catch (ParserConfigurationException pce) {
         LOGGER.error("SERVER ERROR -- Failed to initialize XML Parser.", pce);
         return null;
      }
   }

   private Document createXmlDoc(String postString, DocumentBuilder docBuilder) {
      // Process request input data.
      try {
         InputSource postInput = new InputSource(new StringReader(postString));
         return docBuilder.parse(postInput);
      } catch (SAXException saxe) {
         LOGGER.warn("INVALID REQUEST -- Failed to parse input: " + postString, saxe);
      } catch (IOException ioe) {
         LOGGER.warn("INVALID REQUEST -- Failed to parse input: " + postString, ioe);
      }
      return null;
   }
   
}
