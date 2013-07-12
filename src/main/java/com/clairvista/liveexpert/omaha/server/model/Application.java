package com.clairvista.liveexpert.omaha.server.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="applications")
public class Application {

   @Id
   @GeneratedValue
   private Integer id;

   private String name;

   private String description;

   @Column(name="app_id")
   private String appID;

   @Column(name="created_time")
   private Date createdTime;

   @Column(name="created_by")
   private String createdBy;
   
   public Application() { }
   
   public Application(String name, String description, String appID, String creator) {
      this.name = name;
      this.description = description;
      this.appID = appID;
      createdBy = creator;
   }

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public String getAppID() {
      return appID;
   }

   public void setAppID(String appID) {
      this.appID = appID;
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
   
}
