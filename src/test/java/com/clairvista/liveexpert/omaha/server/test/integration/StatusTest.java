package com.clairvista.liveexpert.omaha.server.test.integration;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
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
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:servlet-context.xml"})
@WebAppConfiguration
@Transactional
public class StatusTest {
   
   @Autowired
   private WebApplicationContext appContext;
   
   @Test
   public void codeRedCheckResponseTest() throws Exception {
       MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(appContext).build();

       MockHttpServletRequestBuilder httpRequest = MockMvcRequestBuilders.get("/status");
       MvcResult result = mockMvc.perform(httpRequest)
             .andExpect(MockMvcResultMatchers.status().isOk())
             .andReturn();
       
       MockHttpServletResponse response = result.getResponse();
       assertEquals(HttpServletResponse.SC_OK, response.getStatus());
       assertEquals("Version: 1.2.3.4\n", response.getContentAsString());
   }
}
