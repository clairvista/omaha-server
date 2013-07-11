package com.clairvista.liveexpert.omaha.server.dao;

import java.util.List;

import com.clairvista.liveexpert.omaha.server.model.Update;

public interface UpdateDAO {

   public void addUpdate(Update update);
   public void updateUpdate(Update update);
   public Update getUpdate(int id);
   public void deleteUpdate(int id);
   public List<Update> getUpdates();
   
}
