package com.mcigroup.eventmanager.front.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.ChildList;
import com.google.api.services.drive.model.ChildReference;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.drive.model.PermissionList;
import com.mcigroup.eventmanager.front.helper.PropertiesManager;
import com.mcigroup.eventmanager.front.model.EventCreation;
import com.mcigroup.eventmanager.front.model.EventTypeEnum;
import com.mcigroup.eventmanager.front.model.Site;
import com.mcigroup.eventmanager.front.model.UserCreation;
import com.mcigroup.eventmanager.front.model.UserRoleEnum;
import com.mcigroup.eventmanager.front.security.CredentialLoader;

public class DriveAPIService {

	private static Drive drive = getDrive();

	private static Drive getDrive() {
		Drive toReturn = drive;

		if (drive == null) {
			toReturn = CredentialLoader.getDriveService();
		}

		return toReturn;
	}

	/**
	 * createFolder create a folder on Drive
	 * @param folderName the name of the folder to create
	 * @param parentId the id of containing folder of the one being created
	 * @param messages error messages
	 * @return the id of the created folder, or empty String;
	 */
	private static String createFolder(String folderName, String parentId,
			List<String> messages) {
		String toReturn = "";
		File eventFolder = new File();
		eventFolder.setTitle(folderName);
		eventFolder.setMimeType("application/vnd.google-apps.folder");
		ParentReference parent = new ParentReference();
		parent.setId(parentId);
		List<ParentReference> parents = new ArrayList<ParentReference>(1);
		parents.add(parent);
		eventFolder.setParents(parents);
		try {
			File createdFolder = drive.files().insert(eventFolder).execute();
			toReturn = createdFolder.getId();
		} catch (IOException e) {
			System.err.println("Error when trying to create folder : "
					+ folderName);
			messages.add("Error when trying to create folder : " + folderName);
			e.printStackTrace();
		}
		return toReturn;
	}
	
        /**
         * createNewSite create a folder on Drive in Root
         * 
         * @param folderName
         *            the name of the folder to create
         * @param messages
         *            error messages
         * @return the id of the created folder, or empty String;
         */
        public static String createNewSite(String folderName, List<String> messages) {
        	String toReturn = "";
        	File eventFolder = new File();
        	eventFolder.setTitle(folderName);
        	eventFolder.setMimeType("application/vnd.google-apps.folder");

        	try {
        	    File createdFolder = drive.files().insert(eventFolder).execute();
        	    toReturn = createdFolder.getId();
        	}
        	catch (IOException e) {
        	    System.err.println("Error when trying to create the new drive folder : " + folderName);
        	    messages.add("Error when trying to create the new drive folder : " + folderName
        		    + "/ Here is the error message: " + e);
        	    e.printStackTrace();
        	}
        	return toReturn;
        }
	
	/**
	 * unshareFolderWithUsers : When removing a user, remove the permission to see the event for the removed user
	 * @param eventFolderId : Id of the folder on which the permission will be removed
	 * @param usersToRemove : the users we must removed the permission on the folder
	 * @param messages : a list of error messages
	 */
	private static void unshareFolderWithUsers(String eventFolderId,
			Collection<UserCreation> usersToRemove, List<String> messages) {
		String adminUser = PropertiesManager.getProperty("admin_user");
		try {
		PermissionList permissions = drive.permissions().list(eventFolderId).execute();
		for (UserCreation user : usersToRemove) {
			if (!adminUser.equals(user.getMail())) {
				Permission newPermission = new Permission();

				newPermission.setValue(user.getMail());
				newPermission.setType("user");
				newPermission.setRole("writer");
				
					
					for(Permission permission : permissions.getItems()) {
						if(user.getMail().equalsIgnoreCase(permission.getEmailAddress())) {
							drive.permissions().delete(eventFolderId, permission.getId()).execute();
						}
					}
				
			}
		}
		} catch (IOException e) {
			System.out
					.println("Error while trying to unshare folder event id with user ");
			messages.add("Error while trying to unshare folder event id with user ");
		}
	}

