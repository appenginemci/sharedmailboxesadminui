package com.mcigroup.eventmanager.front.service;

import java.util.Collection;

import com.mcigroup.eventmanager.front.dao.EventDao;
import com.mcigroup.eventmanager.front.dao.UserDao;
import com.mcigroup.eventmanager.front.helper.Tools;
import com.mcigroup.eventmanager.front.model.EventCreation;
import com.mcigroup.eventmanager.front.model.UserCreation;

public class EventService {

	public static String getAllEvents() {
		EventDao eventDao = new EventDao();
		Collection<EventCreation> events = eventDao.getAllEvents();
		return Tools.gson.toJson(events);

	}
	
	public static String getEventCreationForEventGroup(String groupId) {
		EventDao eventDao = new EventDao();
		UserDao userDao = new UserDao();
		EventCreation event = eventDao.getEventCreationForEventGroup(groupId);
		Collection<UserCreation> users = userDao.getUserCreationsForEventId(event.getDbId()); 
		event.setUsers(users);
		return Tools.gson.toJson(event);
	}
	
	
}
