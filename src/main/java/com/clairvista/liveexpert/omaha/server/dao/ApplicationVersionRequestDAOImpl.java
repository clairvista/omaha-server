package com.clairvista.liveexpert.omaha.server.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.clairvista.liveexpert.omaha.server.model.ApplicationVersionRequest;

@Repository
public class ApplicationVersionRequestDAOImpl implements ApplicationVersionRequestDAO {
   
   @Autowired
   private SessionFactory sessionFactory;
   
   private Session getCurrentSession() {
      return sessionFactory.getCurrentSession();
   }

   public void addApplicationVersionRequest(ApplicationVersionRequest appRequest) {
      getCurrentSession().save(appRequest);
   }

   public void updateApplicationVersionRequest(ApplicationVersionRequest appRequest) {
      ApplicationVersionRequest appRequestToUpdate = getApplicationVersionRequest(appRequest.getId());

      if (appRequestToUpdate != null) {
         appRequestToUpdate.setApplicationVersion(appRequest.getApplicationVersion());
         appRequestToUpdate.setRequest(appRequest.getRequest());
         appRequestToUpdate.setLanguage(appRequest.getLanguage());
         appRequestToUpdate.setBrand(appRequest.getBrand());
         appRequestToUpdate.setClient(appRequest.getClient());
         appRequestToUpdate.setAdditionalParams(appRequest.getAdditionalParams());
         appRequestToUpdate.setExperiments(appRequest.getExperiments());
         appRequestToUpdate.setInstallID(appRequest.getInstallID());
         appRequestToUpdate.setInstallAge(appRequest.getInstallAge());
         appRequestToUpdate.setCreatedTime(appRequest.getCreatedTime());

         getCurrentSession().update(appRequestToUpdate);
      }
   }

   public ApplicationVersionRequest getApplicationVersionRequest(int id) {
      ApplicationVersionRequest update = (ApplicationVersionRequest) getCurrentSession().get(ApplicationVersionRequest.class, id);
      return update;
   }

   public void deleteApplicationVersionRequest(int id) {
      ApplicationVersionRequest update = getApplicationVersionRequest(id);
      if (update != null)
         getCurrentSession().delete(update);
   }

   @SuppressWarnings("unchecked")
   public List<ApplicationVersionRequest> getApplicationVersionRequests() {
      return getCurrentSession().createQuery("FROM ApplicationVersionRequest").list();
   }

}
