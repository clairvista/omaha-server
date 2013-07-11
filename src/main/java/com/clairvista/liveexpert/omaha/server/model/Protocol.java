package com.clairvista.liveexpert.omaha.server.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="protocols")
public class Protocol {

   @Id
   @GeneratedValue
   private Integer id;

   @Column(name="protocol_id")
   private String protocolID;

   @Column(name="created_time")
   private Date createdTime;

   @Column(name="created_by")
   private String createdBy;

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public String getProtocolID() {
      return protocolID;
   }

   public void setProtocolID(String protocolID) {
      this.protocolID = protocolID;
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
