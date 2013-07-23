package com.clairvista.liveexpert.omaha.server.test.util;

import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.hibernate.Session;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.clairvista.liveexpert.omaha.server.model.Application;
import com.clairvista.liveexpert.omaha.server.model.ApplicationVersion;
import com.clairvista.liveexpert.omaha.server.model.OperatingSystem;
import com.clairvista.liveexpert.omaha.server.model.Protocol;
import com.clairvista.liveexpert.omaha.server.model.User;

@SuppressWarnings("unchecked")
public class TestUtils {

   public static final String TEST_CREATOR_NAME = "testSetup";

   public static void setupStaticDBContent(Session session) {
      String initProtocolID = "3.0";
      
      List<Protocol> protocols = session.createQuery("from Protocol where protocolID = :protocolID")
            .setString("protocolID", initProtocolID)
            .list();
      if(protocols.isEmpty()) {
         Protocol protocol = new Protocol();
         protocol.setProtocolID(initProtocolID);
         protocol.setCreatedBy(TEST_CREATOR_NAME);
         session.save(protocol);
      }
   }

   public static void populateTestApplicationVersion(ApplicationVersion testAppVersion, Session session) {
      Application testApp = testAppVersion.getApplication();
      List<Application> applications = session.createQuery("FROM Application WHERE appID = :appID")
            .setString("appID", testApp.getAppID())
            .list();
      if(applications.isEmpty()) {
         session.save(testApp);
      } else {
         testApp = applications.get(0);
      }
      
      List<ApplicationVersion> appVersions = session.createQuery("FROM ApplicationVersion " +
      		"WHERE application = :applicationID " +
      		"AND versionID = :versionID")
            .setInteger("applicationID", testApp.getId())
            .setString("versionID", testAppVersion.getVersionID())
            .list();
      if(appVersions.isEmpty()) {
         testAppVersion.setApplication(testApp);
         session.save(testAppVersion);
      }
   }

   public static void populateTestOperatingSystem(OperatingSystem testOS, Session session) {
      List<OperatingSystem> existingOSs = session.createQuery("FROM OperatingSystem " +
            "WHERE platform = :platform " +
            "AND version = :version " +
            "AND servicePack = :servicePack " +
            "AND architecture = :architecture")
            .setString("platform", testOS.getPlatform())
            .setString("version", testOS.getVersion())
            .setString("servicePack", testOS.getServicePack())
            .setString("architecture", testOS.getArchitecture())
            .list();
      if(existingOSs.isEmpty()) {
         session.save(testOS);
      }
   }

   public static void populateTestUser(User testUser, Session session) {
      List<User> existingUsers = session.createQuery("FROM User " +
            "WHERE userID = :userID " )
            .setString("userID", testUser.getUserID())
            .list();
      if(existingUsers.isEmpty()) {
         session.save(testUser);
      }
   }
   
   public static MvcResult submitTestRequestWithRawResponse(StringBuilder testRequestContent, 
         WebApplicationContext appContext) throws Exception {

      MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(appContext).build();

      MockHttpServletRequestBuilder httpRequest = MockMvcRequestBuilders.post("/update").content(testRequestContent.toString());
      return mockMvc.perform(httpRequest)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();
   }

   public static Element submitTestRequest(StringBuilder testRequestContent, 
         WebApplicationContext appContext) throws Exception {
      MvcResult result = submitTestRequestWithRawResponse(testRequestContent, appContext);
      String responseContent = extractResponseContent(result);
      return parseAsXML(responseContent);
   }
   
   public static String extractResponseContent(MvcResult result) throws Exception {
      MockHttpServletResponse response = result.getResponse();
      return response.getContentAsString();
   }

   public static Element parseAsXML(String responseContent) throws Exception {
      DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
      InputSource postInput = new InputSource(new StringReader(responseContent));
      Document doc = docBuilder.parse(postInput);

      return doc.getDocumentElement();
   }

}
