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
import com.clairvista.liveexpert.omaha.server.model.Request;
import com.clairvista.liveexpert.omaha.server.model.UpdateCheck;
import com.clairvista.liveexpert.omaha.server.test.util.TestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:servlet-context.xml"})
@WebAppConfiguration
@Transactional
public class UpdateCheckTest {
   
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
      TestUtils.populateTestApplicationVersion(testAppVersion1, session);

      ApplicationVersion testAppVersion2 = new ApplicationVersion(testApp, "1.2.3.10", 
            "http://assets.liveexperttest.net", "testAppInstaller.exe", "testHash1235=", 
            1235, TestUtils.TEST_CREATOR_NAME);
      TestUtils.populateTestApplicationVersion(testAppVersion2, session);
   }
   
   @Test
   public void noUpdateResponseTest() throws Exception {
      StringBuilder testRequestContent = new StringBuilder();
      testRequestContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      testRequestContent.append("<request protocol=\"3.0\" version=\"1.0.0.0\" ismachine=\"1\" " +
            "sessionid=\"{session-1235}\" requestid=\"{request-1235}\">");
      testRequestContent.append("  <os platform=\"mac\" version=\"MacOSX\"/>");
      testRequestContent.append("  <app appid=\"{test-app-1234}\" version=\"1.2.3.10\" lang=\"en\" brand=\"test\">");
      testRequestContent.append("    <updatecheck/>");
      testRequestContent.append("  </app>");
      testRequestContent.append("</request>");
      
      Element responseElem = TestUtils.submitTestRequest(testRequestContent, this.appContext);
      
      List<Element> appElems = DomUtils.getChildElementsByTagName(responseElem, "app");
      assertEquals(1, appElems.size());

      Element appElem = appElems.get(0);
      List<Element> actionElems;
      Element actionElem;

      assertEquals("{test-app-1234}", appElem.getAttribute("appid"));
      actionElems = DomUtils.getChildElements(appElem);
      assertEquals(1, actionElems.size());
      actionElem = actionElems.get(0);
      assertEquals("updatecheck", actionElem.getNodeName());
      assertEquals("noupdate", actionElem.getAttribute("status"));
   }
   
   @Test
   public void updateResponseTest() throws Exception {
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
      
      List<Element> appElems = DomUtils.getChildElementsByTagName(responseElem, "app");
      assertEquals(1, appElems.size());

      Element appElem = appElems.get(0);
      List<Element> actionElems;
      Element actionElem;

      assertEquals("{test-app-1234}", appElem.getAttribute("appid"));
      actionElems = DomUtils.getChildElements(appElem);
      assertEquals(1, actionElems.size());
      actionElem = actionElems.get(0);
      assertEquals("updatecheck", actionElem.getNodeName());
      assertEquals("error-internal", actionElem.getAttribute("status"));
      assertEquals("installerNotFound", actionElem.getAttribute("errorDetails"));
   }
   
   @SuppressWarnings("unchecked")
   @Test
   public void updateDataCaptureTest() throws Exception {
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
      List<ApplicationVersionRequest> appRequests = session.createQuery(
            "FROM ApplicationVersionRequest WHERE request = :requestID")
            .setInteger("requestID", omahaRequest.getId())
            .list();
      assertEquals(1, appRequests.size());
      
      ApplicationVersionRequest appRequest = appRequests.get(0);
      List<UpdateCheck> updateChecks = session.createQuery(
            "FROM UpdateCheck WHERE applicationVersionRequest = :appRequestID")
            .setInteger("appRequestID", appRequest.getId())
            .list();
      assertEquals(1, updateChecks.size());
   }
}
