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
import com.clairvista.liveexpert.omaha.server.model.Event;
import com.clairvista.liveexpert.omaha.server.model.Request;
import com.clairvista.liveexpert.omaha.server.test.util.TestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:servlet-context.xml"})
@WebAppConfiguration
@Transactional
public class EventTest {
   
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
      testRequestContent.append("  <os platform=\"mac\" version=\"MacOSX\"/>");
      testRequestContent.append("  <app appid=\"{test-app-1234}\" lang=\"en\" >");
      testRequestContent.append("    <event />");
      testRequestContent.append("  </app>");
      testRequestContent.append("</request>");
      
      Element responseElem = TestUtils.submitTestRequest(testRequestContent, this.appContext);

      List<Element> appElems = DomUtils.getChildElementsByTagName(responseElem, "app");
      assertEquals(1, appElems.size());

      List<Element> eventElems = DomUtils.getChildElementsByTagName(appElems.get(0), "event");
      assertEquals(1, eventElems.size());

      Element eventElem;
      eventElem = eventElems.get(0);
      assertEquals("error-validationFailure", eventElem.getAttribute("status"));
      assertEquals("missing:eventtype,eventresult", eventElem.getAttribute("errorDetails"));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void missingAttrDataCaptureTest() throws Exception {
      StringBuilder testRequestContent = new StringBuilder();
      testRequestContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      testRequestContent.append("<request protocol=\"3.0\" version=\"1.0.0.0\" ismachine=\"1\" " +
            "sessionid=\"{session-1234}\" requestid=\"{request-1234}\">");
      testRequestContent.append("  <os platform=\"mac\" version=\"MacOSX\"/>");
      testRequestContent.append("  <app appid=\"{test-app-1234}\" lang=\"en\" >");
      testRequestContent.append("    <event />");
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
      
      List<Event> events = session.createQuery(
            "FROM Event WHERE applicationVersionRequest = :appRequestID ORDER BY createdTime ASC")
            .setInteger("appRequestID", appRequest.getId())
            .list();
      assertEquals(0, events.size());
   }

