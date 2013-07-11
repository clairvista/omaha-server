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
@Table(name="update_checks")
public class UpdateCheck {

   @Id
   @GeneratedValue
   private Integer id;

   @ManyToOne
   @JoinColumn(name="application_version_id")
   private ApplicationVersion applicationVersion;

   @ManyToOne
   @JoinColumn(name="request_id")
   private Request request;

   @Column(name="update_disabled")
   private Boolean updateDisabled;

   @Column(name="target_version_prefix")
   private String targetVersionPrefix;

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

   public Boolean getUpdateDisabled() {
      return updateDisabled;
   }

   public void setUpdateDisabled(Boolean updateDisabled) {
      this.updateDisabled = updateDisabled;
   }

   public String getTargetVersionPrefix() {
      return targetVersionPrefix;
   }

   public void setTargetVersionPrefix(String targetVersionPrefix) {
      this.targetVersionPrefix = targetVersionPrefix;
   }

   public Date getCreatedTime() {
      return createdTime;
   }

   public void setCreatedTime(Date createdTime) {
      this.createdTime = createdTime;
   }
   
}
