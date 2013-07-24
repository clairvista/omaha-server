package com.clairvista.liveexpert.omaha.server.dao;

import java.util.List;

import com.clairvista.liveexpert.omaha.server.model.Application;

public interface ApplicationDAO {

   public void addApplication(Application application);
   public void updateApplication(Application application);
   public Application getApplication(int id);
   public Application findApplication(String applicationID);
   public void deleteApplication(int id);
   public List<Application> getApplications();
   
}
