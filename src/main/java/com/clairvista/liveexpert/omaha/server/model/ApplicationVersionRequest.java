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
@Table(name="application_version_requests")
public class ApplicationVersionRequest {

   @Id
   @GeneratedValue
   private Integer id;

   @ManyToOne
   @JoinColumn(name="application_version_id")
   private ApplicationVersion applicationVersion;

   @ManyToOne
   @JoinColumn(name="request_id")
   private Request request;
   
   @Column(name="next_version")
   private String nextVersion;

   private String language;

   private String brand;
   
   private String client;

   @Column(name="additional_params")
   private String additionalParams;

   private String experiments;

   @Column(name="install_id")
   private String installID;

   @Column(name="install_age")
   private Integer installAge;

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

   public String getNextVersion() {
      return nextVersion;
   }

   public void setNextVersion(String nextVersion) {
      this.nextVersion = nextVersion;
   }

   public String getLanguage() {
      return language;
   }

   public void setLanguage(String language) {
      this.language = language;
   }

   public String getBrand() {
      return brand;
   }

   public void setBrand(String brand) {
      this.brand = brand;
   }

   public String getClient() {
      return client;
   }

   public void setClient(String client) {
      this.client = client;
   }

   public String getAdditionalParams() {
      return additionalParams;
   }

   public void setAdditionalParams(String additionalParams) {
      this.additionalParams = additionalParams;
   }

   public String getExperiments() {
      return experiments;
   }

   public void setExperiments(String experiments) {
      this.experiments = experiments;
   }

   public String getInstallID() {
      return installID;
   }

   public void setInstallID(String installID) {
      this.installID = installID;
   }

   public Integer getInstallAge() {
      return installAge;
   }

   public void setInstallAge(Integer installAge) {
      this.installAge = installAge;
   }

   public Date getCreatedTime() {
      return createdTime;
   }

   public void setCreatedTime(Date createdTime) {
      this.createdTime = createdTime;
   }
   
}
