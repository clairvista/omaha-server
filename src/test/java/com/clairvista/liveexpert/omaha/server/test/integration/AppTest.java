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
import com.clairvista.liveexpert.omaha.server.model.ApplicationVersionRequest;
import com.clairvista.liveexpert.omaha.server.model.Ping;
import com.clairvista.liveexpert.omaha.server.model.Request;
import com.clairvista.liveexpert.omaha.server.test.util.TestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:servlet-context.xml"})
@WebAppConfiguration
@Transactional
public class AppTest {
   
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
      ApplicationVersion testAppVersion1 = new ApplicationVersion(testApp, "1.2.3.4", 
            "http://assets.liveexperttest.net", "testAppInstaller.exe", "testHash1234=", 
            1234, TestUtils.TEST_CREATOR_NAME);
      TestUtils.populateTestApplicationVersion(testAppVersion1, session);
   }
   
   @Test
   public void unknownAppResponseTest() throws Exception {
      StringBuilder testRequestContent = new StringBuilder();
      testRequestContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      testRequestContent.append("<request protocol=\"3.0\" version=\"1.0.0.0\" ismachine=\"1\" " +
            "sessionid=\"{session-1234}\" requestid=\"{request-1234}\">");
      testRequestContent.append("  <os platform=\"mac\" version=\"MacOSX\"/>");
      testRequestContent.append("  <app appid=\"{unknown-app-1234}\" version=\"1.2.3.4\" lang=\"en\" brand=\"test\">");
      testRequestContent.append("    <updatecheck/>");
      testRequestContent.append("  </app>");
      testRequestContent.append("</request>");
      
      Element responseElem = TestUtils.submitTestRequest(testRequestContent, this.appContext);

      List<Element> appElems = DomUtils.getChildElementsByTagName(responseElem, "app");
      assertEquals(1, appElems.size());
      
      Element appElem = appElems.get(0);
      assertEquals("error-unknownApplication", appElem.getAttribute("status"));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void unknownAppDataCaptureTest() throws Exception {
      StringBuilder testRequestContent = new StringBuilder();
      testRequestContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      testRequestContent.append("<request protocol=\"3.0\" version=\"1.0.0.0\" ismachine=\"1\" " +
            "sessionid=\"{session-1234}\" requestid=\"{request-1234}\">");
      testRequestContent.append("  <os platform=\"mac\" version=\"MacOSX\"/>");
      testRequestContent.append("  <app appid=\"{unknown-app-1234}\" version=\"1.2.3.4\" lang=\"en\" brand=\"test\">");
      testRequestContent.append("    <updatecheck/>");
      testRequestContent.append("  </app>");
      testRequestContent.append("</request>");

      List<Request> preRequests = session.createQuery("FROM Request").list();
      
      TestUtils.submitTestRequest(testRequestContent, this.appContext);

      List<Request> postRequests = session.createQuery("FROM Request").list();
      assertEquals(preRequests.size() + 1, postRequests.size());
      
      Request omahaRequest = postRequests.get(postRequests.size() - 1);
      List<ApplicationVersionRequest> appRequests = session.createQuery(
            "FROM ApplicationVersionRequest WHERE request = :requestID")
            .setInteger("requestID", omahaRequest.getId())
            .list();
      assertEquals(0, appRequests.size());
   }

   @Test
   public void unknownVersionResponseTest() throws Exception {
      StringBuilder testRequestContent = new StringBuilder();
      testRequestContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      testRequestContent.append("<request protocol=\"3.0\" version=\"1.0.0.0\" ismachine=\"1\" " +
            "sessionid=\"{session-1234}\" requestid=\"{request-1234}\">");
      testRequestContent.append("  <os platform=\"mac\" version=\"MacOSX\"/>");
      testRequestContent.append("  <app appid=\"{test-app-1234}\" version=\"1.2.3.5\" lang=\"en\" brand=\"test\">");
      testRequestContent.append("    <updatecheck/>");
      testRequestContent.append("  </app>");
      testRequestContent.append("</request>");
      
      Element responseElem = TestUtils.submitTestRequest(testRequestContent, this.appContext);

      List<Element> appElems = DomUtils.getChildElementsByTagName(responseElem, "app");
      assertEquals(1, appElems.size());
      
      Element appElem = appElems.get(0);
      assertEquals("error-unknownVersion", appElem.getAttribute("status"));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void unknownVersionDataCaptureTest() throws Exception {
      StringBuilder testRequestContent = new StringBuilder();
      testRequestContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      testRequestContent.append("<request protocol=\"3.0\" version=\"1.0.0.0\" ismachine=\"1\" " +
            "sessionid=\"{session-1234}\" requestid=\"{request-1234}\">");
      testRequestContent.append("  <os platform=\"mac\" version=\"MacOSX\"/>");
      testRequestContent.append("  <app appid=\"{test-app-1234}\" version=\"1.2.3.5\" lang=\"en\" brand=\"test\">");
      testRequestContent.append("    <updatecheck/>");
      testRequestContent.append("  </app>");
      testRequestContent.append("</request>");

      List<Request> preRequests = session.createQuery("FROM Request").list();
      
      TestUtils.submitTestRequest(testRequestContent, this.appContext);

      List<Request> postRequests = session.createQuery("FROM Request").list();
      assertEquals(preRequests.size() + 1, postRequests.size());
      
      Request omahaRequest = postRequests.get(postRequests.size() - 1);
      List<ApplicationVersionRequest> appRequests = session.createQuery(
            "FROM ApplicationVersionRequest WHERE request = :requestID")
            .setInteger("requestID", omahaRequest.getId())
            .list();
      assertEquals(0, appRequests.size());
   }

   @Test
   public void missingAttrResponseTest() throws Exception {
      StringBuilder testRequestContent = new StringBuilder();
      testRequestContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      testRequestContent.append("<request protocol=\"3.0\" version=\"1.0.0.0\" ismachine=\"1\" " +
            "sessionid=\"{session-1234}\" requestid=\"{request-1234}\">");
      testRequestContent.append("  <os platform=\"mac\" version=\"MacOSX\"/>");
      testRequestContent.append("  <app>");
      testRequestContent.append("    <updatecheck/>");
      testRequestContent.append("  </app>");
      testRequestContent.append("</request>");
      
      Element responseElem = TestUtils.submitTestRequest(testRequestContent, this.appContext);

      List<Element> appElems = DomUtils.getChildElementsByTagName(responseElem, "app");
      assertEquals(1, appElems.size());

      // There are no attributes that are required outside of the appid.
      Element appElem = appElems.get(0);
      assertEquals("error-unknownApplication", appElem.getAttribute("status"));
      assertEquals("", appElem.getAttribute("errorDetails"));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void missingAttrDataCaptureTest() throws Exception {
      StringBuilder testRequestContent = new StringBuilder();
      testRequestContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      testRequestContent.append("<request protocol=\"3.0\" version=\"1.0.0.0\" ismachine=\"1\" " +
            "sessionid=\"{session-1234}\" requestid=\"{request-1234}\">");
      testRequestContent.append("  <os platform=\"mac\" version=\"MacOSX\"/>");
      testRequestContent.append("  <app>");
      testRequestContent.append("    <updatecheck/>");
      testRequestContent.append("  </app>");
      testRequestContent.append("</request>");

      List<Request> preRequests = session.createQuery("FROM Request").list();
      
      TestUtils.submitTestRequest(testRequestContent, this.appContext);

      List<Request> postRequests = session.createQuery("FROM Request").list();
      assertEquals(preRequests.size() + 1, postRequests.size());
      
      Request omahaRequest = postRequests.get(postRequests.size() - 1);
      List<ApplicationVersionRequest> appRequests = session.createQuery(
            "FROM ApplicationVersionRequest WHERE request = :requestID")
            .setInteger("requestID", omahaRequest.getId())
            .list();
      assertEquals(0, appRequests.size());
   }

   @Test
   public void noActionsResponseTest() throws Exception {
      StringBuilder testRequestContent = new StringBuilder();
      testRequestContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      testRequestContent.append("<request protocol=\"3.0\" version=\"1.0.0.0\" ismachine=\"1\" " +
            "sessionid=\"{session-1234}\" requestid=\"{request-1234}\">");
      testRequestContent.append("  <os platform=\"mac\" version=\"MacOSX\"/>");
      testRequestContent.append("  <app appid=\"{test-app-1234}\" version=\"1.2.3.4\" lang=\"en\" brand=\"test\">");
      testRequestContent.append("  </app>");
      testRequestContent.append("</request>");
      
      Element responseElem = TestUtils.submitTestRequest(testRequestContent, this.appContext);

      List<Element> appElems = DomUtils.getChildElementsByTagName(responseElem, "app");
      assertEquals(1, appElems.size());
      
      Element appElem = appElems.get(0);
      assertEquals("error-noActionsRequested", appElem.getAttribute("status"));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void noActionsDataCaptureTest() throws Exception {
      StringBuilder testRequestContent = new StringBuilder();
      testRequestContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      testRequestContent.append("<request protocol=\"3.0\" version=\"1.0.0.0\" ismachine=\"1\" " +
            "sessionid=\"{session-1234}\" requestid=\"{request-1234}\">");
      testRequestContent.append("  <os platform=\"mac\" version=\"MacOSX\"/>");
      testRequestContent.append("  <app appid=\"{test-app-1234}\" version=\"1.2.3.4\" lang=\"en\" brand=\"test\">");
      testRequestContent.append("  </app>");
      testRequestContent.append("</request>");

      List<Request> preRequests = session.createQuery("FROM Request").list();
      
      TestUtils.submitTestRequest(testRequestContent, this.appContext);

      List<Request> postRequests = session.createQuery("FROM Request").list();
      assertEquals(preRequests.size() + 1, postRequests.size());
      
      Request omahaRequest = postRequests.get(postRequests.size() - 1);
      List<ApplicationVersionRequest> appRequests = session.createQuery(
            "FROM ApplicationVersionRequest WHERE request = :requestID")
            .setInteger("requestID", omahaRequest.getId())
            .list();
      assertEquals(1, appRequests.size());
      
      ApplicationVersionRequest appRequest = appRequests.get(0);
      assertEquals("{test-app-1234}", appRequest.getApplicationVersion().getApplication().getAppID());
      assertEquals("1.2.3.4", appRequest.getApplicationVersion().getVersionID());
      assertEquals("en", appRequest.getLanguage());
      assertEquals("test", appRequest.getBrand());
      assertEquals(null, appRequest.getClient());
      assertEquals(null, appRequest.getNextVersion());
   }

   @SuppressWarnings("unchecked")
   @Test
   public void noVersionSpecifiedDataCaptureTest() throws Exception {
      StringBuilder testRequestContent = new StringBuilder();
      testRequestContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      testRequestContent.append("<request protocol=\"3.0\" version=\"1.0.0.0\" ismachine=\"1\" " +
            "sessionid=\"{session-1234}\" requestid=\"{request-1234}\">");
      testRequestContent.append("  <os platform=\"mac\" version=\"MacOSX\"/>");
      testRequestContent.append("  <app appid=\"{test-app-1234}\" lang=\"en\" brand=\"test\">");
      testRequestContent.append("    <ping/>");
      testRequestContent.append("  </app>");
      testRequestContent.append("</request>");

      List<Request> preRequests = session.createQuery("FROM Request").list();
      
      TestUtils.submitTestRequest(testRequestContent, this.appContext);

      List<Request> postRequests = session.createQuery("FROM Request").list();
      assertEquals(preRequests.size() + 1, postRequests.size());
      
      Request omahaRequest = postRequests.get(postRequests.size() - 1);
      List<ApplicationVersionRequest> appRequests = session.createQuery(
            "FROM ApplicationVersionRequest WHERE request = :requestID")
            .setInteger("requestID", omahaRequest.getId())
            .list();
      assertEquals(1, appRequests.size());
      
      ApplicationVersionRequest appRequest = appRequests.get(0);
      assertEquals("{test-app-1234}", appRequest.getApplicationVersion().getApplication().getAppID());
      assertEquals("0.0.0.0", appRequest.getApplicationVersion().getVersionID());
      assertEquals("en", appRequest.getLanguage());
      assertEquals("test", appRequest.getBrand());
      assertEquals(null, appRequest.getClient());
      assertEquals(null, appRequest.getNextVersion());
   }

   @Test
   public void successResponseTest() throws Exception {
      StringBuilder testRequestContent = new StringBuilder();
      testRequestContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      testRequestContent.append("<request protocol=\"3.0\" version=\"1.0.0.0\" ismachine=\"1\" " +
            "sessionid=\"{session-1234}\" requestid=\"{request-1234}\">");
      testRequestContent.append("  <os platform=\"mac\" version=\"MacOSX\"/>");
      testRequestContent.append("  <app appid=\"{test-app-1234}\" version=\"1.2.3.4\" lang=\"en\" brand=\"test\">");
      testRequestContent.append("    <ping/>");
      testRequestContent.append("  </app>");
      testRequestContent.append("</request>");
      
      Element responseElem = TestUtils.submitTestRequest(testRequestContent, this.appContext);

      List<Element> appElems = DomUtils.getChildElementsByTagName(responseElem, "app");
      assertEquals(1, appElems.size());
      
      Element appElem = appElems.get(0);
      assertEquals("ok", appElem.getAttribute("status"));
      
      List<Element> children = DomUtils.getChildElements(appElem);
      assertEquals(1, children.size());
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
      testRequestContent.append("    <ping/>");
      testRequestContent.append("  </app>");
      testRequestContent.append("</request>");

      List<Request> preRequests = session.createQuery("FROM Request").list();
      
      TestUtils.submitTestRequest(testRequestContent, this.appContext);

      List<Request> postRequests = session.createQuery("FROM Request").list();
      assertEquals(preRequests.size() + 1, postRequests.size());
      
      Request omahaRequest = postRequests.get(postRequests.size() - 1);
      List<ApplicationVersionRequest> appRequests = session.createQuery(
            "FROM ApplicationVersionRequest WHERE request = :requestID")
            .setInteger("requestID", omahaRequest.getId())
            .list();
      assertEquals(1, appRequests.size());
      
      ApplicationVersionRequest appRequest = appRequests.get(0);
      assertEquals("{test-app-1234}", appRequest.getApplicationVersion().getApplication().getAppID());
      assertEquals("1.2.3.4", appRequest.getApplicationVersion().getVersionID());
      assertEquals("en", appRequest.getLanguage());
      assertEquals("test", appRequest.getBrand());
      assertEquals(null, appRequest.getClient());
      assertEquals(null, appRequest.getNextVersion());
      
      List<Ping> pings = session.createQuery(
            "FROM Ping WHERE applicationVersionRequest = :appRequestID")
            .setInteger("appRequestID", appRequest.getId())
            .list();
      assertEquals(1, pings.size());
   }
}
