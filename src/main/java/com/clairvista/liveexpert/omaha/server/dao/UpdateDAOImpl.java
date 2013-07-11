package com.clairvista.liveexpert.omaha.server.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.clairvista.liveexpert.omaha.server.model.Update;

@Repository
public class UpdateDAOImpl implements UpdateDAO {
   
   @Autowired
   private SessionFactory sessionFactory;
   
   private Session getCurrentSession() {
      return sessionFactory.getCurrentSession();
   }

   public void addUpdate(Update update) {
      getCurrentSession().save(update);
   }

   public void updateUpdate(Update update) {
      Update updateToUpdate = getUpdate(update.getId());

      if (updateToUpdate != null) {
         updateToUpdate.setUser(update.getUser());
         updateToUpdate.setSourceVersion(update.getSourceVersion());
         updateToUpdate.setTargetVersion(update.getTargetVersion());
         updateToUpdate.setLanguage(update.getLanguage());
         updateToUpdate.setBrand(update.getBrand());
         updateToUpdate.setAdditionalParams(update.getAdditionalParams());
         updateToUpdate.setExperiments(update.getExperiments());
         updateToUpdate.setCreatedTime(update.getCreatedTime());

         getCurrentSession().update(updateToUpdate);
      }
   }

   public Update getUpdate(int id) {
      Update update = (Update) getCurrentSession().get(Update.class, id);
      return update;
   }

   public void deleteUpdate(int id) {
      Update update = getUpdate(id);
      if (update != null)
         getCurrentSession().delete(update);
   }

   @SuppressWarnings("unchecked")
   public List<Update> getUpdates() {
      return getCurrentSession().createQuery("from Update").list();
   }

}
