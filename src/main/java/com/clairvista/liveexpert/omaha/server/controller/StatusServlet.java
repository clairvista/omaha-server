package com.clairvista.liveexpert.omaha.server.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class StatusServlet {

    public static String OMAHA_VERSION_KEY = "omaha.version";

    private static Logger LOGGER = Logger.getLogger(StatusServlet.class);
    
    @Resource
    private Environment env;
    
    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public @ResponseBody
    String processOmahaRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
            throws ServletException, IOException {
        LOGGER.debug("Status check request received.");
        
        String omahaVersion = env.getProperty(OMAHA_VERSION_KEY);
        if(omahaVersion == null) { omahaVersion = "unknown"; }
        
        StringBuilder response = new StringBuilder();
        response.append("Version: " + omahaVersion + "\n");
        
        httpResponse.setStatus(HttpServletResponse.SC_OK);
        return response.toString();
    }

}
