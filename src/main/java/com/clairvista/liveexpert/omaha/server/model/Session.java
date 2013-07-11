package com.clairvista.liveexpert.omaha.server.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="sessions")
public class Session {

   @Id
   @GeneratedValue
   private Integer id;

   @Column(name="session_id")
   private String sessionID;

   @Column(name="created_time")
   private Date createdTime;

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public String getSessionID() {
      return sessionID;
   }

   public void setSessionID(String sessionID) {
      this.sessionID = sessionID;
   }

   public Date getCreatedTime() {
      return createdTime;
   }

   public void setCreatedTime(Date createdTime) {
      this.createdTime = createdTime;
   }
   
}
