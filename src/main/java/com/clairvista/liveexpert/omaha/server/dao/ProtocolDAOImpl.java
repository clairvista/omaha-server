package com.clairvista.liveexpert.omaha.server.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.clairvista.liveexpert.omaha.server.model.Protocol;

@Repository
public class ProtocolDAOImpl implements ProtocolDAO {

   @Autowired
   private SessionFactory sessionFactory;
   
   private Session getCurrentSession() {
      return sessionFactory.getCurrentSession();
   }
   
   public Protocol getProtocol(int id) {
      Protocol protocol = (Protocol) getCurrentSession().get(Protocol.class, id);
      return protocol;
   }

   @SuppressWarnings("unchecked")
   public Protocol findProtocol(String protocolID) {
      Query query = getCurrentSession().createQuery("FROM Protocol WHERE protocolID = :protocolID");
      query.setString("protocolID", protocolID);
      List<Protocol> protocols = query.list();
      
      Protocol protocol = null;
      if(protocols != null && protocols.size() == 1) {
         protocol = protocols.get(0);
      }

      return protocol;
   }

   @SuppressWarnings("unchecked")
   public List<Protocol> getProtocols() {
      return getCurrentSession().createQuery("FROM Protocol").list();
   }

}
