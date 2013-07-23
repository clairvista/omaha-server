package com.clairvista.liveexpert.omaha.server.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="operating_systems")
public class OperatingSystem {

   @Id
   @GeneratedValue
   private Integer id;

   private String platform;

   private String version;

   @Column(name="service_pack")
   private String servicePack;

   private String architecture;

   @Column(name="created_time")
   private Date createdTime;

   public OperatingSystem() { }
   
   public OperatingSystem(String platform, String version, String servicePack, String architecture) {
      this.platform = platform;
      this.version = version;
      this.servicePack = servicePack;
      this.architecture = architecture;
   }
   
   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public String getPlatform() {
      return platform;
   }

   public void setPlatform(String platform) {
      this.platform = platform;
   }

   public String getVersion() {
      return version;
   }

   public void setVersion(String version) {
      this.version = version;
   }

   public String getServicePack() {
      return servicePack;
   }

   public void setServicePack(String servicePack) {
      this.servicePack = servicePack;
   }

   public String getArchitecture() {
      return architecture;
   }

   public void setArchitecture(String architecture) {
      this.architecture = architecture;
   }

   public Date getCreatedTime() {
      return createdTime;
   }

   public void setCreatedTime(Date createdTime) {
      this.createdTime = createdTime;
   }
   
}
