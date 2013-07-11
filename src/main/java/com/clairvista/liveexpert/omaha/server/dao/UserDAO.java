package com.clairvista.liveexpert.omaha.server.dao;

import java.util.List;

import com.clairvista.liveexpert.omaha.server.model.User;

public interface UserDAO {

   public void addUser(User user);
   public void updateUser(User user);
   public User getUser(int id);
   public User findOrCreateUser(String userID);
   public void deleteUser(int id);
   public List<User> getUsers();
   
}
