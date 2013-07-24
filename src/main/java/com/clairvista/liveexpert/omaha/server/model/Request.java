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
@Table(name="requests")
public class Request {

   @Id
   @GeneratedValue
   private Integer id;
   
   @Column(name="request_id")
   private String requestID;

   @ManyToOne
   @JoinColumn(name="protocol_id")
   private Protocol protocol;

   @ManyToOne
   @JoinColumn(name="client_version_id")
   private ClientVersion clientVersion;
   
   @Column(name="is_machine")
   @Type(type="org.hibernate.type.NumericBooleanType")
   private Boolean isMachine;

   @ManyToOne
   @JoinColumn(name="user_id")
   private User user;

   @ManyToOne
   @JoinColumn(name="operating_system_id")
   private OperatingSystem operatingSystem;

   @ManyToOne
   @JoinColumn(name="session_id")
   private Session session;

   @Column(name="install_source")
   private String installSource;

   @Column(name="origin_url")
   private String originURL;

   @Column(name="created_time")
   private Date createdTime;

   public Request() {
      originURL = null;
   }
   
   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public String getRequestID() {
      return requestID;
   }

   public void setRequestID(String requestID) {
      this.requestID = requestID;
   }

   public Protocol getProtocol() {
      return protocol;
   }

   public void setProtocol(Protocol protocol) {
      this.protocol = protocol;
   }

   public ClientVersion getClientVersion() {
      return clientVersion;
   }

   public void setClientVersion(ClientVersion clientVersion) {
      this.clientVersion = clientVersion;
   }

   public Boolean getIsMachine() {
      return isMachine;
   }

   public void setIsMachine(Boolean isMachine) {
      this.isMachine = isMachine;
   }

   public User getUser() {
      return user;
   }

   public void setUser(User user) {
      this.user = user;
   }

   public OperatingSystem getOperatingSystem() {
      return operatingSystem;
   }

   public void setOperatingSystem(OperatingSystem operatingSystem) {
      this.operatingSystem = operatingSystem;
   }

   public Session getSession() {
      return session;
   }

   public void setSession(Session session) {
      this.session = session;
   }

   public String getInstallSource() {
      return installSource;
   }

   public void setInstallSource(String installSource) {
      this.installSource = installSource;
   }

   public String getOriginURL() {
      return originURL;
   }

   public void setOriginURL(String originURL) {
      this.originURL = originURL;
   }

   public Date getCreatedTime() {
      return createdTime;
   }

   public void setCreatedTime(Date createdTime) {
      this.createdTime = createdTime;
   }
   
}
