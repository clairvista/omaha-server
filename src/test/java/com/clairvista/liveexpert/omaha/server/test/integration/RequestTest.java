package com.clairvista.liveexpert.omaha.server.test.integration;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.xml.DomUtils;
import org.springframework.web.context.WebApplicationContext;
import org.w3c.dom.Element;

import com.clairvista.liveexpert.omaha.server.model.Application;
import com.clairvista.liveexpert.omaha.server.model.ApplicationVersion;
import com.clairvista.liveexpert.omaha.server.model.ApplicationVersionRequest;
import com.clairvista.liveexpert.omaha.server.model.Ping;
import com.clairvista.liveexpert.omaha.server.model.Request;
import com.clairvista.liveexpert.omaha.server.test.util.TestUtils;
import com.clairvista.liveexpert.omaha.server.test.util.TestWebAppConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"file:src/test/resources/test-servlet-context.xml"})
@ComponentScan(basePackageClasses={TestWebAppConfig.class})
@WebAppConfiguration
@Transactional
public class RequestTest {
   
   @Autowired
   private WebApplicationContext appContext;

   @Autowired
   private SessionFactory sessionFactory;
   
   private org.hibernate.Session session;
   
   @Before
   public void setup() {
      session = sessionFactory.getCurrentSession();

      TestUtils.setupStaticDBContent(session);
      
      Application testApp = new Application("Test Application", "Test Application Description", 
            "{test-app-1234}", TestUtils.TEST_CREATOR_NAME);
      ApplicationVersion testAppVersion1 = new ApplicationVersion(testApp, "1.2.3.4", 
            "http://assets.liveexperttest.net", "testAppInstaller.exe", "testHash1234=", 
            1234, TestUtils.TEST_CREATOR_NAME);
      TestUtils.populateTestData(testAppVersion1, session);
   }

