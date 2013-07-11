package com.clairvista.liveexpert.omaha.server.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clairvista.liveexpert.omaha.server.dao.ApplicationDAO;
import com.clairvista.liveexpert.omaha.server.model.Application;

@Service
@Transactional
public class ApplicationServiceImpl implements ApplicationService {

   @Autowired
   private ApplicationDAO appDAO;
   
   public void addApplication(Application application) {
      appDAO.addApplication(application);
   }

   public void updateApplication(Application application) {
      appDAO.updateApplication(application);
   }

   public Application getApplication(int id) {
      return appDAO.getApplication(id);
   }

   public void deleteApplication(int id) {
      appDAO.deleteApplication(id);
   }

   public List<Application> getApplications() {
      return appDAO.getApplications();
   }

}
