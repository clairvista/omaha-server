package com.clairvista.liveexpert.omaha.server.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.clairvista.liveexpert.omaha.server.model.Application;
import com.clairvista.liveexpert.omaha.server.model.ApplicationVersion;

@Repository
public class ApplicationVersionDAOImpl implements ApplicationVersionDAO {
   
   @Autowired
   private SessionFactory sessionFactory;
   
   private Session getCurrentSession() {
      return sessionFactory.getCurrentSession();
   }

   public void addApplicationVersion(ApplicationVersion applicationVersion) {
      getCurrentSession().save(applicationVersion);
   }

   public void updateApplicationVersion(ApplicationVersion applicationVersion) {
      ApplicationVersion versionToUpdate = getApplicationVersion(applicationVersion.getId());

      if (versionToUpdate != null) {
         versionToUpdate.setVersionID(applicationVersion.getVersionID());
         versionToUpdate.setApplication(applicationVersion.getApplication());
         versionToUpdate.setAccessToken(applicationVersion.getAccessToken());
         versionToUpdate.setDownloadBaseURL(applicationVersion.getDownloadBaseURL());
         versionToUpdate.setInstallerName(applicationVersion.getInstallerName());
         versionToUpdate.setInstallerHash(applicationVersion.getInstallerHash());
         versionToUpdate.setInstallerSize(applicationVersion.getInstallerSize());
         versionToUpdate.setCreatedTime(applicationVersion.getCreatedTime());
         versionToUpdate.setCreatedBy(applicationVersion.getCreatedBy());

         getCurrentSession().update(versionToUpdate);
      }
   }

   public ApplicationVersion getApplicationVersion(int id) {
      ApplicationVersion version = (ApplicationVersion) getCurrentSession().get(ApplicationVersion.class, id);
      return version;
   }

   @SuppressWarnings("unchecked")
   public ApplicationVersion findByVersionForApplication(Application app, String version) {
      List<ApplicationVersion> versions = getCurrentSession().createQuery("FROM ApplicationVersion" +
            " WHERE application = :applicationID" +
            " AND versionID = :versionID")
            .setInteger("applicationID", app.getId())
            .setString("versionID", version)
            .list();
      if(versions.size() == 1) {
         return versions.get(0);
      }
      return null;
   }

   @SuppressWarnings("unchecked")
   public ApplicationVersion findCurrentForApplication(Application app) {
      List<ApplicationVersion> versions = getCurrentSession().createQuery("FROM ApplicationVersion" +
            " WHERE application = :applicationID" +
            " ORDER BY versionID DESC")
            .setInteger("applicationID", app.getId())
            .list();
      if(versions.size() > 0) {
         return versions.get(0);
      }
      return null;
   }

   public void deleteApplicationVersion(int id) {
      ApplicationVersion version = getApplicationVersion(id);
      if (version != null)
         getCurrentSession().delete(version);
   }

   @SuppressWarnings("unchecked")
   public List<ApplicationVersion> getApplicationVersions() {
      return getCurrentSession().createQuery("FROM ApplicationVersion").list();
   }

}
