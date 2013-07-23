package com.clairvista.liveexpert.omaha.server.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Element;

import com.clairvista.liveexpert.omaha.server.constants.EventAttrs;
import com.clairvista.liveexpert.omaha.server.dao.EventDAO;
import com.clairvista.liveexpert.omaha.server.model.ApplicationVersionRequest;
import com.clairvista.liveexpert.omaha.server.model.Event;
import com.clairvista.liveexpert.omaha.server.util.RequestElementValidationException;
import com.clairvista.liveexpert.omaha.server.util.XMLUtils;

@Service
@Transactional
public class EventServiceImpl implements EventService {
   
   private static final String[] REQUIRED_EVENT_ATTRIBUTES = new String[]
         { EventAttrs.EVENT_TYPE, EventAttrs.EVENT_RESULT };

   private static Logger LOGGER = Logger.getLogger(EventServiceImpl.class);

   @Autowired
   private EventDAO eventDAO;

   public boolean validateEvent(Element eventElem) throws RequestElementValidationException {
      List<String> missingAttributes = 
            XMLUtils.validateRequiredAttributes(eventElem, REQUIRED_EVENT_ATTRIBUTES);
      if(!missingAttributes.isEmpty()) {
         LOGGER.warn("INVALID REQUEST -- Missing required event attributes. " +
         		"Missing attributes: " + missingAttributes);
         throw new RequestElementValidationException("Missing required Event attributes: " + missingAttributes,
               "missing:" + StringUtils.join(missingAttributes, ","));
      }
      
      return true;
   }
   
   public Event recordEvent(ApplicationVersionRequest appRequest, Element eventElem) 
         throws RequestElementValidationException {
      // Validate Inputs:
      if(!validateEvent(eventElem)) {
         throw new RequestElementValidationException("Event validation failed.", "eventValidationFailed");
      }
      
      // Extract Inputs:
      Integer eventType = XMLUtils.parseInteger(eventElem, EventAttrs.EVENT_TYPE);
      Integer eventResult = XMLUtils.parseInteger(eventElem, EventAttrs.EVENT_RESULT);
      Integer errorCode = XMLUtils.parseInteger(eventElem, EventAttrs.ERROR_CODE);
      Integer extraCode = XMLUtils.parseInteger(eventElem, EventAttrs.EXTRA_CODE);
      Integer downloadTime = XMLUtils.parseInteger(eventElem, EventAttrs.DOWNLOAD_TIME_MS);
      Integer bytesDownloaded = XMLUtils.parseInteger(eventElem, EventAttrs.DOWNLOADED);
      Integer bytesTotal = XMLUtils.parseInteger(eventElem, EventAttrs.TOTAL);
      Integer updateCheckTime = XMLUtils.parseInteger(eventElem, EventAttrs.UPDATE_CHECK_TIME_MS);
      Integer installTime = XMLUtils.parseInteger(eventElem, EventAttrs.INSTALL_TIME_MS);
      Integer sourceURLIndex = XMLUtils.parseInteger(eventElem, EventAttrs.SOURCE_URL_INDEX);
      String stateCancelled = XMLUtils.parseString(eventElem, EventAttrs.STATE_CANCELLED);
      Integer timeSinceUpdateAvailable = XMLUtils.parseInteger(eventElem, EventAttrs.TIME_SINCE_UPDATE_AVAILABLE_MS);
      Integer timeSinceDownloadStart = XMLUtils.parseInteger(eventElem, EventAttrs.TIME_SINCE_DOWNLOAD_START_MS);
      
      // Create Event:
      Event event = new Event();
      
      event.setApplicationVersionRequest(appRequest);
      event.setEventType(eventType);
      event.setEventResult(eventResult);
      event.setErrorCode(errorCode);
      event.setExtraCode(extraCode);
      event.setDownloadTimeMs(downloadTime);
      event.setBytesDownloaded(bytesDownloaded);
      event.setTotalSize(bytesTotal);
      event.setUpdateCheckTime(updateCheckTime);
      event.setInstallTimeMs(installTime);
      event.setSourceUrlIndex(sourceURLIndex);
      event.setStateCancelled(stateCancelled);
      event.setTimeSinceUpdateAvailable(timeSinceUpdateAvailable);
      event.setTimeSinceDownloadStart(timeSinceDownloadStart);
      
      eventDAO.addEvent(event);
      
      return event;
   }

}
