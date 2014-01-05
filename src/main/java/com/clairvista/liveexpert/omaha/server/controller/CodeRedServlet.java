package com.clairvista.liveexpert.omaha.server.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CodeRedServlet {

    private static Logger LOGGER = Logger.getLogger(CodeRedServlet.class);

    @RequestMapping(value = "/check", method = RequestMethod.POST)
    public @ResponseBody
    String processOmahaRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
            throws ServletException, IOException {
        LOGGER.debug("Code Red check request received.");
        
        httpResponse.setStatus(HttpServletResponse.SC_OK);
        return "unsupported request";
    }

}
