package com.clairvista.liveexpert.omaha.server.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.clairvista.liveexpert.omaha.server.model.Request;

@Repository
public class RequestDAOImpl implements RequestDAO {
   
   @Autowired
   private SessionFactory sessionFactory;
   
   private Session getCurrentSession() {
      return sessionFactory.getCurrentSession();
   }

   public void addRequest(Request request) {
      getCurrentSession().save(request);
   }

   public void updateRequest(Request request) {
      Request requestToUpdate = getRequest(request.getId());

      if (requestToUpdate != null) {
         requestToUpdate.setRequestID(request.getRequestID());
         requestToUpdate.setProtocol(request.getProtocol());
         requestToUpdate.setClientVersion(request.getClientVersion());
         requestToUpdate.setUser(request.getUser());
         requestToUpdate.setSession(request.getSession());
         requestToUpdate.setInstallSource(request.getInstallSource());
         requestToUpdate.setOriginURL(request.getOriginURL());
         requestToUpdate.setCreatedTime(request.getCreatedTime());

         getCurrentSession().update(requestToUpdate);
      }
   }

   public Request getRequest(int id) {
      Request request = (Request) getCurrentSession().get(Request.class, id);
      return request;
   }

   public void deleteRequest(int id) {
      Request request = getRequest(id);
      if (request != null)
         getCurrentSession().delete(request);
   }

   @SuppressWarnings("unchecked")
   public List<Request> getRequests() {
      return getCurrentSession().createQuery("from Request").list();
   }

}