	/**
	 * shareFolderWithUsers : when creating an event, or adding a member to an event, share the event folder with a list of member
	 * @param eventFolderId : the id of the folder to share
	 * @param usersToCreate : the list of members who must have access to the folder
	 * @param messages : a list of error messages
	 */
	private static void shareFolderWithUsers(String eventFolderId,
			Collection<UserCreation> usersToCreate, List<String> messages) {
		String adminUser = PropertiesManager.getProperty("admin_user");
		for (UserCreation user : usersToCreate) {
			if (!adminUser.equals(user.getMail())) {
				Permission newPermission = new Permission();

				newPermission.setValue(user.getMail());
				newPermission.setType("user");
				newPermission.setRole("writer");
				try {
					drive.permissions().insert(eventFolderId, newPermission)
							.execute();
				} catch (IOException e) {
					System.out
							.println("Error while sharing folder event id with user "
									+ user.getMail());
					messages.add("Error while sharing folder event id with user "
							+ user.getMail());
				}
			}
		}
	}

	/**
	 * shareOutboxTmpFolderWithUsers : share a technical folder to the members (used for mail sending)
	 * @param usersToCreate : The list of member who must have access to the folder
	 * @param messages : List of error messages
	 */
	private static void shareOutboxTmpFolderWithUsers(
			Collection<UserCreation> usersToCreate, List<String> messages) {
		for (UserCreation user : usersToCreate) {
			Permission newPermission = new Permission();

			newPermission.setValue(user.getMail());
			newPermission.setType("user");
			newPermission.setRole("writer");
			try {
				drive.permissions()
						.insert(PropertiesManager
								.getProperty("outboxTmpFolderId"),
								newPermission).setSendNotificationEmails(false)
						.execute();
				System.err.println("share outboxtmpFolder with user : "
						+ user.getMail());
			} catch (IOException e) {
				System.out
						.println("Error while sharing outboxtmpfolder event id with user "
								+ user.getMail());
				messages.add("Error while sharing outboxtmpfolder event id with user "
						+ user.getMail());
			}
		}
	}

	/**
	 * createFolderStructure : Create the folder Tree on Drive
	 * @param eventToCreate : the event to create
	 * @return a list of error messages, or a success 
	 */
	public static HashMap<String, Object> createFolderStructure(
			EventCreation eventToCreate) {
		HashMap<String, Object> results = new HashMap<String, Object>();
		ArrayList<String> messages = new ArrayList<String>();
		
		Site siteOfEvent = eventToCreate.getSite();
		String rootEventFolderId = siteOfEvent.getFolder_id();
		
		String eventFolderId = createFolder(eventToCreate.getName(),
				rootEventFolderId, messages);
		
		if (StringUtils.isNotBlank(eventFolderId)) {
			results.put("folderId", eventFolderId);
			eventToCreate.setEventFolderId(eventFolderId);
			shareFolderWithUsers(eventFolderId, eventToCreate.getUsers(),
					messages);
			if (EventTypeEnum.ABSTRACT.getConsumerType().equals(
					eventToCreate.getType())) {
				createEventAbstractStructure(eventToCreate, messages);
			} else if (EventTypeEnum.CONGRESS.getConsumerType().equals(
					eventToCreate.getType())) {
				createEventCongressStructure(eventToCreate, messages);
			}
		} else {
			results.put("folderId", "");
		}
		shareOutboxTmpFolderWithUsers(eventToCreate.getUsers(), messages);
		
		if (!messages.isEmpty()) {
			results.put("status", "failure");
		} else {
			results.put("status", "success");
		}
		results.put("messages", messages);
		return results;
	}

	/**
	 * removeFolderStructure removes permanently the event folder, in case of something goes wrong during the creation process
	 * @param folderId : the folder id to delete on drive
	 */
	public static void removeFolderStructure(String folderId) {
		try {
			drive.files().delete(folderId).execute();
			System.err.println("Folder created on drive has been removed");
		} catch (IOException e) {
			System.err
					.println("Error while trying to remove the folder created on drive");
		}
	}

	/**
	 * createEventAbstractStructure : Create the folder tree for an abstract event
	 * @param eventToCreate : the event to create
	 * @param messages : list of error messages
	 */
	private static void createEventAbstractStructure(
			EventCreation eventToCreate, List<String> messages) {
		String eventDocumentationId = createFolder("10-Event Documentation",
				eventToCreate.getEventFolderId(), messages);
		createFolder("10-Admin", eventDocumentationId, messages);
		createFolder("20-Submission Correspondence", eventDocumentationId,
				messages);
		createFolder("30-Notifications", eventDocumentationId, messages);
		createFolder("40-Review", eventDocumentationId, messages);
		createFolder("50-Withdrawal", eventDocumentationId, messages);
		createFolder("60-Delivery Failure", eventDocumentationId, messages);

		String inboxFolderId = createFolder("20-Inbox",
				eventToCreate.getEventFolderId(), messages);
		eventToCreate.setNewFolderId(inboxFolderId);

		createFolder("90-Settings", eventToCreate.getEventFolderId(), messages);
	}

