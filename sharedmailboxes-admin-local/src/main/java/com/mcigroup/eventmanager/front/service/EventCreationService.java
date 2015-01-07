package com.mcigroup.eventmanager.front.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.api.services.admin.directory.model.User;
import com.mcigroup.eventmanager.front.dao.EventDao;
import com.mcigroup.eventmanager.front.dao.UserDao;
import com.mcigroup.eventmanager.front.helper.Tools;
import com.mcigroup.eventmanager.front.model.EventCreation;
import com.mcigroup.eventmanager.front.model.UserCreation;

public class EventCreationService {

	private static void checkUsers(EventCreation eventToCheck, List<String> messages) {
		if(eventToCheck.getUsers() == null || eventToCheck.getUsers().isEmpty()) {
			messages.add("There must be at least one user for the new event");
		} else {
			Collection<UserCreation> newUsers = new ArrayList<UserCreation>(eventToCheck.getUsers().size());
			for(UserCreation userToCreate : eventToCheck.getUsers()) {
				User user = DirectoryAPIService.getUser(userToCreate.getMail());
				//if(user != null) {
				//if(!"true".equals(DirectoryAPIService.checkUser(userToCreate.getMail()))) {
				if(user == null) {
					messages.add("The user " + userToCreate.getMail() + " doesn't exist");
				} else {
					userToCreate.setName(user.getName().getFullName());
					newUsers.add(userToCreate);
				}
			}
			eventToCheck.setUsers(newUsers);
		}
	}
	
	private static HashMap<String, Object> checkAllFields(EventCreation eventToCreate) {
		HashMap<String, Object> results = new HashMap<String, Object>();
		ArrayList<String> messages = new ArrayList<String>();
		if(eventToCreate.getSite() == null) {
			messages.add("The Event Site is mandatory");
		}
		if(StringUtils.isBlank(eventToCreate.getName())) {
			messages.add("Event Name is mandatory");
		} else {
			if(!"true".equals(DirectoryAPIService.isEventNameAvailable(eventToCreate.getName()))) {
				messages.add("Event Name already used");
			}
		}
		if(StringUtils.isBlank(eventToCreate.getMail())) {
			messages.add("Event Mail Prefix is mandatory");
		} else {
			if(!"true".equals(DirectoryAPIService.isEventMailPrefixAvailable(eventToCreate.getMail()))) {
				messages.add("Event Mail prefix already used");
			}
		}
		if(StringUtils.isBlank(eventToCreate.getType())) {
			messages.add("EventType is mandatory");
		}
		checkUsers(eventToCreate, messages);
		if(messages.isEmpty()) {
			results.put("status", "success");
		} else {
			results.put("status", "failure");
			results.put("messages", messages);
		}
		return results;
	}
	
	public static String createEvent(EventCreation eventToCreate) {
		HashMap<String, Object> checks = checkAllFields(eventToCreate);
		if( "failure".equals(checks.get("status"))) {
			return Tools.gson.toJson(checks);
		}
//		else {
			// continue process
			//String groupId=DirectoryAPIService.createGroup(eventToCreate);
			if (!DirectoryAPIService.createGroup(eventToCreate)) {
				ArrayList<String> messages = new ArrayList<String>();
				messages.add("Error during Group creation");
				checks.put("messages", messages);
				checks.put("status", "failure");
				return Tools.gson.toJson(checks);
			}
//			GroupssettingsAPIService.changeGroupssettings(groupId);
			if(!DirectoryAPIService.addUsersToGroup(eventToCreate.getGroupId(), eventToCreate.getUsers())) {
				ArrayList<String> messages = new ArrayList<String>();
				messages.add("Error while adding users to Group");
				checks.put("messages", messages);
				checks.put("status", "failure");
				DirectoryAPIService.removeGroup(eventToCreate.getGroupId());
				return Tools.gson.toJson(checks);
			}
			if(!DirectoryAPIService.addAdminUserToGroup(eventToCreate.getGroupId())) {
				ArrayList<String> messages = new ArrayList<String>();
				messages.add("Error while adding admin user to Group");
				checks.put("messages", messages);
				checks.put("status", "failure");
				DirectoryAPIService.removeGroup(eventToCreate.getGroupId());
				return Tools.gson.toJson(checks);
			}
			
		checks = DriveAPIService.createFolderStructure(eventToCreate);
		
		if ("failure".equals(checks.get("status"))) {
			if(!StringUtils.isBlank(checks.get("folderId").toString())) {
				DriveAPIService.removeFolderStructure(checks.get("folderId").toString());
			}
			DirectoryAPIService.removeGroup(eventToCreate.getGroupId());
			return Tools.gson.toJson(checks);
		}
		String eventFolderId = checks.get("folderId").toString();
			checks = createDatabaseRecords(eventToCreate);
			if ("failure".equals(checks.get("status"))) {
				DriveAPIService.removeFolderStructure(eventFolderId.toString());
				DirectoryAPIService.removeGroup(eventToCreate.getGroupId());
				return Tools.gson.toJson(checks);
			}
			return Tools.gson.toJson(checks);
//		}
		
	}
	
	
	private static HashMap<String, Object> createDatabaseRecords(EventCreation eventToCreate) {
		HashMap<String, Object> results = new HashMap<String, Object>();
		ArrayList<String> messages = new ArrayList<String>();
		EventDao eventDao = new EventDao();
		
		int eventId = eventDao.createEvent(eventToCreate);
		if(eventId != 0) {
			createUserIfNeeded(eventToCreate, messages);
			if (!messages.isEmpty()) {
				results.put("status", "failure");
				results.put("messages", messages);
			} else {
				results.put("status", "success");
			}
		} else {
			messages.add("Error when creating event in database");
			results.put("messages", messages);
			results.put("status", "failure");
		}
		return results;
	}
	
