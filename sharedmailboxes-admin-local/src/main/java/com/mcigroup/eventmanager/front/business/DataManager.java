package com.mcigroup.eventmanager.front.business;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import com.mcigroup.eventmanager.front.dao.SiteDao;
import com.mcigroup.eventmanager.front.dao.UserDao;
import com.mcigroup.eventmanager.front.helper.Tools;
import com.mcigroup.eventmanager.front.model.EventCreation;
import com.mcigroup.eventmanager.front.model.EventTypeEnum;
import com.mcigroup.eventmanager.front.model.Site;
import com.mcigroup.eventmanager.front.model.UserCreation;
import com.mcigroup.eventmanager.front.model.UserRoleEnum;
import com.mcigroup.eventmanager.front.service.DirectoryAPIService;
import com.mcigroup.eventmanager.front.service.DriveAPIService;
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
	
	public static<T> HashMap<String, Object> checkEventSite(String newSite){
	    HashMap<String, Object> results = new HashMap<String, Object>();
		ArrayList<String> messages = new ArrayList<String>();
		
		if(StringUtils.isBlank(newSite) || newSite.equals("undefined")) {
		    messages.add("The New Site name is mandatory");
		}
		
		// The new site must not already exists
		SiteDao siteDao = new SiteDao();
		Collection<Site> existingSites = siteDao.getAllSites();
		for (Site i_siteName : existingSites) {
		    if (newSite.equals(i_siteName.getName())) {
			messages.add("A site with the same name already exists");
		    }
		}
		
		if(messages.isEmpty()) {
			results.put("status", "success");
		} else {
			results.put("status", "failure");
			results.put("messages", messages);
		}
		return results;
	}
	
            public static <T> String createNewSite(String newSite) {
        	HashMap<String, Object> checks = checkEventSite(newSite);
        	if ("failure".equals(checks.get("status"))) {
        	    return Tools.gson.toJson(checks);
        	}
        	else {
        
        	    // Create the new site Drive Folder
        	    ArrayList<String> messages = new ArrayList<String>();
        	    String newSiteGoogleID = DriveAPIService.createNewSite(newSite, messages);
        
        	    if (newSiteGoogleID.isEmpty()) {
        		checks.put("status", "failure");
        		checks.put("messages", messages);
        		return Tools.gson.toJson(checks);
        	    }
        	    else {
        		// Insert the new Site in DB
        		SiteDao siteDao = new SiteDao();
        		int SQLresultCode = siteDao.createSite(newSite, newSiteGoogleID);
        		
        		if(SQLresultCode != 0) {
        		    checks.put("status", "success");
        		    checks.put("newSiteGoogleID", newSiteGoogleID);
        		} else {
        		    // Rollback on site drive folder creation
            		    DriveAPIService.removeFolderStructure(newSiteGoogleID);

            		    checks.put("status", "failure");
        		    messages.add("error during DB SQL site creation");
            		    checks.put("messages", messages);
        		}
        
        	    }
        	    return Tools.gson.toJson(checks);
        	}
            }
	
	
	
	public static<T> String checkEventMailPrefix(String eventMailPrefix){
		return DirectoryAPIService.isEventMailPrefixAvailable(eventMailPrefix);
	}
	
	public static<T> String addEvent(String event){
	    
//	    	System.err.println("GLA MAnual test upload CSV");
//	    	ReadCSV readCSV = new ReadCSV();
//	    	readCSV.run();
	    
		System.err.println(event);
		EventCreation eventToCreate = Tools.gson.fromJson(event, EventCreation.class);

		String siteFolderID = "";
		if (eventToCreate.getSite() != null) {
			Site siteOfEvent = eventToCreate.getSite();
			siteFolderID = siteOfEvent.getFolder_id();
		}		
		
		System.err.println("Event site folder ID = " + siteFolderID);
		System.err.println("Event name = " + eventToCreate.getName());
		System.err.println("Event mail = " + eventToCreate.getMail());
		System.err.println("Event type = " + eventToCreate.getType());
		for(UserCreation userToCreate : eventToCreate.getUsers()) {
			System.err.println("User mail = " + userToCreate.getMail());
			System.err.println("User role = " + userToCreate.getRole());
		}
		return EventCreationService.createEvent(eventToCreate);
	}
	
	public static<T> String processCsvFile(){
		System.err.println("In processCsvFile method");
		String result = "";
		
		addEventByCSV("", "CH-Events", "newEventCSV2", EventTypeEnum.CONGRESS.toString(), "guillaume.lapierre@demo.sogeti-reseller.com", "");
        	
		
		return result;
	}
	
	
	

    @SuppressWarnings("null")
    public static <T> HashMap<String, Object> addEventByCSV(String event, String siteName, String eventName,
	    String eventType, String eventHead, String teamMembers) {

	HashMap<String, Object> results = new HashMap<String, Object>();
	ArrayList<String> messages = new ArrayList<String>();

	System.err.println(event);
	EventCreation eventToCreate = new EventCreation();

	// Get the Site object
	SiteDao siteDao = new SiteDao();
	Collection<Site> existingSites = siteDao.getAllSites();
	for (Site i_site : existingSites) {
	    if (siteName.equals(i_site.getName())) {
		eventToCreate.setSite(i_site);
	    }
	}

	// If the site doesn't exist
	if (eventToCreate.getSite() == null) {
	    System.out.println("The Event Site: <" + siteName + "> doesn't correspont to a real Event Site");

	    // Creation of a new site
	    createNewSite(siteName);

	    existingSites = siteDao.getAllSites();
	    for (Site i_site : existingSites) {
		if (siteName.equals(i_site.getName())) {
		    eventToCreate.setSite(i_site);
		}
	    }

	    if (eventToCreate.getSite() == null) {
		messages.add("Error while retrieving the newly created site: <" + siteName + ">.");
	    }
	}

	if (messages.isEmpty()) {

	    // Check the event head user
	    UserDao userDao = new UserDao();
	    Collection<UserCreation> listOfUsers = new ArrayList<UserCreation>();

	    if (!eventHead.isEmpty()) {
		UserCreation userEventHead = userDao.getUserByEmail(eventHead);
		if (userEventHead != null) {
		    userEventHead.setRole(UserRoleEnum.EVENTHEAD.toString());
		    listOfUsers.add(userEventHead);
		}
		else {
		    messages.add("Error while retrieving the event Head user <" + eventHead
			    + "> from system.");
		}
	    }

	    // Check the event team member's user(s)
	    if (!teamMembers.isEmpty()) {
		UserCreation userTeamMember = userDao.getUserByEmail(teamMembers);
		//TODO: implement the multiple user team member assignment
		if (userTeamMember != null) {
		    userTeamMember.setRole(UserRoleEnum.TEAMMEMBER.toString());
		    listOfUsers.add(userTeamMember);
		}
		else {
		    messages.add("Error while retrieving the event Team Member user <" + userTeamMember
			    + "> from system.");
		}
	    }



	    if (listOfUsers.isEmpty()) {
		messages.add("There must be at least one user in the Event.");
	    }


	    if (messages.isEmpty()) {
		eventToCreate.setUsers(listOfUsers);

		// Get the other value
		eventToCreate.setName(eventName);
		eventToCreate.setType(eventType.toLowerCase());
		// TODO: manage the mail domain dynamically
//		eventToCreate.setMail(eventName.replace(" ", ".") + "@demo.sogeti-reseller.com");
		eventToCreate.setMail(eventName.replace(" ", "."));


		//---------------------------
		// Display of the values
		//---------------------------
		String siteFolderID = "";
		if (eventToCreate.getSite() != null) {
		    Site siteOfEvent = eventToCreate.getSite();
		    siteFolderID = siteOfEvent.getFolder_id();
		}

		System.err.println("Event site folder ID = " + siteFolderID);
		System.err.println("Event name = " + eventToCreate.getName());
		System.err.println("Event mail = " + eventToCreate.getMail());
		System.err.println("Event type = " + eventToCreate.getType());
		for (UserCreation userToCreate : eventToCreate.getUsers()) {
		    System.err.println("User mail = " + userToCreate.getMail());
		    System.err.println("User role = " + userToCreate.getRole());
		}
		
		String result = EventCreationService.createEvent(eventToCreate);
	    }
	}

	if (messages.isEmpty()) {
	    results.put("status", "success");
	}
	else {
	    results.put("status", "failure");
	    results.put("messages", messages);
	}

	return results;
    }

	
	public static<T> String getUsersForEventGroup(String groupId) {
		return EventService.getEventCreationForEventGroup(groupId);
	}
	
	public static<T> String updEvent(String event){
		System.err.println(event);
		EventCreation eventToUpdate = Tools.gson.fromJson(event, EventCreation.class);
//		System.err.println("Event site ID = " + eventToUpdate.getSite_id());
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