	/**
	 * createUsersFolderForCongressEvent : when creating a congress event, create folders  for each member in In Progress, and a folder for each event head or pool head in For Approval
	 * @param eventToCreate the eventTo Create, containing the list of members
	 * @param inProgressFolderId the id of folder In Progress : for all members
	 * @param forApprovalFolderId the id of folder For Approval for Event Head or Pool Head
	 * @param messages error messages
	 * @return return the updated list of members, with the link to their created folders
	 */
	public static Collection<UserCreation> createUsersFolderForCongressEvent(
			EventCreation eventToCreate, String inProgressFolderId,
			String forApprovalFolderId, List<String> messages) {
		Collection<UserCreation> updatedUsers = new ArrayList<UserCreation>(
				eventToCreate.getUsers().size());
		for (UserCreation userToCreate : eventToCreate.getUsers()) {
			String inProgressUserFolderId = createFolder(
					userToCreate.getName(), inProgressFolderId, messages);
			userToCreate.setInProgressFolderId(inProgressUserFolderId);
			if (!UserRoleEnum.TEAMMEMBER.getUserRole().equals(
					userToCreate.getRole())) {
				String forApprovalUserFolderId = createFolder(
						userToCreate.getName(), forApprovalFolderId, messages);
				userToCreate.setForApprovalId(forApprovalUserFolderId);
			}
			updatedUsers.add(userToCreate);
		}
		return updatedUsers;
	}

	/**
	 * updateUsersFolderForCongressEvent update users : when the role of an user change, change the folder architecture :
	 * if the user was Team Member and is now Event Head or Pool Head, we must create a For Approval Folder
	 * if the user was Event Head or Pool Head and is now Team Member, we must remove its For Approval Folder
	 * if the user was Event Head or Pool Head and is now respectively Pool Head or Event Head, nothing to do
	 * @param eventToCreate the event, containing the list of users
	 * @param inProgressFolderId the id of the folder In Progress
	 * @param forApprovalFolderId the id of the folder For Approval
	 * @param messages a list of error messages
	 * @return the updated list of users, with their folders
	 */
	public static Collection<UserCreation> updateUsersFolderForCongressEvent(
			EventCreation eventToCreate, String inProgressFolderId,
			String forApprovalFolderId, List<String> messages) {
		Collection<UserCreation> updatedUsers = new ArrayList<UserCreation>(
				eventToCreate.getUsers().size());
		for (UserCreation userToCreate : eventToCreate.getUsers()) {
			if (!UserRoleEnum.TEAMMEMBER.getUserRole().equals(
					userToCreate.getRole())) {
				// In the case of moving from Pool Head to Event Head or backward, don't create a for Approval Folder
				if (StringUtils.isBlank(userToCreate.getForApprovalId()) || "null".equalsIgnoreCase(userToCreate.getForApprovalId())) {
					String forApprovalUserFolderId = createFolder(
						userToCreate.getName(), forApprovalFolderId, messages);
					userToCreate.setForApprovalId(forApprovalUserFolderId);
				}
			} else {
				// we must remove the folder For Approval
				if (!removeFolder(userToCreate.getForApprovalId(),
						eventToCreate.getNewFolderId())) {
					messages.add("Error while removing For Approval folder for user "
							+ userToCreate.getMail());
				} else {
					userToCreate.setForApprovalId(null);
				}
			}
			updatedUsers.add(userToCreate);
		}
		return updatedUsers;
	}

	/**
	 * removeUsersFolderForCongressEvent remove the folders on Drive for the users to remove
	 * @param eventToCreate : the event containing the list of users to remove
	 * @param messages : a list of error messages
	 */
	public static void removeUsersFolderForCongressEvent(
			EventCreation eventToCreate, List<String> messages) {
		for (UserCreation userToCreate : eventToCreate.getUsers()) {
			if (!removeFolder(userToCreate.getInProgressFolderId(),
					eventToCreate.getNewFolderId())) {
				messages.add("Error while removing In Progress Folder for user "
						+ userToCreate.getMail());
			}
			if (!UserRoleEnum.TEAMMEMBER.getUserRole().equals(
					userToCreate.getRole())) {
				if (!removeFolder(userToCreate.getForApprovalId(),
						eventToCreate.getNewFolderId())) {
					messages.add("Error while removing For Approval Folder for user "
							+ userToCreate.getMail());
				}
			}
		}
	}