	private static void createUserIfNeeded(EventCreation eventToCreate, List<String> messages) {
		UserDao userDao = new UserDao();
		Collection<UserCreation> usersToCreate = eventToCreate.getUsers();
		for(UserCreation userToCreate : usersToCreate) {
			System.err.println(userToCreate.getName());
			if (userDao.getUserByEmail(userToCreate.getMail()) == null) {
				//create user in db
				if (userDao.addUser(userToCreate)) {
					System.err.println("Create user : " + userToCreate.getMail() + " in DB");
				} else {
					System.err.println("Failure when trying to add user : " + userToCreate.getMail());
					messages.add("Failure when trying to add user : " + userToCreate.getMail());
				}
			}
			if(userDao.linkMemberToEvent(eventToCreate, userToCreate)){
				System.err.println("User : " + userToCreate.getMail() + " linked to the event : " + eventToCreate.getName());
			} else {
				System.err.println("Failure when trying to link user : " + userToCreate.getMail() + " to the event : " + eventToCreate.getName());
				messages.add("Failure when trying to link user : " + userToCreate.getMail() + " to the event : " + eventToCreate.getName());
			}
		}
	}
	
	private static void updateUsersInDB(EventCreation eventToCreate, List<String> messages) {
		UserDao userDao = new UserDao();
		Collection<UserCreation> usersToUpdate = eventToCreate.getUsers();
		for(UserCreation userToUpdate : usersToUpdate) {
			System.err.println(userToUpdate.getName());
			if(userDao.updateLinkMemberToEvent(userToUpdate)){
				System.err.println("User : " + userToUpdate.getMail() + " updated for the event : " + eventToCreate.getName());
			} else {
				System.err.println("Failure when trying to update the link for user : " + userToUpdate.getMail() + " for the event : " + eventToCreate.getName());
				messages.add("Failure when trying to update the link for user : " + userToUpdate.getMail() + " for the event : " + eventToCreate.getName());
			}
		}
	}
	
