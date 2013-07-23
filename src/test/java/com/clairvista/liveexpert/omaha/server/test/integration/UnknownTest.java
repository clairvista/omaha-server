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
import com.clairvista.liveexpert.omaha.server.test.util.TestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:servlet-context.xml"})
@WebAppConfiguration
@Transactional
public class UnknownTest {
   
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
   public void successResponseTest() throws Exception {
      StringBuilder testRequestContent = new StringBuilder();
      testRequestContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      testRequestContent.append("<request protocol=\"3.0\" version=\"1.0.0.0\" ismachine=\"1\" " +
            "sessionid=\"{session-1234}\" requestid=\"{request-1234}\">");
      testRequestContent.append("  <os platform=\"mac\" version=\"MacOSX\"/>");
      testRequestContent.append("  <app appid=\"{test-app-1234}\" version=\"1.2.3.4\" lang=\"en\" brand=\"test\">");
      testRequestContent.append("    <foo />");
      testRequestContent.append("  </app>");
      testRequestContent.append("</request>");
      
      Element responseElem = TestUtils.submitTestRequest(testRequestContent, this.appContext);

      List<Element> appElems = DomUtils.getChildElementsByTagName(responseElem, "app");
      assertEquals(1, appElems.size());

      List<Element> unknownElems = DomUtils.getChildElementsByTagName(appElems.get(0), "unknown");
      assertEquals(1, unknownElems.size());
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
      testRequestContent.append("    <foo />");
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
   }
}
