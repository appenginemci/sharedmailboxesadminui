package com.mcigroup.eventmanager.front.business;

import com.google.appengine.api.users.UserService;
import com.mcigroup.eventmanager.front.helper.Tools;
import com.mcigroup.eventmanager.front.model.EventCreation;
import com.mcigroup.eventmanager.front.model.UserCreation;
import com.mcigroup.eventmanager.front.service.DirectoryAPIService;
import com.mcigroup.eventmanager.front.service.EventCreationService;
import com.mcigroup.eventmanager.front.service.EventService;
import com.mcigroup.eventmanager.front.service.SiteService;


public class DataManager {
	 
//	public static<T> String getDriveList(String userEmail){
//			return DriveAPIService.getFileList(userEmail);
//	}
//	
//	public static<T> String getDriveListForManager(String userEmail){
//		return DriveAPIService.getFileListForManager(userEmail);
//	}
//	
//	public static<T> String getConsumerType(String userEmail){
//		return DriveAPIService.getConsumerType(userEmail);
//	}
	
	public static<T> String getAllEvents(){
		return EventService.getAllEvents();
	}

	public static<T> String getAllUsers(){
		return DirectoryAPIService.getAllUsers();
	}
	
	public static<T> String checkUser(String userMail){
		return DirectoryAPIService.checkUser(userMail);
	}
	
	public static<T> String checkEventName(String eventName){
		return DirectoryAPIService.isEventNameAvailable(eventName);
	}
	
	public static<T> String getAllSites(){
		return SiteService.getAllSites();
	}
	
	public static<T> String checkEventSite(String eventSite){
//		return DirectoryAPIService.isEventSiteAvailable(eventSite);
	    // TODO: implement this method
		return null;
	}
	
	public static<T> String checkEventMailPrefix(String eventMailPrefix){
		return DirectoryAPIService.isEventMailPrefixAvailable(eventMailPrefix);
	}
	
	public static<T> String addEvent(String event){
		System.err.println(event);
		EventCreation eventToCreate = Tools.gson.fromJson(event, EventCreation.class);
		System.err.println("Event site ID = " + eventToCreate.getSite_id());
		System.err.println("Event site folder ID = " + eventToCreate.getSiteFolder_id());
		System.err.println("Event name = " + eventToCreate.getName());
		System.err.println("Event mail = " + eventToCreate.getMail());
		System.err.println("Event type = " + eventToCreate.getType());
		for(UserCreation userToCreate : eventToCreate.getUsers()) {
			System.err.println("User mail = " + userToCreate.getMail());
			System.err.println("User role = " + userToCreate.getRole());
		}
		return EventCreationService.createEvent(eventToCreate);
	}
	
	public static<T> String getUsersForEventGroup(String groupId) {
		return EventService.getEventCreationForEventGroup(groupId);
	}
	
	public static<T> String updEvent(String event){
		System.err.println(event);
		EventCreation eventToUpdate = Tools.gson.fromJson(event, EventCreation.class);
		System.err.println("Event site ID = " + eventToUpdate.getSite_id());
		System.err.println("Event name = " + eventToUpdate.getName());
		System.err.println("Event mail = " + eventToUpdate.getMail());
		System.err.println("Event type = " + eventToUpdate.getType());
		for(UserCreation userToUpdate : eventToUpdate.getUsers()) {
			System.err.println("User mail = " + userToUpdate.getMail());
			System.err.println("User role = " + userToUpdate.getRole());
		}
		return EventCreationService.updateEvent(eventToUpdate);
	}
}