	private static void removeUsersInDB(EventCreation eventToCreate, List<String> messages) {
		UserDao userDao = new UserDao();
		Collection<UserCreation> usersToRemove = eventToCreate.getUsers();
		for(UserCreation userToRemove : usersToRemove) {
			System.err.println(userToRemove.getName());
			if(userDao.removeLinkMemberFromEvent(userToRemove)){
				System.err.println("User : " + userToRemove.getMail() + " removed for the event : " + eventToCreate.getName());
			} else {
				System.err.println("Failure when trying to remove the link for user : " + userToRemove.getMail() + " for the event : " + eventToCreate.getName());
				messages.add("Failure when trying to remove the link for user : " + userToRemove.getMail() + " for the event : " + eventToCreate.getName());
			}
		}
	}
	
	
	private static HashMap<String, Object> fillUsersListForRemoveAddUpdate(Collection<UserCreation> newUsersList, Collection<UserCreation> currentUsersList, Collection<UserCreation> usersToUpdate) {
		HashMap<String, Object> results = new HashMap<String, Object>();
		ArrayList<String> messages = new ArrayList<>();
		Iterator<UserCreation> currentUsersIterator = currentUsersList.iterator();
		
		while (currentUsersIterator.hasNext()) {
			boolean found = false;
			UserCreation currentUser = (UserCreation) currentUsersIterator.next();
			Iterator<UserCreation> newUsersIterator = newUsersList.iterator();
			while(newUsersIterator.hasNext() && !found) {
				System.err.println("current user : mail = " + currentUser.getMail() + " -- role = " + currentUser.getRole());
				UserCreation newUser = (UserCreation) newUsersIterator.next();
				System.err.println("new user : mail = " + newUser.getMail() + " -- role = " + newUser.getRole());
				if(newUser.equals(currentUser)) {
					System.err.println("sameUser");
					found = true;
				} else if (newUser.getMail().equalsIgnoreCase(currentUser.getMail()) && !newUser.getRole().equalsIgnoreCase(currentUser.getRole())) {
					System.err.println("userToUpdate");
					found=true;
					currentUser.setRole(newUser.getRole());
					usersToUpdate.add(currentUser);
				}
				
			}
			if(found) {
				currentUsersIterator.remove();
				newUsersIterator.remove();
			} else {
				System.err.println("userToRemove");
			}
		}
		if(currentUsersList.isEmpty() && newUsersList.isEmpty() && usersToUpdate.isEmpty()) {
			messages.add("No modification to the list");	
		}
		if (!messages.isEmpty()) {
			results.put("status", "failure");
			results.put("messages", messages);
		} else {
			results.put("status", "success");
			messages.add("Modification correctly made");
		}
		return results;
	}
	
	private static List<String> addUsersToEvent(EventCreation eventToUpdate, Collection<UserCreation> users) {
		ArrayList<String> messages = new ArrayList<>();
		eventToUpdate.setUsers(users);
		if(!DirectoryAPIService.addUsersToGroup(eventToUpdate.getGroupId(), users)) {
			messages.add("Error while adding a new user to the group");
			return messages;
		} else {
			HashMap<String, Object> checks = DriveAPIService.addUsersToFolderForEvent(eventToUpdate);
			if(checks.get("status").equals("failure")) {
				return (List<String>) (checks.get("messages"));
			} else {
				createUserIfNeeded(eventToUpdate, messages);
//				messages.add("User properly added to the Event");
			}
		}
		return messages;
	}
	
	private static List<String> updateUsersForEvent(EventCreation eventToUpdate, Collection<UserCreation> users) {
		ArrayList<String> messages = new ArrayList<>();
		eventToUpdate.setUsers(users);
			HashMap<String, Object> checks = DriveAPIService.updateUsersFolderForEvent(eventToUpdate);
			if(checks.get("status").equals("failure")) {
				return (List<String>) (checks.get("messages"));
			} else {
				updateUsersInDB(eventToUpdate, messages);
//				messages.add("User properly updated for the Event");
			}
		return messages;
	}
	
	private static List<String> removeUsersForEvent(EventCreation eventToUpdate, Collection<UserCreation> users) {
		ArrayList<String> messages = new ArrayList<>();
		eventToUpdate.setUsers(users);
		if(!DirectoryAPIService.removeUsersFromGroup(eventToUpdate.getGroupId(), users)) {
			messages.add("Error while removing a member from the group");
			return messages;
		} else {
			HashMap<String, Object> checks = DriveAPIService.removeUsersFolderForEvent(eventToUpdate);
			if(checks.get("status").equals("failure")) {
				return (List<String>) (checks.get("messages"));
			} else {
				removeUsersInDB(eventToUpdate, messages);
//				messages.add("User properly removed from the Event");
			}
		}
		return messages;
	}
	
