package com.clairvista.liveexpert.omaha.server.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.log4j.Logger;

@Entity
@Table(name="application_versions")
public class ApplicationVersion {

   private static Logger LOGGER = Logger.getLogger(ApplicationVersion.class);
   
   @Id
   @GeneratedValue
   private Integer id;

   @Column(name="version_id")
   private String versionID;

   @ManyToOne
   @JoinColumn(name="application_id")
   private Application application;

   @Column(name="access_token")
   private String accessToken;

   @Column(name="download_base_url")
   private String downloadBaseURL;

   @Column(name="installer_name")
   private String installerName;

   @Column(name="installer_hash")
   private String installerHash;

   @Column(name="installer_size")
   private Integer installerSize;

   @Column(name="created_time")
   private Date createdTime;

   @Column(name="created_by")
   private String createdBy;

   public ApplicationVersion() { }

   public ApplicationVersion(Application app, String versionID, String downloadBaseURL, String installerName,
         String installerHash, int installerSize, String creator) {
      application = app;
      this.versionID = versionID;
      this.downloadBaseURL = downloadBaseURL;
      this.installerName = installerName;
      this.installerHash = installerHash;
      this.installerSize = installerSize;
      createdBy = creator;
   }

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public String getVersionID() {
      return versionID;
   }

   public void setVersionID(String versionID) {
      this.versionID = versionID;
   }

   public Application getApplication() {
      return application;
   }

   public void setApplication(Application application) {
      this.application = application;
   }

   public String getAccessToken() {
      return accessToken;
   }

   public void setAccessToken(String accessToken) {
      this.accessToken = accessToken;
   }

   public String getDownloadBaseURL() {
      return downloadBaseURL;
   }

   public void setDownloadBaseURL(String downloadBaseURL) {
      this.downloadBaseURL = downloadBaseURL;
   }

   public String getInstallerName() {
      return installerName;
   }

   public void setInstallerName(String installerName) {
      this.installerName = installerName;
   }

   public String getInstallerHash() {
      return installerHash;
   }

   public void setInstallerHash(String installerHash) {
      this.installerHash = installerHash;
   }

   public Integer getInstallerSize() {
      return installerSize;
   }

   public void setInstallerSize(Integer installerSize) {
      this.installerSize = installerSize;
   }

   public Date getCreatedTime() {
      return createdTime;
   }

   public void setCreatedTime(Date createdTime) {
      this.createdTime = createdTime;
   }

   public String getCreatedBy() {
      return createdBy;
   }

   public void setCreatedBy(String createdBy) {
      this.createdBy = createdBy;
   }

   /**
    * Splits the version number on '.'s and converts it into a list of integers.
    * 
    * @return The list of integers representing the version number.
    */
   public List<Integer> getParsedVersionID() {
      List<Integer >parsedVersion = new ArrayList<Integer>();
      if(this.versionID == null || this.versionID.isEmpty()) {
         return parsedVersion;
      }
      
      String[] splitVersion = this.versionID.split("\\.");
      for(String tupleString : splitVersion) {
         Integer tuple = 0;
         try {
            tuple = Integer.parseInt(tupleString);
         } catch (NumberFormatException nfe) {
            LOGGER.warn("Unable to parse version tuple '" + tupleString + "' of application version " + this.id);
         }
         parsedVersion.add(tuple);
      }
      
      return parsedVersion;
   }
   
}