   @Test
   public void invalidProtocolResponseTest() throws Exception {
      StringBuilder testRequestContent = new StringBuilder();
      testRequestContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      testRequestContent.append("<request protocol=\"foo\" version=\"1.0.0.0\" ismachine=\"1\" " +
            "sessionid=\"{session-1234}\" requestid=\"{request-1234}\">");
      testRequestContent.append("  <os platform=\"mac\" version=\"MacOSX\"/>");
      testRequestContent.append("  <app appid=\"{test-app-1234}\" version=\"1.2.3.4\" lang=\"en\" brand=\"test\">");
      testRequestContent.append("    <updatecheck/>");
      testRequestContent.append("  </app>");
      testRequestContent.append("</request>");

      MvcResult result = TestUtils.submitTestRequestWithRawResponse(testRequestContent, this.appContext);
      String responseContent = TestUtils.extractResponseContent(result);

      assertEquals("invalidRequest-unsupportedProtocol", responseContent);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void invalidProtocolDataCaptureTest() throws Exception {      
   }
   
   @Test
   public void missingAttrResponseTest() throws Exception {
      StringBuilder testRequestContent = new StringBuilder();
      testRequestContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      testRequestContent.append("<request protocol=\"3.0\" sessionid=\"{session-1234}\">");
      testRequestContent.append("  <os platform=\"mac\" version=\"MacOSX\"/>");
      testRequestContent.append("  <app appid=\"{test-app-1234}\" version=\"1.2.3.4\" lang=\"en\" brand=\"test\">");
      testRequestContent.append("    <updatecheck/>");
      testRequestContent.append("  </app>");
      testRequestContent.append("</request>");

      MvcResult result = TestUtils.submitTestRequestWithRawResponse(testRequestContent, this.appContext);
      String responseContent = TestUtils.extractResponseContent(result);

      assertEquals("invalidRequest-missing:version,ismachine,requestid", responseContent);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void missingAttrDataCaptureTest() throws Exception {      
   }
   
   @Test
   public void noAppsResponseTest() throws Exception {      
   }

   @SuppressWarnings("unchecked")
   @Test
   public void noAppsDataCaptureTest() throws Exception {      
   }
   
   @Test
   public void successResponseTest() throws Exception {
      StringBuilder testRequestContent = new StringBuilder();
      testRequestContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      testRequestContent.append("<request protocol=\"3.0\" version=\"1.0.0.0\" ismachine=\"1\" " +
            "sessionid=\"{session-1234}\" requestid=\"{request-1234}\">");
      testRequestContent.append("  <os platform=\"mac\" version=\"MacOSX\"/>");
      testRequestContent.append("  <app appid=\"{test-app-1234}\" version=\"1.2.3.4\" lang=\"en\" brand=\"test\">");
      testRequestContent.append("    <updatecheck/>");
      testRequestContent.append("  </app>");
      testRequestContent.append("</request>");
      
      Element responseElem = TestUtils.submitTestRequest(testRequestContent, this.appContext);

      assertEquals("todo", responseElem.getAttribute("server"));
      assertEquals("3.0", responseElem.getAttribute("protocol"));

      List<Element> children = DomUtils.getChildElements(responseElem);
      assertEquals(2, children.size());  // One for the daystart and one for the app
   }

   @SuppressWarnings("unchecked")
   @Test
   public void successDataCaptureTest() throws Exception {
      StringBuilder testRequestContent = new StringBuilder();
      testRequestContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      testRequestContent.append("<request protocol=\"3.0\" version=\"1.0.0.0\" ismachine=\"1\" " +
            "sessionid=\"{session-1234}\" requestid=\"{request-1234}\">");
      testRequestContent.append("  <os platform=\"mac\" version=\"MacOSX\"/>");
      testRequestContent.append("  <app appid=\"{test-app-1234}\" version=\"1.2.3.4\" lang=\"en\" brand=\"test\">");
      testRequestContent.append("    <updatecheck/>");
      testRequestContent.append("  </app>");
      testRequestContent.append("</request>");

      List<Request> preRequests = session.createQuery("FROM Request").list();
      
      TestUtils.submitTestRequest(testRequestContent, this.appContext);

      List<Request> postRequests = session.createQuery("FROM Request").list();
      assertEquals(preRequests.size() + 1, postRequests.size());
      
      Request omahaRequest = postRequests.get(postRequests.size() - 1);

      assertEquals("{request-1234}", omahaRequest.getRequestID());
      assertEquals("3.0", omahaRequest.getProtocol().getProtocolID());
      assertEquals("{session-1234}", omahaRequest.getSession().getSessionID());
      assertEquals("1.0.0.0", omahaRequest.getClientVersion().getVersionID());
      assertEquals(true, omahaRequest.getIsMachine());
      assertEquals(null, omahaRequest.getOriginURL());
      assertEquals(null, omahaRequest.getUser());
   }

   @SuppressWarnings("unchecked")
   @Test
   public void withUserDataCaptureTest() throws Exception {
      StringBuilder testRequestContent = new StringBuilder();
      testRequestContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      testRequestContent.append("<request protocol=\"3.0\" version=\"1.0.0.0\" ismachine=\"1\" " +
            "sessionid=\"{session-1234}\" requestid=\"{request-1234}\" userid=\"{user-1234}\">");
      testRequestContent.append("  <os platform=\"mac\" version=\"MacOSX\"/>");
      testRequestContent.append("  <app appid=\"{test-app-1234}\" version=\"1.2.3.4\" lang=\"en\" brand=\"test\">");
      testRequestContent.append("    <updatecheck/>");
      testRequestContent.append("  </app>");
      testRequestContent.append("</request>");

      List<Request> preRequests = session.createQuery("FROM Request").list();
      
      TestUtils.submitTestRequest(testRequestContent, this.appContext);

      List<Request> postRequests = session.createQuery("FROM Request").list();
      assertEquals(preRequests.size() + 1, postRequests.size());
      
      Request omahaRequest = postRequests.get(postRequests.size() - 1);

      assertEquals("{request-1234}", omahaRequest.getRequestID());
      assertEquals("3.0", omahaRequest.getProtocol().getProtocolID());
      assertEquals("{session-1234}", omahaRequest.getSession().getSessionID());
      assertEquals("1.0.0.0", omahaRequest.getClientVersion().getVersionID());
      assertEquals(true, omahaRequest.getIsMachine());
      assertEquals(null, omahaRequest.getOriginURL());
      assertEquals("{user-1234}", omahaRequest.getUser().getUserID());
      // TODO: Get this working.
      //assertEquals("mac", omahaRequest.getUser().getOperatingSystem().getPlatform());
   }
}