	/**
	 * removeFolder : move files from the folder to remove to the "Inbox" folder, and remove a folder
	 * @param folderIdToRemove : the folder which will be removed
	 * @param folderIdToReceiveFiles : the Inbox folder which will contains files
	 * @return true if the remove happens, false else
	 */
	private static boolean removeFolder(String folderIdToRemove,
			String folderIdToReceiveFiles) {
		boolean success = false;
		try {
			ChildList childs = drive.children().list(folderIdToRemove)
					.execute();
			for (ChildReference child : childs.getItems()) {
				ParentReference parentReference = new ParentReference();
				parentReference.setId(folderIdToReceiveFiles);
				drive.parents().insert(child.getId(), parentReference)
						.execute();

				drive.parents().delete(child.getId(), folderIdToRemove)
						.execute();

			}
			drive.files().trash(folderIdToRemove).execute();
			success = true;
		} catch (IOException e) {
			e.printStackTrace();
			return success;
		}
		return success;
	}

	/**
	 * createEventCongressStructure : create the folder tree for a Congress Event
	 * @param eventToCreate the event to create
	 * @param messages a list of error messages
	 */
	public static void createEventCongressStructure(
			EventCreation eventToCreate, List<String> messages) {
		createFolder("10-Event Documentation",
				eventToCreate.getEventFolderId(), messages);
		String inboxFolderId = createFolder("20-Inbox",
				eventToCreate.getEventFolderId(), messages);
		String newFolderId = createFolder("10-New", inboxFolderId, messages);
		eventToCreate.setNewFolderId(newFolderId);
		String inProgressFolderId = createFolder("20-In Progress",
				inboxFolderId, messages);
		// create a folder for each user who is team member
		String forApprovalFolderId = createFolder("50-For Approval",
				inboxFolderId, messages);
		// create a folder for each user who is not team member
		Collection<UserCreation> updatedUsers = new ArrayList<UserCreation>(
				eventToCreate.getUsers().size());
		updatedUsers = createUsersFolderForCongressEvent(eventToCreate,
				inProgressFolderId, forApprovalFolderId, messages);
		eventToCreate.setUsers(updatedUsers);
		String closedFolderId = createFolder("80-Closed", inboxFolderId,
				messages);
		eventToCreate.setClosedFolderId(closedFolderId);
		String attachmentsFolderId = createFolder("90-Attachments",
				inboxFolderId, messages);
		eventToCreate.setAttachmentsFolderId(attachmentsFolderId);
		String settingsFolder = createFolder("90-Settings",
				eventToCreate.getEventFolderId(), messages);
		createFolder("Mail Sender", settingsFolder, messages);
	}

	/**
	 * Retrieve the id of the In Progress Folder for an event (of type congress)
	 * @param eventFolderId the event Folder Id
	 * @return empty String if not found, the id if found
	 */
	private static String getInProgressFolderIdForEvent(String eventFolderId) {
		String inProgressFolderId = "";
		try {
			ChildList inboxFolder = drive
					.children()
					.list(eventFolderId)
					.setQ("mimeType = 'application/vnd.google-apps.folder' and title = '20-Inbox' and trashed = false")
					.execute();
			if (inboxFolder.getItems().size() == 1) {
				ChildReference inbox = inboxFolder.getItems().get(0);
				ChildList inprogressFolder = drive
						.children()
						.list(inbox.getId())
						.setQ("mimeType = 'application/vnd.google-apps.folder' and title = '20-In Progress' and trashed = false")
						.execute();
				if (inprogressFolder.getItems().size() == 1) {
					inProgressFolderId = inprogressFolder.getItems().get(0)
							.getId();
					System.err.println("inProgressFolderId = "
							+ inProgressFolderId);
				}
			}
		} catch (IOException e) {
			System.err
					.println("error while trying to retrieve inProgressFolder id");
			e.printStackTrace();
		}
		return inProgressFolderId;
	}

