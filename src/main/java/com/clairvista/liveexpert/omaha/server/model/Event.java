package com.clairvista.liveexpert.omaha.server.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="events")
public class Event {

   @Id
   @GeneratedValue
   private Integer id;

   @ManyToOne
   @JoinColumn(name="application_version_id")
   private ApplicationVersion applicationVersion;

   @ManyToOne
   @JoinColumn(name="request_id")
   private Request request;

   @Column(name="event_type")
   private Integer eventType;

   @Column(name="event_result")
   private Integer eventResult;

   @Column(name="error_code")
   private Integer errorCode;

   @Column(name="extra_code")
   private Integer extraCode;
   
   @Column(name="updateCheckTime")
   private Integer updateCheckTime;

   @Column(name="download_time_ms")
   private Integer downloadTimeMs;

   @Column(name="install_time_ms")
   private Integer installTimeMs;

   @Column(name="bytes_downloaded")
   private Integer bytesDownloaded;

   @Column(name="total_size")
   private Integer totalSize;

   @Column(name="source_url_index")
   private Integer sourceUrlIndex;

   @Column(name="state_cancelled")
   private Integer stateCancelled;

   @Column(name="time_since_update_available")
   private Integer timeSinceUpdateAvailable;

   @Column(name="time_since_download_start")
   private Integer timeSinceDownloadStart;

   @Column(name="created_time")
   private Date createdTime;

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public ApplicationVersion getApplicationVersion() {
      return applicationVersion;
   }

   public void setApplicationVersion(ApplicationVersion applicationVersion) {
      this.applicationVersion = applicationVersion;
   }

   public Request getRequest() {
      return request;
   }

   public void setRequest(Request request) {
      this.request = request;
   }

   public Integer getEventType() {
      return eventType;
   }

   public void setEventType(Integer eventType) {
      this.eventType = eventType;
   }

   public Integer getEventResult() {
      return eventResult;
   }

   public void setEventResult(Integer eventResult) {
      this.eventResult = eventResult;
   }

   public Integer getErrorCode() {
      return errorCode;
   }

   public void setErrorCode(Integer errorCode) {
      this.errorCode = errorCode;
   }

   public Integer getExtraCode() {
      return extraCode;
   }

   public void setExtraCode(Integer extraCode) {
      this.extraCode = extraCode;
   }

   public Integer getUpdateCheckTime() {
      return updateCheckTime;
   }

   public void setUpdateCheckTime(Integer updateCheckTime) {
      this.updateCheckTime = updateCheckTime;
   }

   public Integer getDownloadTimeMs() {
      return downloadTimeMs;
   }

   public void setDownloadTimeMs(Integer downloadTimeMs) {
      this.downloadTimeMs = downloadTimeMs;
   }

   public Integer getInstallTimeMs() {
      return installTimeMs;
   }

   public void setInstallTimeMs(Integer installTimeMs) {
      this.installTimeMs = installTimeMs;
   }

   public Integer getBytesDownloaded() {
      return bytesDownloaded;
   }

   public void setBytesDownloaded(Integer bytesDownloaded) {
      this.bytesDownloaded = bytesDownloaded;
   }

   public Integer getTotalSize() {
      return totalSize;
   }

   public void setTotalSize(Integer totalSize) {
      this.totalSize = totalSize;
   }

   public Integer getSourceUrlIndex() {
      return sourceUrlIndex;
   }

   public void setSourceUrlIndex(Integer sourceUrlIndex) {
      this.sourceUrlIndex = sourceUrlIndex;
   }

   public Integer getStateCancelled() {
      return stateCancelled;
   }

   public void setStateCancelled(Integer stateCancelled) {
      this.stateCancelled = stateCancelled;
   }

   public Integer getTimeSinceUpdateAvailable() {
      return timeSinceUpdateAvailable;
   }

   public void setTimeSinceUpdateAvailable(Integer timeSinceUpdateAvailable) {
      this.timeSinceUpdateAvailable = timeSinceUpdateAvailable;
   }

   public Integer getTimeSinceDownloadStart() {
      return timeSinceDownloadStart;
   }

   public void setTimeSinceDownloadStart(Integer timeSinceDownloadStart) {
      this.timeSinceDownloadStart = timeSinceDownloadStart;
   }

   public Date getCreatedTime() {
      return createdTime;
   }

   public void setCreatedTime(Date createdTime) {
      this.createdTime = createdTime;
   }
   
}
