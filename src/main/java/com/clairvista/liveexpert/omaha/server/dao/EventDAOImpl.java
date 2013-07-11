package com.clairvista.liveexpert.omaha.server.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.clairvista.liveexpert.omaha.server.model.Event;

@Repository
public class EventDAOImpl implements EventDAO {
   
   @Autowired
   private SessionFactory sessionFactory;
   
   private Session getCurrentSession() {
      return sessionFactory.getCurrentSession();
   }

   public void addEvent(Event event) {
      getCurrentSession().save(event);
   }

   public void updateEvent(Event event) {
      Event eventToUpdate = getEvent(event.getId());

      if (eventToUpdate != null) {
         eventToUpdate.setApplicationVersion(event.getApplicationVersion());
         eventToUpdate.setRequest(event.getRequest());
         eventToUpdate.setEventType(event.getEventType());
         eventToUpdate.setEventResult(event.getEventResult());
         eventToUpdate.setErrorCode(event.getErrorCode());
         eventToUpdate.setExtraCode(event.getExtraCode());
         eventToUpdate.setUpdateCheckTime(event.getUpdateCheckTime());
         eventToUpdate.setDownloadTimeMs(event.getDownloadTimeMs());
         eventToUpdate.setInstallTimeMs(event.getInstallTimeMs());
         eventToUpdate.setBytesDownloaded(event.getBytesDownloaded());
         eventToUpdate.setTotalSize(event.getTotalSize());
         eventToUpdate.setSourceUrlIndex(event.getSourceUrlIndex());
         eventToUpdate.setStateCancelled(event.getStateCancelled());
         eventToUpdate.setTimeSinceUpdateAvailable(event.getTimeSinceUpdateAvailable());
         eventToUpdate.setTimeSinceDownloadStart(event.getTimeSinceDownloadStart());
         eventToUpdate.setCreatedTime(event.getCreatedTime());
         getCurrentSession().update(eventToUpdate);
      }
   }

   public Event getEvent(int id) {
      Event event = (Event) getCurrentSession().get(Event.class, id);
      return event;
   }

   public void deleteEvent(int id) {
      Event event = getEvent(id);
      if (event != null)
         getCurrentSession().delete(event);
   }

   @SuppressWarnings("unchecked")
   public List<Event> getEvents() {
      return getCurrentSession().createQuery("from Event").list();
   }

}
