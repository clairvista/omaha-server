package com.clairvista.liveexpert.omaha.server.dao;

import java.util.List;

import com.clairvista.liveexpert.omaha.server.model.Session;

public interface SessionDAO {

   // This is a dynamic lookup table so it should only be added to programmatically.
   public Session getSession(int id);
   public Session findOrCreateSession(String sessionID);
   public List<Session> getSessions();
   
}
