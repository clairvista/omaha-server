package com.clairvista.liveexpert.omaha.server.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name="update_checks")
public class UpdateCheck {

   @Id
   @GeneratedValue
   private Integer id;

   @ManyToOne
   @JoinColumn(name="application_version_request_id")
   private ApplicationVersionRequest applicationVersionRequest;

   @Column(name="access_token")
   private String accessToken;

   @Column(name="update_disabled")
   @Type(type="org.hibernate.type.NumericBooleanType")
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

   public ApplicationVersionRequest getApplicationVersionRequest() {
      return applicationVersionRequest;
   }

   public void setApplicationVersionRequest(ApplicationVersionRequest applicationVersionRequest) {
      this.applicationVersionRequest = applicationVersionRequest;
   }

   public String getAccessToken() {
      return accessToken;
   }

   public void setAccessToken(String accessToken) {
      this.accessToken = accessToken;
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
