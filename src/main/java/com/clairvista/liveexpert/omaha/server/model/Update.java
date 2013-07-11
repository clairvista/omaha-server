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
@Table(name="updates")
public class Update {

   @Id
   @GeneratedValue
   private Integer id;

   @ManyToOne
   @JoinColumn(name="user_id")
   private User user;

   @ManyToOne
   @JoinColumn(name="source_version_id")
   private ApplicationVersion sourceVersion;

   @ManyToOne
   @JoinColumn(name="target_version_id")
   private ApplicationVersion targetVersion;

   private String language;

   private String brand;

   @Column(name="additional_params")
   private String additionalParams;

   private String experiments;

   @Column(name="created_time")
   private Date createdTime;

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public User getUser() {
      return user;
   }

   public void setUser(User user) {
      this.user = user;
   }

   public ApplicationVersion getSourceVersion() {
      return sourceVersion;
   }

   public void setSourceVersion(ApplicationVersion sourceVersion) {
      this.sourceVersion = sourceVersion;
   }

   public ApplicationVersion getTargetVersion() {
      return targetVersion;
   }

   public void setTargetVersion(ApplicationVersion targetVersion) {
      this.targetVersion = targetVersion;
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

   public Date getCreatedTime() {
      return createdTime;
   }

   public void setCreatedTime(Date createdTime) {
      this.createdTime = createdTime;
   }
   
}
