package com.clairvista.liveexpert.omaha.server.dao;

import java.util.List;

import com.clairvista.liveexpert.omaha.server.model.OperatingSystemHistory;

public interface OperatingSystemHistoryDAO {

   public void addOperatingSystemHistory(OperatingSystemHistory osHistory);
   public void updateOperatingSystemHistory(OperatingSystemHistory osHistory);
   public OperatingSystemHistory getOperatingSystemHistory(int id);
   public void deleteOperatingSystemHistory(int id);
   public List<OperatingSystemHistory> getOperatingSystemHistories();
   
}
