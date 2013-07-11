package com.clairvista.liveexpert.omaha.server.test.integration;

import java.util.List;

import junit.framework.Assert;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.clairvista.liveexpert.omaha.server.model.Protocol;
import com.clairvista.liveexpert.omaha.server.model.Request;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"file:src/main/webapp/WEB-INF/servlet-context.xml"}) // "test-servlet-context.xml"})
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

      String testBuilderName = "testSetup";
      String initProtocolID = "3.0";
      
      List<Protocol> protocols = session.createQuery("from Protocol where protocolID = :protocolID")
            .setString("protocolID", initProtocolID)
            .list();
      if(protocols.isEmpty()) {
         Protocol protocol = new Protocol();
         protocol.setProtocolID(initProtocolID);
         protocol.setCreatedBy(testBuilderName);
         session.save(protocol);
      }
   }
   
   @Test
   public void responseTest() throws Exception {
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
      testInput.append("    <ping r=\"1\"/>");
      testInput.append("  </app>");
      testInput.append("</request>");
      
      List<Request> requests = session.createQuery("from Request").list();
      Assert.assertEquals(0, requests.size());

      MockHttpServletRequestBuilder httpRequest = MockMvcRequestBuilders.post("/update").content(testInput.toString());
      this.mockMvc.perform(httpRequest).andExpect(MockMvcResultMatchers.status().isOk());

      requests = session.createQuery("from Request").list();
      Assert.assertEquals(1, requests.size());

      Request omahaRequest = requests.get(0);
      Assert.assertEquals("3.0", omahaRequest.getProtocol().getProtocolID());
      Assert.assertEquals("1.3.23.0", omahaRequest.getClientVersion().getVersionID());
      Assert.assertFalse(omahaRequest.getIsMachine());
      Assert.assertEquals("{5FAD27D4-6BFA-4daa-A1B3-5A1F821FEE0F}", omahaRequest.getSession().getSessionID());
      Assert.assertEquals("{D0BBD725-742D-44ae-8D46-0231E881D58E}", omahaRequest.getUser().getUserID());
      Assert.assertEquals("scheduler", omahaRequest.getInstallSource());
      Assert.assertEquals("{C8F6EDF3-B623-4ee6-B2DA-1D08A0B4C665}", omahaRequest.getRequestID());
      Assert.assertEquals(null, omahaRequest.getOriginURL());
   }
   
   // TODO: Add test to verify that the session, user, and client version are not created if one already exists. 
}
