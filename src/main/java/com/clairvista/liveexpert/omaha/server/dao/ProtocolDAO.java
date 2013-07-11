package com.clairvista.liveexpert.omaha.server.dao;

import java.util.List;

import com.clairvista.liveexpert.omaha.server.model.Protocol;

public interface ProtocolDAO {

   // This is a static lookup table, so it should never be modified programmatically.
   public Protocol getProtocol(int id);
   public Protocol getProtocol(String protocolID);
   public List<Protocol> getProtocols();
   
}
