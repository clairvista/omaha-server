package com.clairvista.liveexpert.omaha.server.dao;

import java.util.List;

import com.clairvista.liveexpert.omaha.server.model.ClientVersion;

public interface ClientVersionDAO {

   // This is a dynamic lookup table so it should only be added to programmatically.
   public ClientVersion getClientVersion(int id);
   public ClientVersion findOrCreateClientVersion(String versionID);
   public List<ClientVersion> getClientVersions();
   
}
