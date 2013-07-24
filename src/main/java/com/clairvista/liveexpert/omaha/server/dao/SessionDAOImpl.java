package com.clairvista.liveexpert.omaha.server.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.clairvista.liveexpert.omaha.server.model.Session;

@Repository
public class SessionDAOImpl implements SessionDAO {
   
   @Autowired
   private SessionFactory sessionFactory;
   
   private org.hibernate.Session getCurrentSession() {
      return sessionFactory.getCurrentSession();
   }

   public Session getSession(int id) {
      Session session = (Session) getCurrentSession().get(Session.class, id);
      return session;
   }

   @SuppressWarnings("unchecked")
   public Session findOrCreateSession(String sessionID) {
      Query query = getCurrentSession().createQuery("from Session where sessionID = :sessionID");
      query.setString("sessionID", sessionID);
      List<Session> sessions = query.list();
      
      Session session = null;
      if(sessions == null || sessions.isEmpty()) {
         session = new Session();
         session.setSessionID(sessionID);
         getCurrentSession().save(session);
      } else if(sessions.size() == 1) {
         session = sessions.get(0);
      }

      return session;
   }

   @SuppressWarnings("unchecked")
   public List<Session> getSessions() {
      return getCurrentSession().createQuery("from Session").list();
   }

}
