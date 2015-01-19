package com.mcigroup.eventmanager.front.service;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;












import com.google.api.services.drive.Drive;
import com.google.gdata.client.spreadsheet.SpreadsheetQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.google.gdata.util.ServiceException;
import com.mcigroup.eventmanager.front.business.DataManager;
import com.mcigroup.eventmanager.front.dao.SiteDao;
import com.mcigroup.eventmanager.front.dao.UserDao;
import com.mcigroup.eventmanager.front.helper.PropertiesManager;
import com.mcigroup.eventmanager.front.model.EventCreation;
import com.mcigroup.eventmanager.front.model.Site;
import com.mcigroup.eventmanager.front.model.UserCreation;
import com.mcigroup.eventmanager.front.model.UserRoleEnum;
import com.mcigroup.eventmanager.front.security.CredentialLoader;

public class SpreadSheetDAO {

    
	private static SpreadsheetService spreadSheetService = getSpreadsheetService();

	private static SpreadsheetService getSpreadsheetService() {
	    SpreadsheetService toReturn = spreadSheetService;

		if (spreadSheetService == null) {
			toReturn = CredentialLoader.getSpreadsheetService();
		}

		return toReturn;
	}


    /**
     * Method to retrieve a spread sheet filtering with the exact name.
     * 
     * @param spreadSheetName
     * @return List<WorksheetEntry>
     * @throws IOException
     * @throws ServiceException
     */
    public static WorksheetEntry getWorkSheetByExactName(String spreadSheetName) throws IOException,
	    ServiceException {

	WorksheetEntry worksheetToReturn = null;

	// Define the URL to request. This should never change.
	URL SPREADSHEET_FEED_URL = new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full");

	// Create the query to filter the SpreadSheet
	SpreadsheetQuery query = new SpreadsheetQuery(SPREADSHEET_FEED_URL);

	// Value the exact name of the SpreadSheet
	query.setTitleExact(true);
	query.setTitleQuery(spreadSheetName);

	SpreadsheetFeed feed = spreadSheetService.getFeed(query,
		SpreadsheetFeed.class);
	List<SpreadsheetEntry> spreadsheets = feed.getEntries();

	if (spreadsheets.size() == 1) {

	    SpreadsheetEntry spreadsheet = spreadsheets.get(0);
//	    logger.info("Spreadsheet successfully retrieved.");
	    // + spreadsheet.getTitle().getPlainText());

	    // logger.debug("SpreadSheet Google ID: " +
	    // spreadsheet.getId());

	    WorksheetFeed worksheetFeed = spreadSheetService.getFeed(
		    spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);

	    worksheetToReturn = worksheetFeed.getEntries().get(0);

	}
	else {
	    System.out.println("\nno OR several spreadsheet(s) with this name exist in Drive. Name:" + spreadSheetName);
	}

	
					
	return worksheetToReturn;

    }
    


    /**
     * Method to
     * 
     * @param spreadSheetName
     * @return List<WorksheetEntry>
     * @throws IOException
     * @throws ServiceException
     */
    public static String processCSVFile(String spreadSheetName) throws IOException, ServiceException {
	System.err.println("In processCsvFile method");

	WorksheetEntry workSheet = SpreadSheetDAO.getWorkSheetByExactName("createEventByCSV");

	if (workSheet == null) {
	    // no OR several spreadsheet(s) with this name exist in Drive
	    return "no OR several spreadsheet(s) with this name exist in Drive";

	}
	else {

	    try {

		// Fetch the list feed of the worksheet.
		URL listFeedUrl = workSheet.getListFeedUrl();
		ListFeed listFeed = spreadSheetService.getFeed(listFeedUrl, ListFeed.class);


		// ==================
		// ====== LOOP ======
		// ==================
		for (int i = 0; i < listFeed.getTotalResults(); i++) {
		    ListEntry currentRow = listFeed.getEntries().get(i);

		    // Read cells of the line
		    String eventSite = currentRow.getCustomElements().getValue(PropertiesManager.getProperty("site"));
		    String eventType = currentRow.getCustomElements().getValue(PropertiesManager.getProperty("event_type"));
		    String eventName = currentRow.getCustomElements().getValue(PropertiesManager.getProperty("event_name"));
		    String eventTeamHeadUser = currentRow.getCustomElements().getValue(PropertiesManager.getProperty("leader_name"));
		    String eventTeamMembers = currentRow.getCustomElements().getValue(PropertiesManager.getProperty("list_of_members"));
		    String eventEmail = currentRow.getCustomElements().getValue(PropertiesManager.getProperty("event_email_address"));
		    
		    
		    System.out.println("eventSite = " + eventSite);
		    System.out.println("eventType = " + eventType);
		    System.out.println("eventName = " + eventName);
		    System.out.println("eventTeamHeadUser = " + eventTeamHeadUser);
		    System.out.println("eventTeamMembers = " + eventTeamMembers);
		    System.out.println("eventEmail = " + eventEmail);

		    List<String> teamMembers = new ArrayList<String>();

		    if (eventTeamMembers != null && !eventTeamMembers.isEmpty()) {
			teamMembers = Arrays.asList(eventTeamMembers.split(","));
		    }
		    HashMap<String, Object> checks = addEventByCSV(null, eventSite, eventName, eventType,
			    eventTeamHeadUser, teamMembers, eventEmail);

		    // Update the input "Result" Cell value
		    if (checks.get("status").equals("failure")) {

			String errorMessage = "";
			
			try {
			    errorMessage = checks.get("messages").toString();
			}
			catch (Exception e) {
			    System.out.println("Exception: " + e);
			    errorMessage = "no specific error message";
			}
			
			currentRow.getCustomElements().setValueLocal(PropertiesManager.getProperty("event_creation_result"), errorMessage);
			currentRow.update();
		    }
		    else {
			currentRow.getCustomElements().setValueLocal(PropertiesManager.getProperty("event_creation_result"), "OK");
			currentRow.update();
		    }
		}
	    }
	    catch (Exception e) {
		throw e;
	    }

	}
	return "correctEndOfTreatment";
    }
    


    @SuppressWarnings("null")
    public static <T> HashMap<String, Object> addEventByCSV(String event, String siteName, String eventName,
	    String eventType, String eventHead, List<String> teamMembers, String eventMail) {

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
	    DataManager.createNewSite(siteName);

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

	    if (eventHead != null && !eventHead.isEmpty()) {
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
	    if (teamMembers != null && !teamMembers.isEmpty()) {

		for (String i_user : teamMembers) {
		    UserCreation userTeamMember = userDao.getUserByEmail(i_user);
		    if (userTeamMember != null) {
			userTeamMember.setRole(UserRoleEnum.TEAMMEMBER.toString());
			listOfUsers.add(userTeamMember);
		    }
		    else {
			messages.add("Error while retrieving the event Team Member user <" + i_user
				+ "> from system.");
		    }
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
		eventToCreate.setMail(eventMail.substring(0, eventMail.indexOf("@")));


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
		
		//TODO GLA: don't forget to uncomment
		String result = EventCreationService.createEvent(eventToCreate);
		messages.add(result);
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


}
