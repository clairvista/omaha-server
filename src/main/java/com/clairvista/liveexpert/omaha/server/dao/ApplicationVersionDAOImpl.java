package com.clairvista.liveexpert.omaha.server.dao;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.clairvista.liveexpert.omaha.server.model.Application;
import com.clairvista.liveexpert.omaha.server.model.ApplicationVersion;

@Repository
public class ApplicationVersionDAOImpl implements ApplicationVersionDAO {

   private static Logger LOGGER = Logger.getLogger(ApplicationVersionDAOImpl.class);
   
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
            " WHERE application = :applicationID")
            .setInteger("applicationID", app.getId())
            .list();

      Map<ApplicationVersion, List<Integer>> versionLookup = new HashMap<ApplicationVersion, List<Integer>>();
      for(ApplicationVersion version : versions) {
         versionLookup.put(version, version.getParsedVersionID());
      }
      Set<ApplicationVersion> initialVersions = versionLookup.keySet();
      return findLargestVersion(versionLookup, initialVersions, 0);
   }

   /**
    * Recursively finds the initialVersions entry with the numerically largest version number.
    *
    * @param versionLookup  A lookup map providing versions for each entry in the initialVersions set.
    * @param initialVersions  The set of versions to be considered as part of this iteration.
    * @param currentIndex  The version tuple index being considered during this iteration.
    * @return The entry from initialVersions with the numerically largest version number.
    */
   private ApplicationVersion findLargestVersion(Map<ApplicationVersion, List<Integer>> versionLookup, 
         Set<ApplicationVersion> initialVersions, Integer currentIndex) {
      if(initialVersions.isEmpty()) { return null; }
      if(initialVersions.size() == 1) { return (ApplicationVersion) initialVersions.toArray()[0]; }
      
      Set<ApplicationVersion> finalVersions = new HashSet<ApplicationVersion>();
      
      int maxValue = 0;
      for(ApplicationVersion version : initialVersions) {
         List<Integer> parsedVersion = versionLookup.get(version);
         if(parsedVersion.size() > currentIndex) {
            Integer value = parsedVersion.get(currentIndex);
            if(value == null) { 
               continue; 
            } else if(value == maxValue) {
               finalVersions.add(version);
            } else if(value > maxValue) {
               maxValue = value;
               finalVersions.clear();
               finalVersions.add(version);
            }
         }
      }
      if(finalVersions.isEmpty()) {
         ApplicationVersion version = (ApplicationVersion) initialVersions.toArray()[0];
         if(initialVersions.size() > 1) {
            LOGGER.warn("Found " + initialVersions.size() + " equal versions for application " + 
                  version.getApplication().getId() + ". Returning version " + version.getId());
         }
         return version;
      } else {
         return findLargestVersion(versionLookup, finalVersions, (currentIndex + 1));
      }
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
