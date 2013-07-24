package com.clairvista.liveexpert.omaha.server.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.clairvista.liveexpert.omaha.server.model.Ping;

@Repository
public class PingDAOImpl implements PingDAO {
   
   @Autowired
   private SessionFactory sessionFactory;
   
   private Session getCurrentSession() {
      return sessionFactory.getCurrentSession();
   }

   public void addPing(Ping ping) {
      getCurrentSession().save(ping);
   }

   public void updatePing(Ping ping) {
      Ping pingToUpdate = getPing(ping.getId());

      if (pingToUpdate != null) {
         pingToUpdate.setWasActive(ping.getWasActive());
         pingToUpdate.setLastActive(ping.getLastActive());
         pingToUpdate.setLastPresent(ping.getLastPresent());

         getCurrentSession().update(pingToUpdate);
      }
   }

   public Ping getPing(int id) {
      Ping ping = (Ping) getCurrentSession().get(Ping.class, id);
      return ping;
   }

   public void deletePing(int id) {
      Ping ping = getPing(id);
      if (ping != null)
         getCurrentSession().delete(ping);
   }

   @SuppressWarnings("unchecked")
   public List<Ping> getPings() {
      return getCurrentSession().createQuery("FROM Ping").list();
   }

}
