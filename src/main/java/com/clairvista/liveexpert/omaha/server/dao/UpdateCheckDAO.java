package com.clairvista.liveexpert.omaha.server.dao;

import java.util.List;

import com.clairvista.liveexpert.omaha.server.model.UpdateCheck;

public interface UpdateCheckDAO {

   public void addUpdateCheck(UpdateCheck updateCheck);
   public void updateUpdateCheck(UpdateCheck updateCheck);
   public UpdateCheck getUpdateCheck(int id);
   public void deleteUpdateCheck(int id);
   public List<UpdateCheck> getUpdateChecks();
   
}
