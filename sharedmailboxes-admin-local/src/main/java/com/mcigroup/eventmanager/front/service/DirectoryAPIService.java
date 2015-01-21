package com.mcigroup.eventmanager.front.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.Directory.Groups.List;
import com.google.api.services.admin.directory.model.Group;
import com.google.api.services.admin.directory.model.Groups;
import com.google.api.services.admin.directory.model.Member;
import com.google.api.services.admin.directory.model.User;
import com.google.api.services.admin.directory.model.Users;
import com.mcigroup.eventmanager.front.helper.PropertiesManager;
import com.mcigroup.eventmanager.front.helper.Tools;
import com.mcigroup.eventmanager.front.model.EventCreation;
import com.mcigroup.eventmanager.front.model.UserCreation;
import com.mcigroup.eventmanager.front.security.CredentialLoader;



public class DirectoryAPIService {
	
	private static Directory directory = getDirectory();
	private static String domain = PropertiesManager.getProperty("domain");
	private static Directory getDirectory(){
		Directory toReturn = directory;
		
		if(directory == null){ toReturn = CredentialLoader.getDirectoryService(); }
		
		return toReturn;
	}
	
	
	/**
	 * isInDomainUser : authentication method, to check that the user belongs to the domain
	 * @param userEmail : the user mail address
	 * @return true if the user belongs to the domain, or false
	 */
	public static boolean isInDomainUser(String userEmail){
		
		boolean isDomainUser = false;
			System.err.println("in isInDomainUser");
		try {
			com.google.api.services.admin.directory.Directory.Users.List userList = directory.users().list();
			System.err.println("Domain = " + domain);
			userList.setDomain(domain);
//			Users users = userList.execute();
//			System.err.println("Number of users = " + users.getUsers().size());
//			for(User user : users.getUsers()) {
//				System.err.println("user : " + user.getName());
//			}
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
	
	/**
	 * return all users of the domain, to give a hint in front end when adding users to an event
	 * @return a String representing the list of users
	 */
	public static String getAllUsers(){
		
			System.err.println("in getAllUsers");
			java.util.List<String> userEmails = new ArrayList<String>();
		try {
			com.google.api.services.admin.directory.Directory.Users.List userList = directory.users().list();
			userList.setDomain(domain);
			do {
				Users users = userList.execute();
				System.err.println("Number of users = " + users.getUsers().size());
				for(User user : users.getUsers()) {
//					System.err.println("user : " + user.getName());
					userEmails.add(user.getPrimaryEmail());
				}
				userList.setPageToken(users.getNextPageToken());
			} while (userList.getPageToken() != null &&
					userList.getPageToken().length() > 0);
			
			
		} catch (IOException e) {
			System.err.println("error while retrieving all users");
		}
		
		return Tools.gson.toJson(userEmails);
	}
	
	/**
	 * Check that the user inputed in front end exists in the domain
	 * @param userMail the user inputed
	 * @return true if the user exists, or false
	 */
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

	/**
	 * Return the corresponding User from a given email address
	 * @param userMail the mail address of the user
	 * @return the user or null
	 */
	public static User getUser(String userMail) {
		System.err.println("in getUser");
		User userToGet = null;
		try {
			userToGet = directory.users().get(userMail).execute();
		} catch (IOException e) {
			System.err.println("error while checking the user");
		}

		return userToGet;
	}
	public static void listGroup() {
		try {
//			Groups groups = directory.groups().list("demo.sogeti-reseller.com").execute();
			List groupsList = directory.groups().list();
			groupsList.setDomain(domain);
			Groups groups = groupsList.execute();
			System.err.println("Number of groups = " + groups.getGroups().size());
//			for(Group group : groups.getGroups()) {
//				System.err.println("Group email = " + group.getEmail());
//			}
		} catch (IOException e) {
			System.err.println("Error while retrieving groups");
			e.printStackTrace();
		}
	}
	
	/**
	 * Check that the inputed Event Name is free (= there is no group with the same name)
	 * @param eventName the event name to check
	 * @return the result of the check, or error
	 */
	public static String isEventNameAvailable(String eventName){
		
		System.err.println("in checkEventName");
		List groupsList;
		try {
			groupsList = directory.groups().list();
			groupsList.setDomain(domain);
			do {
				Groups groups = groupsList.execute();
				//System.err.println("Number of groups = " + groups.getGroups().size());
				for(Group group : groups.getGroups()) {
					if(group.getName().equalsIgnoreCase(eventName)) {
						return Tools.gson.toJson(false);
					}
				}
				
				groupsList.setPageToken(groups.getNextPageToken());
			} while (groupsList.getPageToken() != null &&
					groupsList.getPageToken().length() > 0);
			
			
			return Tools.gson.toJson(true);
		} catch (IOException e) {
			e.printStackTrace();
			return Tools.gson.toJson("error");
		}
		
	}
	
	/**
	 * check that an inputed address for the group is not already used
	 * @param eventMailPrefix : the address to check
	 * @return status of the check, or error
	 */
	public static String isEventMailPrefixAvailable(String eventMailPrefix){
		
		System.err.println("in checkEventMailPrefix");
		List groupsList;
		try {
			groupsList = directory.groups().list();
			groupsList.setDomain(domain);
			
			do {
				Groups groups = groupsList.execute();
				//System.err.println("Number of groups = " + groups.getGroups().size());
				for(Group group : groups.getGroups()) {
					if(group.getEmail().toLowerCase().startsWith(eventMailPrefix.toLowerCase())) {
						return Tools.gson.toJson(false);
					}
				}
				
				groupsList.setPageToken(groups.getNextPageToken());
			} while (groupsList.getPageToken() != null &&
					groupsList.getPageToken().length() > 0);
			
			
			return Tools.gson.toJson(true);
		} catch (IOException e) {
			e.printStackTrace();
			return Tools.gson.toJson("error");
		}
		
	}

	/**
	 * Create a new group, corresponding to the event inputed in Front End
	 * @param eventToCreate the event to create
	 * @return the status of the operation
	 */
	public static boolean createGroup(EventCreation eventToCreate) {
		Group groupToAdd = new Group();
		groupToAdd.setName(eventToCreate.getName());
		groupToAdd.setEmail(eventToCreate.getMail() + "@" + domain);
		boolean success = false;
		try {
			Group addedGroup = directory.groups().insert(groupToAdd).execute();
			System.err.println("added group id = " + addedGroup.getId());
			eventToCreate.setGroupId(addedGroup.getId());
			success=true;
		} catch (IOException e) {
			System.err.println("error while trying to add group");
			e.printStackTrace();
		}
		return success;
	}
	
	/**
	 * When something wrong happen during the creation process (after the group creation), remove the group
	 * @param groupId the id of the group to remove
	 * @return status of the remove
	 */
	public static boolean removeGroup(String groupId) {
		boolean success = false;
		try {
			directory.groups().delete(groupId).execute();
			System.err.println("group removed");
			success=true;
		} catch (IOException e) {
			System.err.println("error while removing group");
			e.printStackTrace();
		}
		return success;
	}
	
	/**
	 * Add the list of users to the created group
	 * @param groupId the id of the group on which we must add users
	 * @param usersToCreate list of users to add to the group
	 * @return status of the add operation
	 */
	public static boolean addUsersToGroup(String groupId, Collection<UserCreation> usersToCreate) {
//		Collection<UserCreation> usersToCreate = eventToCreate.getUsers();
		String adminUser = PropertiesManager.getProperty("admin_user");
		for(UserCreation userToCreate : usersToCreate) {
			if(!adminUser.equals(userToCreate.getMail())) {
			Member memberToAdd = new Member();
				try {
					User userToAdd = directory.users()
							.get(userToCreate.getMail()).execute();
					memberToAdd.setEmail(userToAdd.getPrimaryEmail());
					directory.members()
							.insert(groupId, memberToAdd)
							.execute();
					System.err.println("Member : " + userToCreate.getMail()
							+ " added to the group");
				} catch (IOException e) {
					System.err.println("error while trying to add user : "
							+ userToCreate.getMail());
					e.printStackTrace();
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Remove Users from a group, when updating the list of users
	 * @param groupId the id of the group from which we will remove users
	 * @param usersToRemove the list of users to remove
	 * @return remove operation status
	 */
	public static boolean removeUsersFromGroup(String groupId, Collection<UserCreation> usersToRemove) {
//		Collection<UserCreation> usersToCreate = eventToCreate.getUsers();
		for(UserCreation userToRemove : usersToRemove) {
				try {
					directory.members().delete(groupId, userToRemove.getMail()).execute();
					
					System.err.println("Member : " + userToRemove.getMail()
							+ " removed from the group");
				} catch (IOException e) {
					System.err.println("error while trying to remove member : "
							+ userToRemove.getMail() + " from the group");
					e.printStackTrace();
					return false;
				}
		}
		return true;
	}
	
	/**
	 * Add explicitly the admin user to the group (found in Properties)
	 * @param groupId
	 * @return
	 */
	public static boolean addAdminUserToGroup(String groupId) {
		String adminUser = PropertiesManager.getProperty("admin_user");
		// add admin user
		try {
			Member memberToAdd = new Member();
			memberToAdd.setEmail(adminUser);
			Member newMember = directory.members().insert(groupId, memberToAdd).execute();
			System.err.println("Member : " + newMember.getEmail() + " added to the group");
//			Alias aliasForAdmin = new Alias();
//			aliasForAdmin.setAlias(eventToCreate.getMail() + "@" + domain);
//			directory.users().aliases().insert(adminUser, aliasForAdmin).execute();
		} catch (IOException e) {
			System.err.println("error while trying to add user : " + adminUser);
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
}

