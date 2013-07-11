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
@Table(name="pings")
public class Ping {

   @Id
   @GeneratedValue
   private Integer id;

   @ManyToOne
   @JoinColumn(name="application_version_id")
   private ApplicationVersion applicationVersion;

   @ManyToOne
   @JoinColumn(name="request_id")
   private Request request;

   @Column(name="was_active")
   private Boolean wasActive;

   @Column(name="last_active")
   private Integer lastActive;

   @Column(name="last_present")
   private Integer lastPresent;

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

   public Boolean getWasActive() {
      return wasActive;
   }

   public void setWasActive(Boolean wasActive) {
      this.wasActive = wasActive;
   }

   public Integer getLastActive() {
      return lastActive;
   }

   public void setLastActive(Integer lastActive) {
      this.lastActive = lastActive;
   }

   public Integer getLastPresent() {
      return lastPresent;
   }

   public void setLastPresent(Integer lastPresent) {
      this.lastPresent = lastPresent;
   }

   public Date getCreatedTime() {
      return createdTime;
   }

   public void setCreatedTime(Date createdTime) {
      this.createdTime = createdTime;
   }
   
}
