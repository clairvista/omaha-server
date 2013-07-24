package com.clairvista.liveexpert.omaha.server.dao;

import java.util.List;

import com.clairvista.liveexpert.omaha.server.model.Event;

public interface EventDAO {

   public void addEvent(Event event);
   public void updateEvent(Event event);
   public Event getEvent(int id);
   public void deleteEvent(int id);
   public List<Event> getEvents();
   
}
