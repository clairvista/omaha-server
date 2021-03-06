package com.clairvista.liveexpert.omaha.server.dao;

import java.util.List;

import com.clairvista.liveexpert.omaha.server.model.Application;
import com.clairvista.liveexpert.omaha.server.model.ApplicationVersion;

public interface ApplicationVersionDAO {

   public void addApplicationVersion(ApplicationVersion applicationVersion);
   public void updateApplicationVersion(ApplicationVersion applicationVersion);
   public ApplicationVersion getApplicationVersion(int id);
   public ApplicationVersion findByVersionForApplication(Application app, String version);
   public ApplicationVersion findCurrentForApplication(Application app);
   public void deleteApplicationVersion(int id);
   public List<ApplicationVersion> getApplicationVersions();
   
}
