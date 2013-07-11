package com.clairvista.liveexpert.omaha.server.service;

import java.util.List;

import com.clairvista.liveexpert.omaha.server.model.Application;

public interface ApplicationService {

   public void addApplication(Application application);
   public void updateApplication(Application application);
   public Application getApplication(int id);
   public void deleteApplication(int id);
   public List<Application> getApplications();

}
