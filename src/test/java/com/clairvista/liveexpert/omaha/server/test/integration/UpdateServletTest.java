package com.clairvista.liveexpert.omaha.server.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.xml.DomUtils;
import org.springframework.web.context.WebApplicationContext;
import org.w3c.dom.Element;

import com.clairvista.liveexpert.omaha.server.model.Application;
import com.clairvista.liveexpert.omaha.server.model.ApplicationVersion;
import com.clairvista.liveexpert.omaha.server.model.ApplicationVersionRequest;
import com.clairvista.liveexpert.omaha.server.model.Event;
import com.clairvista.liveexpert.omaha.server.model.OperatingSystem;
import com.clairvista.liveexpert.omaha.server.model.Ping;
import com.clairvista.liveexpert.omaha.server.model.Request;
import com.clairvista.liveexpert.omaha.server.model.UpdateCheck;
import com.clairvista.liveexpert.omaha.server.test.util.TestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"file:src/test/resources/test-servlet-context.xml"})
@WebAppConfiguration
@Transactional
@SuppressWarnings("unchecked")
public class UpdateServletTest {
   
   @Autowired
   private WebApplicationContext appContext;

   private MockMvc mockMvc;

   @Autowired
   private SessionFactory sessionFactory;
   
   private org.hibernate.Session session;
   
   @Before
   public void setup() {
      session = sessionFactory.getCurrentSession();

      TestUtils.setupStaticDBContent(session);
      
      Application testApp1 = new Application("Test Application 1", "Test Application 1 Description", 
            "{430FD4D0-B729-4F61-AA34-91526481799D}", TestUtils.TEST_CREATOR_NAME);
      ApplicationVersion testAppVersion1 = new ApplicationVersion(testApp1, "1.3.23.0", 
            "http://www.test-download.com", "test-app-1.exe", "VXriGUVI0TNqfLlU02vBel4Q3Zo=", 
            23963192, TestUtils.TEST_CREATOR_NAME);
      TestUtils.populateTestData(testAppVersion1, session);
      
      Application testApp2 = new Application("Test Application 2", "Test Application 2 Description", 
            "{D0AB2EBC-931B-4013-9FEB-C9C4C2225C8C}", TestUtils.TEST_CREATOR_NAME);
      ApplicationVersion testAppVersion2 = new ApplicationVersion(testApp2, "2.2.2.0", 
            "http://www.test-download.com", "test-app-2.exe", 
            "N2VhZGRjYmNjYTFhNjU3OTcyMjU3OWE4YThhNDA3ZWI2NTA4M2ZiYyAgcG9tLnhtbAo=", 
            23963192, TestUtils.TEST_CREATOR_NAME);
      TestUtils.populateTestData(testAppVersion2, session);
   }
   
