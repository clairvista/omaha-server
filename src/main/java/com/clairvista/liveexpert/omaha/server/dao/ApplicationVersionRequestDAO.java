package com.clairvista.liveexpert.omaha.server.dao;

import java.util.List;

import com.clairvista.liveexpert.omaha.server.model.ApplicationVersionRequest;

public interface ApplicationVersionRequestDAO {

   public void addApplicationVersionRequest(ApplicationVersionRequest appRequest);
   public void updateApplicationVersionRequest(ApplicationVersionRequest appRequest);
   public ApplicationVersionRequest getApplicationVersionRequest(int id);
   public void deleteApplicationVersionRequest(int id);
   public List<ApplicationVersionRequest> getApplicationVersionRequests();
   
}
