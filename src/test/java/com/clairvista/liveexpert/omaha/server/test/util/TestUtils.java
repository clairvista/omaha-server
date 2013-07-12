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
import com.clairvista.liveexpert.omaha.server.model.Protocol;

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

   public static void populateTestData(ApplicationVersion testAppVersion, Session session) {
      Application testApp = testAppVersion.getApplication();
      List<Application> applications = session.createQuery("FROM Application WHERE appID = :appID")
            .setString("appID", testApp.getAppID())
            .list();
      if(applications.isEmpty()) {
         session.save(testApp);
      } else {
         testApp = applications.get(0);
      }
      
      List<ApplicationVersion> appVersions = session.createQuery("FROM ApplicationVersion WHERE application = :applicationID AND versionID = :versionID")
            .setInteger("applicationID", testApp.getId())
            .setString("versionID", testAppVersion.getVersionID())
            .list();
      if(appVersions.isEmpty()) {
         testAppVersion.setApplication(testApp);
         session.save(testAppVersion);
      }
   }

   public static Element submitTestRequest(StringBuilder testRequestContent, 
         WebApplicationContext appContext) throws Exception {
      MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(appContext).build();

      MockHttpServletRequestBuilder httpRequest = MockMvcRequestBuilders.post("/update").content(testRequestContent.toString());
      MvcResult result = mockMvc.perform(httpRequest)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();

      return TestUtils.extractResponseXML(result);
   }
   
   public static Element extractResponseXML(MvcResult result) throws Exception {
      MockHttpServletResponse response = result.getResponse();
      String responseContent = response.getContentAsString();

      DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
      InputSource postInput = new InputSource(new StringReader(responseContent));
      Document doc = docBuilder.parse(postInput);

      return doc.getDocumentElement();
   }

}
