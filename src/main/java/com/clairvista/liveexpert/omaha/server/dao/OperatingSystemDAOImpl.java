package com.clairvista.liveexpert.omaha.server.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.clairvista.liveexpert.omaha.server.model.OperatingSystem;

@Repository
public class OperatingSystemDAOImpl implements OperatingSystemDAO {
   
   @Autowired
   private SessionFactory sessionFactory;
   
   private Session getCurrentSession() {
      return sessionFactory.getCurrentSession();
   }

   public void addOperatingSystem(OperatingSystem operatingSystem) {
      getCurrentSession().save(operatingSystem);
   }

   public void updateOperatingSystem(OperatingSystem operatingSystem) {
      OperatingSystem osToUpdate = getOperatingSystem(operatingSystem.getId());

      if (osToUpdate != null) {
         osToUpdate.setPlatform(operatingSystem.getPlatform());
         osToUpdate.setVersion(operatingSystem.getVersion());
         osToUpdate.setServicePack(operatingSystem.getServicePack());
         osToUpdate.setArchitecture(operatingSystem.getArchitecture());
         osToUpdate.setCreatedTime(operatingSystem.getCreatedTime());

         getCurrentSession().update(osToUpdate);
      }
   }

   public OperatingSystem getOperatingSystem(int id) {
      OperatingSystem os = (OperatingSystem) getCurrentSession().get(OperatingSystem.class, id);
      return os;
   }

   @SuppressWarnings("unchecked")
   public OperatingSystem findOrCreateOperatingSystem(String platform, String version, String servicePack,
         String architecture) {
      Query query = getCurrentSession().createQuery("FROM OperatingSystem " +
      		"WHERE platform = :platform " +
      		"AND version = :version " +
      		"AND servicePack = :servicePack " +
      		"AND architecture = :architecture");
      query.setString("platform", platform);
      query.setString("version", version);
      query.setString("servicePack", servicePack);
      query.setString("architecture", architecture);
      List<OperatingSystem> operatingSystems = query.list();

      OperatingSystem operatingSystem = null;
      if(operatingSystems == null || operatingSystems.isEmpty()) {
         operatingSystem = new OperatingSystem();
         operatingSystem.setPlatform(platform);
         operatingSystem.setVersion(version);
         operatingSystem.setServicePack(servicePack);
         operatingSystem.setArchitecture(architecture);
         getCurrentSession().save(operatingSystem);
      } else if(operatingSystems.size() == 1) {
         operatingSystem = operatingSystems.get(0);
      }

      return operatingSystem;
   }

   public void deleteOperatingSystem(int id) {
      OperatingSystem os = getOperatingSystem(id);
      if (os != null)
         getCurrentSession().delete(os);
   }

   @SuppressWarnings("unchecked")
   public List<OperatingSystem> getOperatingSystems() {
      return getCurrentSession().createQuery("FROM OperatingSystem").list();
   }

}