   // TODO: Split this up. Validate indivudual components.
   @Test
   public void fullValidationTest() throws Exception {
      this.mockMvc = MockMvcBuilders.webAppContextSetup(this.appContext).build();

      StringBuilder testInput = new StringBuilder();
      testInput.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      testInput.append("<request protocol=\"3.0\" version=\"1.3.23.0\" ismachine=\"0\" " +
            "sessionid=\"{5FAD27D4-6BFA-4daa-A1B3-5A1F821FEE0F}\" userid=\"{D0BBD725-742D-44ae-8D46-0231E881D58E}\" " +
            "installsource=\"scheduler\" testsource=\"ossdev\" requestid=\"{C8F6EDF3-B623-4ee6-B2DA-1D08A0B4C665}\">");
      testInput.append("  <os platform=\"win\" version=\"6.1\" sp=\"\" arch=\"x64\"/>");
      testInput.append("  <app appid=\"{430FD4D0-B729-4F61-AA34-91526481799D}\" version=\"1.3.23.0\" " +
            "nextversion=\"\" lang=\"en\" brand=\"GGLS\" client=\"someclientid\" installage=\"39\">");
      testInput.append("    <updatecheck/>");
      testInput.append("    <ping r=\"1\"/>");
      testInput.append("  </app>");
      testInput.append("  <app appid=\"{D0AB2EBC-931B-4013-9FEB-C9C4C2225C8C}\" version=\"2.2.2.0\" " +
            "nextversion=\"\" lang=\"en\" brand=\"GGLS\" client=\"\" installage=\"6\">");
      testInput.append("    <updatecheck/>");
      testInput.append("    <event eventtype=\"5\" eventresult=\"1\" errorcode=\"0\" extracode1=\"0\"/>");
      testInput.append("    <event eventtype=\"2\" eventresult=\"4\" errorcode=\"-2147219440\" extracode1=\"268435463\"/>");
      testInput.append("    <ping r=\"1\"/>");
      testInput.append("  </app>");
      testInput.append("</request>");

      MockHttpServletRequestBuilder httpRequest = MockMvcRequestBuilders.post("/update").content(testInput.toString());
      MvcResult result = this.mockMvc.perform(httpRequest)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();


      // Validate Response:
      
      Element responseElem = TestUtils.extractResponseXML(result);
      
      assertEquals("UTF-8 document returned.", "UTF-8", responseElem.getOwnerDocument().getXmlEncoding());
      
      assertEquals("Protocol used is 3.0.", "3.0", responseElem.getAttribute("protocol"));
      assertEquals("Server specifies it's type.","todo", responseElem.getAttribute("server"));
      // TODO: Assert daystart response
      
      List<Element> appElems = DomUtils.getChildElementsByTagName(responseElem, "app");
      assertEquals("Two app elements returned.", 2, appElems.size());

      Element firstAppElem = appElems.get(0);
      Element secondAppElem = appElems.get(1);
      List<Element> actionElems;
      Element actionElem;

      assertEquals("App element has proper application ID.", 
            "{430FD4D0-B729-4F61-AA34-91526481799D}", firstAppElem.getAttribute("appid"));
      actionElems = DomUtils.getChildElements(firstAppElem);
      assertEquals("First app has two action elements.", 2, actionElems.size());
      actionElem = actionElems.get(0);
      assertEquals("First action is an update check.", "updatecheck", actionElem.getNodeName());
      assertEquals("Update Check status is ok.", "noupdate", actionElem.getAttribute("status"));
      actionElem = actionElems.get(1);
      assertEquals("First action is a ping.", "ping", actionElem.getNodeName());
      assertEquals("Ping status is ok.", "ok", actionElem.getAttribute("status"));

      assertEquals("App element has proper application ID.", 
            "{D0AB2EBC-931B-4013-9FEB-C9C4C2225C8C}", secondAppElem.getAttribute("appid"));
      actionElems = DomUtils.getChildElements(secondAppElem);
      assertEquals("Second app has four action elements.", 4, actionElems.size());
      actionElem = actionElems.get(0);
      assertEquals("First action is an update check.", "updatecheck", actionElem.getNodeName());
      assertEquals("Update Check status is ok.", "noupdate", actionElem.getAttribute("status"));
      actionElem = actionElems.get(1);
      assertEquals("First action is an event.", "event", actionElem.getNodeName());
      assertEquals("Event status is ok.", "ok", actionElem.getAttribute("status"));
      actionElem = actionElems.get(2);
      assertEquals("First action is an event.", "event", actionElem.getNodeName());
      assertEquals("Event status is ok.", "ok", actionElem.getAttribute("status"));
      actionElem = actionElems.get(3);
      assertEquals("First action is a ping.", "ping", actionElem.getNodeName());
      assertEquals("Ping status is ok.", "ok", actionElem.getAttribute("status"));
      
      
      // Validate Database State:
      
      List<Request> requests = session.createQuery("FROM Request").list();
      Request omahaRequest = requests.get(requests.size() - 1);

      assertEquals("3.0", omahaRequest.getProtocol().getProtocolID());
      assertEquals("1.3.23.0", omahaRequest.getClientVersion().getVersionID());
      assertFalse(omahaRequest.getIsMachine());
      assertEquals("{5FAD27D4-6BFA-4daa-A1B3-5A1F821FEE0F}", omahaRequest.getSession().getSessionID());
      assertEquals("{D0BBD725-742D-44ae-8D46-0231E881D58E}", omahaRequest.getUser().getUserID());
      assertEquals("scheduler", omahaRequest.getInstallSource());
      assertEquals("{C8F6EDF3-B623-4ee6-B2DA-1D08A0B4C665}", omahaRequest.getRequestID());
      assertEquals(null, omahaRequest.getOriginURL());
      
      OperatingSystem os = omahaRequest.getOperatingSystem();
      assertNotNull(os);
      assertEquals("win", os.getPlatform());
      assertEquals("6.1", os.getVersion());
      assertEquals(null, os.getServicePack());
      assertEquals("x64", os.getArchitecture());

      List<ApplicationVersionRequest> appRequests = session.createQuery(
            "FROM ApplicationVersionRequest WHERE request = :requestID")
      		.setInteger("requestID", omahaRequest.getId())
      		.list();
      assertEquals("Two application requests were recorded.",  2, appRequests.size());
      ApplicationVersionRequest firstAppRequest = appRequests.get(0);
      ApplicationVersionRequest secondAppRequest = appRequests.get(1);
      ApplicationVersion appVersion;

      appVersion = firstAppRequest.getApplicationVersion();
      assertEquals("1.3.23.0", appVersion.getVersionID());
      assertEquals(null, firstAppRequest.getNextVersion());
      assertEquals("en", firstAppRequest.getLanguage());
      assertEquals("GGLS", firstAppRequest.getBrand());
      assertEquals("someclientid", firstAppRequest.getClient());
      assertEquals(39, (int)firstAppRequest.getInstallAge());


      appVersion = secondAppRequest.getApplicationVersion();
      assertEquals("2.2.2.0", appVersion.getVersionID());
      assertEquals(null, secondAppRequest.getNextVersion());
      assertEquals("en", secondAppRequest.getLanguage());
      assertEquals("GGLS", secondAppRequest.getBrand());
      assertEquals(null, secondAppRequest.getClient());
      assertEquals(6, (int)secondAppRequest.getInstallAge());
      
      List<UpdateCheck> updateChecks;
      UpdateCheck updateCheck;
      List<Ping> pings;
      Ping ping;
      List<Event> events;
      Event event;

      updateChecks = session.createQuery(
            "FROM UpdateCheck WHERE applicationVersionRequest = :appRequestID")
            .setInteger("appRequestID", firstAppRequest.getId())
            .list();
      assertEquals("One Update Check recorded for first application request.",  1, updateChecks.size());
      
      updateCheck = updateChecks.get(0);
      assertEquals(null, updateCheck.getTargetVersionPrefix());
      assertEquals(null, updateCheck.getUpdateDisabled());
      

      pings = session.createQuery(
            "FROM Ping WHERE applicationVersionRequest = :appRequestID")
            .setInteger("appRequestID", firstAppRequest.getId())
            .list();
      assertEquals("One Ping recorded for first application request.",  1, pings.size());
      
      ping = pings.get(0);
      assertEquals(null, ping.getWasActive());
      assertEquals(null, ping.getLastActive());
      assertEquals(1, (int)ping.getLastPresent());

      updateChecks = session.createQuery(
            "FROM UpdateCheck WHERE applicationVersionRequest = :appRequestID")
            .setInteger("appRequestID", secondAppRequest.getId())
            .list();
      assertEquals("One Update Check recorded for second application request.",  1, updateChecks.size());
      
      updateCheck = updateChecks.get(0);
      assertEquals(null, updateCheck.getTargetVersionPrefix());
      assertEquals(null, updateCheck.getUpdateDisabled());

      events = session.createQuery(
            "FROM Event WHERE applicationVersionRequest = :appRequestID")
            .setInteger("appRequestID", secondAppRequest.getId())
            .list();
      assertEquals("Two Event recorded for second application request.",  2, events.size());
      
      event = events.get(0);
      assertEquals(5, (int)event.getEventType());
      assertEquals(1, (int)event.getEventResult());
      assertEquals(0, (int)event.getErrorCode());
      assertEquals(0, (int)event.getExtraCode());
      assertEquals(null, event.getBytesDownloaded());
      assertEquals(null, event.getTotalSize());
      
      event = events.get(1);
      assertEquals(2, (int)event.getEventType());
      assertEquals(4, (int)event.getEventResult());
      assertEquals(-2147219440, (int)event.getErrorCode());
      assertEquals(268435463, (int)event.getExtraCode());
      assertEquals(null, event.getBytesDownloaded());
      assertEquals(null, event.getTotalSize());
      

      pings = session.createQuery(
            "FROM Ping WHERE applicationVersionRequest = :appRequestID")
            .setInteger("appRequestID", secondAppRequest.getId())
            .list();
      assertEquals("One Ping recorded for second application request.",  1, pings.size());
      
      ping = pings.get(0);
      assertEquals(null, ping.getWasActive());
      assertEquals(null, ping.getLastActive());
      assertEquals(1, (int)ping.getLastPresent());
   }
   
   // TODO: Add test to verify that the session, user, and client version are not created if one already exists. 
}
