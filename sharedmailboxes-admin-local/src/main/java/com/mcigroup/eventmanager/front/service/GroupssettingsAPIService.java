package com.mcigroup.eventmanager.front.service;

import java.io.IOException;

import com.google.api.services.groupssettings.Groupssettings;
import com.google.api.services.groupssettings.model.Groups;
//import com.google.api.services.groupssettings.Groupssettings;
import com.mcigroup.eventmanager.front.helper.PropertiesManager;
import com.mcigroup.eventmanager.front.security.CredentialLoader;



public class GroupssettingsAPIService {
	
	private static Groupssettings groupssettings = getGroupssettings();
	private static String domain = PropertiesManager.getProperty("domain");
	private static Groupssettings getGroupssettings(){
		Groupssettings toReturn = groupssettings;
		
		if(groupssettings == null){ toReturn = CredentialLoader.getGroupssettings(); }
		
		return toReturn;
	}
	
	
	public static void changeGroupssettings(String groupId) {
		try {
			Groups groups = groupssettings.groups().get(groupId).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static void setAccessLevel() {
		
	}
	
	/*
	public static boolean isInDomainUser(String userEmail){
		
		boolean isDomainUser = false;
			System.err.println("in isInDomainUser");
		try {
			com.google.api.services.admin.directory.Directory.Users.List userList = directory.users().list();
			userList.setDomain(domain);
			Users users = userList.execute();
			System.err.println("Number of users = " + users.getUsers().size());
			for(User user : users.getUsers()) {
				System.err.println("user : " + user.getName());
			}
			User connected = directory.users().get(userEmail).execute();
			if (connected != null) {
				isDomainUser = true;
				System.err.println("user is in domain");
			}
		} catch (IOException e) {
			isDomainUser = false;
		}
		
		return isDomainUser;
	}
	
	public static String getAllUsers(){
		
			System.err.println("in getAllUsers");
			java.util.List<String> userEmails = new ArrayList<String>();
		try {
			com.google.api.services.admin.directory.Directory.Users.List userList = directory.users().list();
			userList.setDomain(domain);
			Users users = userList.execute();
			System.err.println("Number of users = " + users.getUsers().size());
			for(User user : users.getUsers()) {
				System.err.println("user : " + user.getName());
				userEmails.add(user.getPrimaryEmail());
			}
		} catch (IOException e) {
			System.err.println("error while retrieving all users");
		}
		
		return Tools.gson.toJson(userEmails);
	}
	
	public static String checkUser(String userMail){
		
		System.err.println("in checkUser");
		boolean userChecked = false;
	try {
		User userToCheck = directory.users().get(userMail).execute();
		if (userToCheck != null) {
			userChecked = true;
		}
	} catch (IOException e) {
		System.err.println("error while checking the user");
	}
	
	return Tools.gson.toJson(userChecked);
}
	
	public static void listGroup() {
		try {
//			Groups groups = directory.groups().list("demo.sogeti-reseller.com").execute();
			List groupsList = directory.groups().list();
			groupsList.setDomain(domain);
			Groups groups = groupsList.execute();
			System.err.println("Number of groups = " + groups.getGroups().size());
			for(Group group : groups.getGroups()) {
				System.err.println("Group email = " + group.getEmail());
			}
		} catch (IOException e) {
			System.err.println("Error while retrieving groups");
			e.printStackTrace();
		}
	}
	
	public static String isEventNameAvailable(String eventName){
		
		System.err.println("in checkEventName");
		List groupsList;
		try {
			groupsList = directory.groups().list();
			groupsList.setDomain(domain);
			Groups groups = groupsList.execute();
			//System.err.println("Number of groups = " + groups.getGroups().size());
			for(Group group : groups.getGroups()) {
				if(group.getName().equalsIgnoreCase(eventName)) {
					return Tools.gson.toJson(false);
				}
			}
			return Tools.gson.toJson(true);
		} catch (IOException e) {
			e.printStackTrace();
			return Tools.gson.toJson("error");
		}
		
	}
	
public static String isEventMailPrefixAvailable(String eventMailPrefix){
		
		System.err.println("in checkEventMailPrefix");
		List groupsList;
		try {
			groupsList = directory.groups().list();
			groupsList.setDomain(domain);
			Groups groups = groupsList.execute();
			//System.err.println("Number of groups = " + groups.getGroups().size());
			for(Group group : groups.getGroups()) {
				if(group.getEmail().toLowerCase().startsWith(eventMailPrefix.toLowerCase())) {
					return Tools.gson.toJson(false);
				}
			}
			return Tools.gson.toJson(true);
		} catch (IOException e) {
			e.printStackTrace();
			return Tools.gson.toJson("error");
		}
		
	}

	public static String createGroup(EventCreation eventToCreate) {
		Group groupToAdd = new Group();
		groupToAdd.setName(eventToCreate.getName());
		groupToAdd.setEmail(eventToCreate.getMail() + "@" + domain);
		String groupId="";
		try {
			Group addedGroup = directory.groups().insert(groupToAdd).execute();
			System.err.println("added group id = " + addedGroup.getId());
			groupId=addedGroup.getId();
		} catch (IOException e) {
			System.err.println("error while trying to add group");
			e.printStackTrace();
		}
		return groupId;
	}*/
}

