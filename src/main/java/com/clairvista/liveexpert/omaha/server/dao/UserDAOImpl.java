package com.clairvista.liveexpert.omaha.server.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.clairvista.liveexpert.omaha.server.model.User;

@Repository
public class UserDAOImpl implements UserDAO {
   
   @Autowired
   private SessionFactory sessionFactory;
   
   private Session getCurrentSession() {
      return sessionFactory.getCurrentSession();
   }

   public void addUser(User user) {
      getCurrentSession().save(user);
   }

   public void updateUser(User user) {
      User userToUpdate = getUser(user.getId());

      if (userToUpdate != null) {
         userToUpdate.setUserID(user.getUserID());
         userToUpdate.setOperatingSystem(user.getOperatingSystem());
         userToUpdate.setCreatedTime(user.getCreatedTime());

         getCurrentSession().update(userToUpdate);
      }
   }

   public User getUser(int id) {
      User user = (User) getCurrentSession().get(User.class, id);
      return user;
   }

   @SuppressWarnings("unchecked")
   public User findOrCreateUser(String userID) {
      Query query = getCurrentSession().createQuery("FROM User WHERE userID = :userID");
      query.setString("userID", userID);
      List<User> users = query.list();

      User user = null;
      if(users == null || users.isEmpty()) {
         user = new User();
         user.setUserID(userID);
         getCurrentSession().save(user);
      } else if(users.size() == 1) {
         user = users.get(0);
      }

      return user;
   }

   public void deleteUser(int id) {
      User user = getUser(id);
      if (user != null)
         getCurrentSession().delete(user);
   }

   @SuppressWarnings("unchecked")
   public List<User> getUsers() {
      return getCurrentSession().createQuery("FROM User").list();
   }

}
