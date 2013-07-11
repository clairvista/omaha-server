package com.clairvista.liveexpert.omaha.server.dao;

import java.util.List;

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

   public OperatingSystem findOrCreateOperatingSystem(String platform, String version, String servicePack,
         String architecture) {
      // TODO Auto-generated method stub
      return null;
   }

   public void deleteOperatingSystem(int id) {
      OperatingSystem os = getOperatingSystem(id);
      if (os != null)
         getCurrentSession().delete(os);
   }

   @SuppressWarnings("unchecked")
   public List<OperatingSystem> getOperatingSystems() {
      return getCurrentSession().createQuery("from OperatingSystem").list();
   }

}
