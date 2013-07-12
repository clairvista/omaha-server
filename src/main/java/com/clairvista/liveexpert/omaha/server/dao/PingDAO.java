package com.clairvista.liveexpert.omaha.server.dao;

import java.util.List;

import com.clairvista.liveexpert.omaha.server.model.Ping;

public interface PingDAO {

   public void addPing(Ping event);
   public void updatePing(Ping event);
   public Ping getPing(int id);
   public void deletePing(int id);
   public List<Ping> getPings();
   
}
