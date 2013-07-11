package com.clairvista.liveexpert.omaha.server.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.clairvista.liveexpert.omaha.server.model.Application;

@Repository
public class ApplicationDAOImpl implements ApplicationDAO {
   
   @Autowired
   private SessionFactory sessionFactory;
   
   private Session getCurrentSession() {
      return sessionFactory.getCurrentSession();
   }

   public void addApplication(Application application) {
      getCurrentSession().save(application);
   }

   public void updateApplication(Application application) {
      Application appToUpdate = getApplication(application.getId());

      if (appToUpdate != null) {
         appToUpdate.setName(application.getName());
         appToUpdate.setDescription(application.getDescription());
         appToUpdate.setAppID(application.getAppID());
         appToUpdate.setCreatedTime(application.getCreatedTime());
         appToUpdate.setCreatedBy(application.getCreatedBy());

         getCurrentSession().update(appToUpdate);
      }
   }

   public Application getApplication(int id) {
      Application app = (Application) getCurrentSession().get(Application.class, id);
      return app;
   }

   public void deleteApplication(int id) {
      Application app = getApplication(id);
      if (app != null)
         getCurrentSession().delete(app);
   }

   @SuppressWarnings("unchecked")
   public List<Application> getApplications() {
      return getCurrentSession().createQuery("from Application").list();
   }

}
