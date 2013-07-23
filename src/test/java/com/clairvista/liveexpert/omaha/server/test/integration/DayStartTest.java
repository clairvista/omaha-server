package com.clairvista.liveexpert.omaha.server.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.xml.DomUtils;
import org.springframework.web.context.WebApplicationContext;
import org.w3c.dom.Element;

import com.clairvista.liveexpert.omaha.server.model.Application;
import com.clairvista.liveexpert.omaha.server.model.ApplicationVersion;
import com.clairvista.liveexpert.omaha.server.test.util.TestUtils;
import com.clairvista.liveexpert.omaha.server.test.util.TestWebAppConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"file:src/test/resources/test-servlet-context.xml"})
@ComponentScan(basePackageClasses={TestWebAppConfig.class})
@WebAppConfiguration
@Transactional
public class DayStartTest {
   
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
      TestUtils.populateTestData(testAppVersion0, session);
   }
   
   @Test
   public void successResponseTest() throws Exception {
      StringBuilder testRequestContent = new StringBuilder();
      testRequestContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      testRequestContent.append("<request protocol=\"3.0\" version=\"1.0.0.0\" ismachine=\"1\" " +
            "sessionid=\"{session-1234}\" requestid=\"{request-1234}\">");
      testRequestContent.append("  <os platform=\"mac\" version=\"MacOSX\"/>");
      testRequestContent.append("  <app appid=\"{unknown-app-1234}\" lang=\"en\">");
      testRequestContent.append("    <ping/>");
      testRequestContent.append("  </app>");
      testRequestContent.append("</request>");
      
      int initSecondsSinceMidnight = calcSecondsSinceMidnight();
      
      Element responseElem = TestUtils.submitTestRequest(testRequestContent, this.appContext);

      int finalSecondsSinceMidnight = calcSecondsSinceMidnight();

      List<Element> appElems = DomUtils.getChildElementsByTagName(responseElem, "daystart");
      assertEquals(1, appElems.size());
      
      Element appElem = appElems.get(0);
      Integer returnedSecondsSinceMidnight = Integer.parseInt(appElem.getAttribute("elapsed_seconds"));
      assertTrue(initSecondsSinceMidnight <= returnedSecondsSinceMidnight);
      assertTrue(finalSecondsSinceMidnight >= returnedSecondsSinceMidnight);
   }

   private int calcSecondsSinceMidnight() {
      Calendar currentTime = Calendar.getInstance(); //now

      Calendar midnightTime = Calendar.getInstance(); //midnight
      midnightTime.set(Calendar.HOUR_OF_DAY, 0);
      midnightTime.set(Calendar.MINUTE, 0);
      midnightTime.set(Calendar.SECOND, 0);
      midnightTime.set(Calendar.MILLISECOND, 0);

      int diff = (int) (currentTime.getTimeInMillis() - midnightTime.getTimeInMillis()) ;
      return diff / 1000;
   }
}
