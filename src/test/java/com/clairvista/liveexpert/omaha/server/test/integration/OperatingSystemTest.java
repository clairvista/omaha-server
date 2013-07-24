package com.clairvista.liveexpert.omaha.server.test.integration;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.xml.DomUtils;
import org.springframework.web.context.WebApplicationContext;
import org.w3c.dom.Element;

import com.clairvista.liveexpert.omaha.server.model.Application;
import com.clairvista.liveexpert.omaha.server.model.ApplicationVersion;
import com.clairvista.liveexpert.omaha.server.model.OperatingSystem;
import com.clairvista.liveexpert.omaha.server.model.Request;
import com.clairvista.liveexpert.omaha.server.model.User;
import com.clairvista.liveexpert.omaha.server.test.util.TestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:servlet-context.xml"})
@WebAppConfiguration
@Transactional
public class OperatingSystemTest {
   
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
      ApplicationVersion testAppVersion0 = new ApplicationVersion(testApp, "0.0.0.0", 
            "http://assets.liveexperttest.net", "testAppInstaller.exe", "testHash1234=", 
            1234, TestUtils.TEST_CREATOR_NAME);
      TestUtils.populateTestApplicationVersion(testAppVersion0, session);
   }

   @Test
   public void missingAttrResponseTest() throws Exception {
      StringBuilder testRequestContent = new StringBuilder();
      testRequestContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      testRequestContent.append("<request protocol=\"3.0\" version=\"1.0.0.0\" ismachine=\"1\" " +
            "sessionid=\"{session-1234}\" requestid=\"{request-1234}\">");
      testRequestContent.append("  <os />");
      testRequestContent.append("</request>");
      
      Element responseElem = TestUtils.submitTestRequest(testRequestContent, this.appContext);
      
      List<Element> children = DomUtils.getChildElements(responseElem);
      assertEquals(1, children.size());  // Just a daystart element.
   }

   @SuppressWarnings("unchecked")
   @Test
   public void missingAttrDataCaptureTest() throws Exception {
      StringBuilder testRequestContent = new StringBuilder();
      testRequestContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      testRequestContent.append("<request protocol=\"3.0\" version=\"1.0.0.0\" ismachine=\"1\" " +
            "sessionid=\"{session-1234}\" requestid=\"{request-1234}\" userid=\"{user-with-os-1234}\">");
      testRequestContent.append("  <os />");
      testRequestContent.append("</request>");
      
      OperatingSystem os = new OperatingSystem("mac", "MacOSX", "10.4.11_ppc750", "arm");
      TestUtils.populateTestOperatingSystem(os, session);
      User existingUser = new User("{user-with-os-1234}", os);
      TestUtils.populateTestUser(existingUser, session);

      List<OperatingSystem> preOSs = session.createQuery("FROM OperatingSystem").list();
      List<Request> preRequests = session.createQuery("FROM Request").list();
      
      TestUtils.submitTestRequest(testRequestContent, this.appContext);

      List<OperatingSystem> postOSs = session.createQuery("FROM OperatingSystem").list();
      assertEquals(preOSs.size(), postOSs.size()); // No new OS record.

      List<Request> postRequests = session.createQuery("FROM Request").list();
      assertEquals(preRequests.size() + 1, postRequests.size());
      
      Request omahaRequest = postRequests.get(postRequests.size() - 1);
      assertEquals(existingUser.getId(), omahaRequest.getUser().getId());
      
      // Fetch fresh copy of user.
      assertEquals(os.getId(), omahaRequest.getUser().getOperatingSystem().getId()); // OS association remained unchanged.
   }

   @Test
   public void successResponseTest() throws Exception {
      StringBuilder testRequestContent = new StringBuilder();
      testRequestContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      testRequestContent.append("<request protocol=\"3.0\" version=\"1.0.0.0\" ismachine=\"1\" " +
            "sessionid=\"{session-1234}\" requestid=\"{request-1234}\">");
      testRequestContent.append("  <os platform=\"mac\" version=\"MacOSX\"/>");
      testRequestContent.append("</request>");
      
      Element responseElem = TestUtils.submitTestRequest(testRequestContent, this.appContext);
      
      List<Element> children = DomUtils.getChildElements(responseElem);
      assertEquals(1, children.size());  // Just a daystart element.
   }

   @SuppressWarnings("unchecked")
   @Test
   public void successDataCaptureTest() throws Exception {
      StringBuilder testRequestContent = new StringBuilder();
      testRequestContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      testRequestContent.append("<request protocol=\"3.0\" version=\"1.0.0.0\" ismachine=\"1\" " +
            "sessionid=\"{session-1234}\" requestid=\"{request-1234}\">");
      testRequestContent.append("  <os platform=\"win\" version=\"5.2\" sp=\"Service Pack 2\" arch=\"x86\"/>");
      testRequestContent.append("</request>");

      List<OperatingSystem> preOSs = session.createQuery("FROM OperatingSystem").list();
      
      TestUtils.submitTestRequest(testRequestContent, this.appContext);

      List<OperatingSystem> postOSs = session.createQuery("FROM OperatingSystem").list();
      assertEquals(preOSs.size() + 1, postOSs.size());
      
      OperatingSystem newOS = postOSs.get(postOSs.size() - 1);
      assertEquals("win", newOS.getPlatform());
      assertEquals("5.2", newOS.getVersion());
      assertEquals("Service Pack 2", newOS.getServicePack());
      assertEquals("x86", newOS.getArchitecture());
   }

   @SuppressWarnings("unchecked")
   @Test
   public void successWithUserDataCaptureTest() throws Exception {
      StringBuilder testRequestContent = new StringBuilder();
      testRequestContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      testRequestContent.append("<request protocol=\"3.0\" version=\"1.0.0.0\" ismachine=\"1\" " +
            "sessionid=\"{session-1234}\" requestid=\"{request-1234}\" userid=\"{os-user-1234}\">");
      testRequestContent.append("  <os platform=\"win\" version=\"5.2\" sp=\"Service Pack 2\" arch=\"x86\"/>");
      testRequestContent.append("</request>");

      List<OperatingSystem> preOSs = session.createQuery("FROM OperatingSystem").list();
      List<Request> preRequests = session.createQuery("FROM Request").list();
      
      TestUtils.submitTestRequest(testRequestContent, this.appContext);

      List<OperatingSystem> postOSs = session.createQuery("FROM OperatingSystem").list();
      assertEquals(preOSs.size() + 1, postOSs.size());

      List<Request> postRequests = session.createQuery("FROM Request").list();
      assertEquals(preRequests.size() + 1, postRequests.size());
      
      OperatingSystem newOS = postOSs.get(postOSs.size() - 1);
      assertEquals("win", newOS.getPlatform());
      assertEquals("5.2", newOS.getVersion());
      assertEquals("Service Pack 2", newOS.getServicePack());
      assertEquals("x86", newOS.getArchitecture());

      Request omahaRequest = postRequests.get(postRequests.size() - 1);
      assertEquals(newOS.getId(), omahaRequest.getUser().getOperatingSystem().getId());
   }
}
