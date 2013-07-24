package com.clairvista.liveexpert.omaha.server.dao;

import java.util.List;

import com.clairvista.liveexpert.omaha.server.model.OperatingSystem;

public interface OperatingSystemDAO {

   public void addOperatingSystem(OperatingSystem os);
   public void updateOperatingSystem(OperatingSystem os);
   public OperatingSystem getOperatingSystem(int id);
   public OperatingSystem findOrCreateOperatingSystem(String platform, String version, 
         String servicePack, String architecture);
   public void deleteOperatingSystem(int id);
   public List<OperatingSystem> getOperatingSystems();
   
}