	public static String updateEvent(EventCreation eventToUpdate) {
		HashMap<String, Object> results = new HashMap<String, Object>();
		List<String> messages = new ArrayList<String>();
		checkUsers(eventToUpdate, messages);
		if(!messages.isEmpty()) {
			
			results.put("status", "failure");
			results.put("messages", messages);
			return Tools.gson.toJson(results);
		}
		UserDao userDao = new UserDao();
		
		Collection<UserCreation> removeUsersList = userDao.getUserCreationsForEventId(eventToUpdate.getDbId());
		Collection<UserCreation> updateUsersList = new ArrayList<UserCreation>();
		Collection<UserCreation> addUsersList = eventToUpdate.getUsers();
		results = fillUsersListForRemoveAddUpdate(addUsersList,removeUsersList,updateUsersList);
		if (results.get("status").equals("failure")) {
			return Tools.gson.toJson(results);
		}
		
		
		for(UserCreation userToAdd : addUsersList) {
			System.err.println("User a ajouter = " + userToAdd.getMail() + " -- " + userToAdd.getName() + " -- " + userToAdd.getRole()+ " -- " + userToAdd.getDbId() + " -- "+ userToAdd.getInProgressFolderId());
		}
		for(UserCreation usersToUpdate : updateUsersList) {
			System.err.println("User a mettre à jour = " + usersToUpdate.getMail() + " -- " + usersToUpdate.getName() + " -- " + usersToUpdate.getRole()+ " -- " + usersToUpdate.getDbId() + " -- "+ usersToUpdate.getInProgressFolderId());
		}
		for(UserCreation usersToRemove : removeUsersList) {
			System.err.println("User a supprimer = " + usersToRemove.getMail() + " -- " + usersToRemove.getName() + " -- " + usersToRemove.getRole()+ " -- " + usersToRemove.getDbId() + " -- "+ usersToRemove.getInProgressFolderId());
		}
		
		
		messages = addUsersToEvent(eventToUpdate, addUsersList);
		if(!messages.isEmpty()) {
			results.put("status", "failure");
			results.put("messages", messages);
			return Tools.gson.toJson(results);
		}
		
		messages = updateUsersForEvent(eventToUpdate, updateUsersList);
		if(!messages.isEmpty()) {
			results.put("status", "failure");
			results.put("messages", messages);
			return Tools.gson.toJson(results);
		}
		
		messages = removeUsersForEvent(eventToUpdate, removeUsersList);
		if(!messages.isEmpty()) {
			results.put("status", "failure");
			results.put("messages", messages);
			return Tools.gson.toJson(results);
		}
		
		messages.add("Event properly updated");
		results.put("status", "success");
		results.put("messages", messages);
		
		return null;
		
		/*Iterator<UserCreation> usersIterator = eventToUpdate.getUsers().iterator();
		while (usersIterator.hasNext()) {
			UserCreation user = (UserCreation) usersIterator.next();
			if (updateListForUser(user, eventToUpdate.getUsers(), usersToRemove, usersToUpdate)) {
				usersIterator.remove();
			}
		}
		
//		else {
			// continue process
			//String groupId=DirectoryAPIService.createGroup(eventToCreate);
			if (!DirectoryAPIService.createGroup(eventToCreate)) {
				ArrayList<String> messages = new ArrayList<String>();
				messages.add("Error during Group creation");
				checks.put("messages", messages);
				checks.put("status", "failure");
				return Tools.gson.toJson(checks);
			}
//			GroupssettingsAPIService.changeGroupssettings(groupId);
			if(!DirectoryAPIService.addUsersToGroup(eventToCreate)) {
				ArrayList<String> messages = new ArrayList<String>();
				messages.add("Error while adding users to Group");
				checks.put("messages", messages);
				checks.put("status", "failure");
				DirectoryAPIService.removeGroup(eventToCreate.getGroupId());
				return Tools.gson.toJson(checks);
			}
		checks = DriveAPIService.createFolderStructure(eventToCreate);
		if ("failure".equals(checks.get("status"))) {
			if(!StringUtils.isBlank(checks.get("folderId").toString())) {
				DriveAPIService.removeFolderStructure(checks.get("folderId").toString());
			}
			DirectoryAPIService.removeGroup(eventToCreate.getGroupId());
			return Tools.gson.toJson(checks);
		}
		String eventFolderId = checks.get("folderId").toString();
			checks = createDatabaseRecords(eventToCreate);
			if ("failure".equals(checks.get("status"))) {
				DriveAPIService.removeFolderStructure(eventFolderId.toString());
				DirectoryAPIService.removeGroup(eventToCreate.getGroupId());
				return Tools.gson.toJson(checks);
			}
			return Tools.gson.toJson(checks);
//		}*/
		
	}
}