   @Test
   public void successResponseTest() throws Exception {
      StringBuilder testRequestContent = new StringBuilder();
      testRequestContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      testRequestContent.append("<request protocol=\"3.0\" version=\"1.0.0.0\" ismachine=\"1\" " +
            "sessionid=\"{session-1234}\" requestid=\"{request-1234}\">");
      testRequestContent.append("  <os platform=\"mac\" version=\"MacOSX\"/>");
      testRequestContent.append("  <app appid=\"{test-app-1234}\" lang=\"en\" >");
      testRequestContent.append("    <event eventtype=\"0\" eventresult=\"0\" errorcode=\"-1\" extracode1=\"-1\" " +
            "download_time_ms=\"0\" downloaded=\"1234\" total=\"123456\" update_check_time_ms=\"12\" " +
            "install_time_ms=\"123\" source_url_index=\"1234\" state_cancelled=\"installing\" " +
            "time_since_update_available_ms=\"12345\" time_since_download_start_ms=\"1234\" />");
      testRequestContent.append("    <event eventtype=\"6\" eventresult=\"4\" errorcode=\"726\" extracode1=\"624185\" />");
      testRequestContent.append("    <event eventtype=\"103\" eventresult=\"10\" />");
      testRequestContent.append("  </app>");
      testRequestContent.append("</request>");
      
      Element responseElem = TestUtils.submitTestRequest(testRequestContent, this.appContext);

      List<Element> appElems = DomUtils.getChildElementsByTagName(responseElem, "app");
      assertEquals(1, appElems.size());

      List<Element> eventElems = DomUtils.getChildElementsByTagName(appElems.get(0), "event");
      assertEquals(3, eventElems.size());

      Element eventElem;
      eventElem = eventElems.get(0);
      assertEquals("ok", eventElem.getAttribute("status"));

      eventElem = eventElems.get(1);
      assertEquals("ok", eventElem.getAttribute("status"));

      eventElem = eventElems.get(2);
      assertEquals("ok", eventElem.getAttribute("status"));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void successDataCaptureTest() throws Exception {
      StringBuilder testRequestContent = new StringBuilder();
      testRequestContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      testRequestContent.append("<request protocol=\"3.0\" version=\"1.0.0.0\" ismachine=\"1\" " +
            "sessionid=\"{session-1234}\" requestid=\"{request-1234}\">");
      testRequestContent.append("  <os platform=\"mac\" version=\"MacOSX\"/>");
      testRequestContent.append("  <app appid=\"{test-app-1234}\" lang=\"en\" >");
      testRequestContent.append("    <event eventtype=\"0\" eventresult=\"0\" errorcode=\"-1\" extracode1=\"-1\" " +
            "download_time_ms=\"0\" downloaded=\"1234\" total=\"123456\" update_check_time_ms=\"12\" " +
            "install_time_ms=\"123\" source_url_index=\"1234\" state_cancelled=\"installing\" " +
            "time_since_update_available_ms=\"12345\" time_since_download_start_ms=\"1234\" />");
      testRequestContent.append("    <event eventtype=\"6\" eventresult=\"4\" errorcode=\"726\" extracode1=\"624185\" />");
      testRequestContent.append("    <event eventtype=\"103\" eventresult=\"10\" />");
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
      
      List<Event> events = session.createQuery(
            "FROM Event WHERE applicationVersionRequest = :appRequestID ORDER BY createdTime ASC")
            .setInteger("appRequestID", appRequest.getId())
            .list();
      assertEquals(3, events.size());
      
      Event event;
      event = events.get(0);
      assertEquals(0, (int) event.getEventType());
      assertEquals(0, (int) event.getEventResult());
      assertEquals(-1, (int) event.getErrorCode());
      assertEquals(-1, (int) event.getExtraCode());
      assertEquals(0, (int) event.getDownloadTimeMs());
      assertEquals(1234, (int) event.getBytesDownloaded());
      assertEquals(123456, (int) event.getTotalSize());
      assertEquals(12, (int) event.getUpdateCheckTime());
      assertEquals(123, (int) event.getInstallTimeMs());
      assertEquals(1234, (int) event.getSourceUrlIndex());
      assertEquals("installing", event.getStateCancelled());
      assertEquals(12345, (int) event.getTimeSinceUpdateAvailable());
      assertEquals(1234, (int) event.getTimeSinceDownloadStart());

      event = events.get(1);
      assertEquals(6, (int) event.getEventType());
      assertEquals(4, (int) event.getEventResult());
      assertEquals(726, (int) event.getErrorCode());
      assertEquals(624185, (int) event.getExtraCode());
      assertEquals(null, event.getDownloadTimeMs());
      assertEquals(null, event.getBytesDownloaded());
      assertEquals(null, event.getTotalSize());
      assertEquals(null, event.getUpdateCheckTime());
      assertEquals(null, event.getInstallTimeMs());
      assertEquals(null, event.getSourceUrlIndex());
      assertEquals(null, event.getStateCancelled());
      assertEquals(null, event.getTimeSinceUpdateAvailable());
      assertEquals(null, event.getTimeSinceDownloadStart());

      event = events.get(2);
      assertEquals(103, (int) event.getEventType());
      assertEquals(10, (int) event.getEventResult());
      assertEquals(null, event.getErrorCode());
      assertEquals(null, event.getExtraCode());
      assertEquals(null, event.getDownloadTimeMs());
      assertEquals(null, event.getBytesDownloaded());
      assertEquals(null, event.getTotalSize());
      assertEquals(null, event.getUpdateCheckTime());
      assertEquals(null, event.getInstallTimeMs());
      assertEquals(null, event.getSourceUrlIndex());
      assertEquals(null, event.getStateCancelled());
      assertEquals(null, event.getTimeSinceUpdateAvailable());
      assertEquals(null, event.getTimeSinceDownloadStart());
   }
}
