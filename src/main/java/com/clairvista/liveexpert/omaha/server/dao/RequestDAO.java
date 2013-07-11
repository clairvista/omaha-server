package com.clairvista.liveexpert.omaha.server.dao;

import java.util.List;

import com.clairvista.liveexpert.omaha.server.model.Request;

public interface RequestDAO {

   public void addRequest(Request request);
   public void updateRequest(Request request);
   public Request getRequest(int id);
   public void deleteRequest(int id);
   public List<Request> getRequests();
   
}
