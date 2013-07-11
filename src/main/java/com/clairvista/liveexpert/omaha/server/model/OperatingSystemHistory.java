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
@Table(name="operating_systems_history")
public class OperatingSystemHistory {

   @Id
   @GeneratedValue
   private Integer id;

   @ManyToOne
   @JoinColumn(name="user_id")
   private User user;

   @Column(name="archived_time")
   private Date archivedTime;
   
   private String platform;

   private String version;

   @Column(name="service_pack")
   private String servicePack;

   private String architecture;

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

   public Date getArchivedTime() {
      return archivedTime;
   }

   public void setArchivedTime(Date archivedTime) {
      this.archivedTime = archivedTime;
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
   
}