	/**
	 * Retrieve the id of the For Approval Folder for an event (of type congress)
	 * @param eventFolderId the event Folder Id
	 * @return empty String if not found, the id if found
	 */
	private static String getForApprovalFolderIdForEvent(String eventFolderId) {
		String forApprovalFolderId = "";
		try {
			ChildList inboxFolder = drive
					.children()
					.list(eventFolderId)
					.setQ("mimeType = 'application/vnd.google-apps.folder' and title = '20-Inbox' and trashed = false")
					.execute();
			if (inboxFolder.getItems().size() == 1) {
				ChildReference inbox = inboxFolder.getItems().get(0);
				ChildList forApprovalFolder = drive
						.children()
						.list(inbox.getId())
						.setQ("mimeType = 'application/vnd.google-apps.folder' and title = '50-For Approval' and trashed = false")
						.execute();
				if (forApprovalFolder.getItems().size() == 1) {
					forApprovalFolderId = forApprovalFolder.getItems().get(0)
							.getId();
					System.err.println("forApprovalFolderId = "
							+ forApprovalFolderId);
				}
			}
		} catch (IOException e) {
			System.err
					.println("error while trying to retrieve forApprovalFolder id");
			e.printStackTrace();
		}
		return forApprovalFolderId;
	}

	/**
	 * addUsersToFolderForEvent : when creating a user, eventually creating its personal drive folders, and share the event with the user
	 * @param event the event created
	 * @return a Map containing the result of the creation
	 */
	public static HashMap<String, Object> addUsersToFolderForEvent(
			EventCreation event) {
		HashMap<String, Object> results = new HashMap<String, Object>();
		ArrayList<String> messages = new ArrayList<String>();
		if (EventTypeEnum.CONGRESS.getConsumerType().equals(event.getType())) {
			String inProgressFolderId = DriveAPIService
					.getInProgressFolderIdForEvent(event.getEventFolderId());
			String forApprovalFolderId = DriveAPIService
					.getForApprovalFolderIdForEvent(event.getEventFolderId());
			event.setUsers(createUsersFolderForCongressEvent(event,
					inProgressFolderId, forApprovalFolderId, messages));
		}
		shareFolderWithUsers(event.getEventFolderId(), event.getUsers(),
				messages);
		shareOutboxTmpFolderWithUsers(event.getUsers(), messages);
		if (!messages.isEmpty()) {
			results.put("status", "failure");
		} else {
			results.put("status", "success");
		}
		results.put("messages", messages);
		return results;
	}

	/**
	 * updateUsersFolderForEvent : when updating a user, eventually create or remove its personal drive folders, 
	 * @param event the event updated
	 * @return a Map containing the result of the update
	 */
	public static HashMap<String, Object> updateUsersFolderForEvent(
			EventCreation event) {
		HashMap<String, Object> results = new HashMap<String, Object>();
		ArrayList<String> messages = new ArrayList<String>();
		if (EventTypeEnum.CONGRESS.getConsumerType().equals(event.getType())) {
			String inProgressFolderId = DriveAPIService
					.getInProgressFolderIdForEvent(event.getEventFolderId());
			String forApprovalFolderId = DriveAPIService
					.getForApprovalFolderIdForEvent(event.getEventFolderId());
			event.setUsers(updateUsersFolderForCongressEvent(event,
					inProgressFolderId, forApprovalFolderId, messages));
		}
		if (!messages.isEmpty()) {
			results.put("status", "failure");
		} else {
			results.put("status", "success");
		}
		results.put("messages", messages);
		return results;
	}

	/**
	 * removeUsersFolderForEvent : remove the Drive content associated to an user for an event, and remove the sharing of the event tree with him.
	 * @param event the event, containing the list of users to remove
	 * @return a Map containing the success or failure of the remove
	 */
	public static HashMap<String, Object> removeUsersFolderForEvent(
			EventCreation event) {
		HashMap<String, Object> results = new HashMap<String, Object>();
		ArrayList<String> messages = new ArrayList<String>();
		if (EventTypeEnum.CONGRESS.getConsumerType().equals(event.getType())) {
			removeUsersFolderForCongressEvent(event, messages);
		}
		if (!messages.isEmpty()) {
			results.put("status", "failure");
		} else {
			unshareFolderWithUsers(event.getEventFolderId(), event.getUsers(), messages);
			if (!messages.isEmpty()) {
				results.put("status", "failure");
			} else {
				unshareFolderWithUsers(event.getEventFolderId(), event.getUsers(), messages);
				
				results.put("status", "success");
			}
		}
		
		results.put("messages", messages);
		return results;
	}
}
