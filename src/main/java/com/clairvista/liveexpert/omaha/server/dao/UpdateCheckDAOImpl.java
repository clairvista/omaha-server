package com.clairvista.liveexpert.omaha.server.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.clairvista.liveexpert.omaha.server.model.UpdateCheck;

@Repository
public class UpdateCheckDAOImpl implements UpdateCheckDAO {
   
   @Autowired
   private SessionFactory sessionFactory;
   
   private Session getCurrentSession() {
      return sessionFactory.getCurrentSession();
   }

   public void addUpdateCheck(UpdateCheck updateCheck) {
      getCurrentSession().save(updateCheck);
   }

   public void updateUpdateCheck(UpdateCheck updateCheck) {
      UpdateCheck updateCheckToUpdate = getUpdateCheck(updateCheck.getId());

      if (updateCheckToUpdate != null) {
         updateCheckToUpdate.setApplicationVersionRequest(updateCheck.getApplicationVersionRequest());
         updateCheckToUpdate.setAccessToken(updateCheck.getAccessToken());
         updateCheckToUpdate.setUpdateDisabled(updateCheck.getUpdateDisabled());
         updateCheckToUpdate.setTargetVersionPrefix(updateCheck.getTargetVersionPrefix());
         updateCheckToUpdate.setCreatedTime(updateCheck.getCreatedTime());

         getCurrentSession().update(updateCheckToUpdate);
      }
   }

   public UpdateCheck getUpdateCheck(int id) {
      UpdateCheck updateCheck = (UpdateCheck) getCurrentSession().get(UpdateCheck.class, id);
      return updateCheck;
   }

   public void deleteUpdateCheck(int id) {
      UpdateCheck updateCheck = getUpdateCheck(id);
      if (updateCheck != null)
         getCurrentSession().delete(updateCheck);
   }

   @SuppressWarnings("unchecked")
   public List<UpdateCheck> getUpdateChecks() {
      return getCurrentSession().createQuery("from UpdateCheck").list();
   }

}
