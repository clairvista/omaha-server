package com.clairvista.liveexpert.omaha.server.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Element;

import com.clairvista.liveexpert.omaha.server.constants.ActionAttrs;
import com.clairvista.liveexpert.omaha.server.constants.UpdateCheckAttrs;
import com.clairvista.liveexpert.omaha.server.dao.ApplicationVersionDAO;
import com.clairvista.liveexpert.omaha.server.dao.UpdateCheckDAO;
import com.clairvista.liveexpert.omaha.server.model.Application;
import com.clairvista.liveexpert.omaha.server.model.ApplicationVersion;
import com.clairvista.liveexpert.omaha.server.model.ApplicationVersionRequest;
import com.clairvista.liveexpert.omaha.server.model.UpdateCheck;
import com.clairvista.liveexpert.omaha.server.response.ActionResponse;
import com.clairvista.liveexpert.omaha.server.response.ManifestResponse;
import com.clairvista.liveexpert.omaha.server.response.PackageResponse;
import com.clairvista.liveexpert.omaha.server.response.UpdateCheckResponse;
import com.clairvista.liveexpert.omaha.server.response.UrlResponse;
import com.clairvista.liveexpert.omaha.server.util.XMLUtils;

@Service
@Transactional
public class UpdateCheckServiceImpl implements UpdateCheckService {

   private static Logger LOGGER = Logger.getLogger(UpdateCheckServiceImpl.class);
   private static final int RESOURCE_PRESENCE_CHECK_TIMEOUT = 1000;

   @Autowired
   private UpdateCheckDAO updateCheckDAO;

   @Autowired
   private ApplicationVersionDAO applicationVersionDAO;
   
   public UpdateCheck recordUpdateCheck(ApplicationVersionRequest appRequest, Element updateCheckElem) {
      // Extract Inputs:
      String accessToken = XMLUtils.parseString(updateCheckElem, UpdateCheckAttrs.ACCESS_TOKEN);
      Boolean updateDisabled = XMLUtils.parseBoolean(updateCheckElem, UpdateCheckAttrs.UPDATE_DISABLED);
      String targetVersionPrefix = XMLUtils.parseString(updateCheckElem, UpdateCheckAttrs.TARGET_VERSION_PREFIX);
      
      // Create Event:
      UpdateCheck updateCheck = new UpdateCheck();

      updateCheck.setApplicationVersionRequest(appRequest);
      updateCheck.setAccessToken(accessToken);
      updateCheck.setUpdateDisabled(updateDisabled);
      updateCheck.setTargetVersionPrefix(targetVersionPrefix);
      
      updateCheckDAO.addUpdateCheck(updateCheck);
      
      return updateCheck;
   }

   public UpdateCheckResponse processUpdateCheck(UpdateCheck updateCheck) {
      UpdateCheckResponse response = new UpdateCheckResponse();

      ApplicationVersionRequest appRequest = updateCheck.getApplicationVersionRequest();
      if(appRequest == null) {
         LOGGER.error("Update Check request did not reference an Application Version Request.");
         response.setStatus("error-internal");
         return response;
      }
      
      ApplicationVersion appVersion = appRequest.getApplicationVersion();
      if(appVersion == null) {
         LOGGER.error("Application Version Request for Update Check request did not reference an Application Version.");
         response.setStatus("error-internal");
         return response;
      }
      
      Application app = appVersion.getApplication();
      if(app == null) {
         LOGGER.error("Application Version for Update Check request did not reference an Application.");
         response.setStatus("error-internal");
         return response;
      }
      
      // TODO: Validate the OS of the request with the application.
      
      ApplicationVersion currentVersion = applicationVersionDAO.findCurrentForApplication(app);
      if(currentVersion == null) {
         LOGGER.error("Unable to identify current version for Update Check.");
         response.setStatus("error-internal");
         return response;
      }
      
      if(currentVersion.getVersionID().equals(appVersion.getVersionID())) {
         response.setStatus("noupdate");
         return response;
      }
      
      addUpdateDetails(response, currentVersion);
      
      return response;
   }

   private void addUpdateDetails(UpdateCheckResponse response, ApplicationVersion currentVersion) {
      String downloadPath = getDownloadPath(currentVersion);
      String installerName = currentVersion.getInstallerName();
      String installerHash = currentVersion.getInstallerHash();
      Integer installerSize = currentVersion.getInstallerSize();
      
      if(isResourcePresent(downloadPath + installerName)) {
         UrlResponse url = new UrlResponse(downloadPath);
         response.addUrl(url);
         
         ManifestResponse manifest = new ManifestResponse(currentVersion.getVersionID());

         PackageResponse packageResponse = new PackageResponse();
         packageResponse.setHash(installerHash);
         packageResponse.setName(installerName);
         packageResponse.setRequired(true);
         packageResponse.setSize(installerSize);
         manifest.addPackage(packageResponse);
         
         ActionResponse action = new ActionResponse();
         action.setEvent(ActionAttrs.INSTALL_EVENT);
         manifest.addAction(action);
         response.setManifest(manifest);
      } else {
         LOGGER.error("Installer was not found at the expected location.");
         response.setStatus("error-internal");
      }
   }

   private String getDownloadPath(ApplicationVersion currentVersion) {
      String downloadBaseURL = currentVersion.getDownloadBaseURL();
      String version = currentVersion.getVersionID();
      
      return downloadBaseURL + "/" + version + "/";
   }

   private boolean isResourcePresent(String downloadURL) {
      try {
         HttpURLConnection.setFollowRedirects(false);
         HttpURLConnection conn =
            (HttpURLConnection) new URL(downloadURL).openConnection();
         conn.setConnectTimeout(RESOURCE_PRESENCE_CHECK_TIMEOUT);
         conn.setRequestMethod("HEAD");
         return (conn.getResponseCode() == HttpURLConnection.HTTP_OK);
       } catch (MalformedURLException mue) {
          LOGGER.error("Download URL was not valid: " + downloadURL, mue);
       } catch (IOException ioe) {
          LOGGER.error("Failed to verify installer at: " + downloadURL, ioe);
       }
      
      return false;
   }

}
