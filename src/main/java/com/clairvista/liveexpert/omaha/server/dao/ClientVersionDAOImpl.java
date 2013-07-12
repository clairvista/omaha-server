package com.clairvista.liveexpert.omaha.server.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.clairvista.liveexpert.omaha.server.model.ClientVersion;

@Repository
public class ClientVersionDAOImpl implements ClientVersionDAO {

   @Autowired
   private SessionFactory sessionFactory;
   
   private Session getCurrentSession() {
      return sessionFactory.getCurrentSession();
   }
   
   public ClientVersion getClientVersion(int id) {
      ClientVersion clientVersion = (ClientVersion) getCurrentSession().get(ClientVersion.class, id);
      return clientVersion;
   }

   @SuppressWarnings("unchecked")
   public ClientVersion findOrCreateClientVersion(String versionID) {
      Query query = getCurrentSession().createQuery("FROM ClientVersion WHERE versionID = :versionID");
      query.setString("versionID", versionID);
      List<ClientVersion> versions = query.list();
      
      ClientVersion version = null;
      if(versions == null || versions.isEmpty()) {
         // Create new record.
         version = new ClientVersion();
         version.setVersionID(versionID);
         getCurrentSession().save(version);
      } else if(versions.size() == 1) {
         // Found existing record.
         version = versions.get(0);
      }

      return version;
   }

   @SuppressWarnings("unchecked")
   public List<ClientVersion> getClientVersions() {
      return getCurrentSession().createQuery("FROM ClientVersion").list();
   }

}
