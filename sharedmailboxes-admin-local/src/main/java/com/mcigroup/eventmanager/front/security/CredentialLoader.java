package com.mcigroup.eventmanager.front.security;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.DirectoryScopes;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.groupssettings.Groupssettings;
import com.google.api.services.groupssettings.GroupssettingsScopes;
import com.mcigroup.eventmanager.front.dao.DomainCredentialsDAO;
import com.mcigroup.eventmanager.front.model.DomainCredentials;
import com.mcigroup.eventmanager.front.model.security.GoogleCredentialItem;
import com.google.gdata.client.spreadsheet.SpreadsheetService;

public class CredentialLoader {
	
		private static DomainCredentials domainCredentials = DomainCredentialsDAO.loadDomainCredentials();
		private static GoogleCredentialItem googleCredentialItem = generateGoogleCredentialItem(getAllServicesScopes());
		 
		public static Groupssettings getGroupssettings() {
			Groupssettings groupsSettingsService = null;
			if(googleCredentialItem != null){
				groupsSettingsService = new Groupssettings.Builder(googleCredentialItem.getHttpTransport(), googleCredentialItem.getJsonFactory(), googleCredentialItem.getGoogleCredential())
			      .setApplicationName("My Project").build();
			}
			return groupsSettingsService;
		}
		
		
		public static Drive getDriveService(){
			Drive driveService = null;
			if(googleCredentialItem != null){
				driveService = new Drive.Builder(googleCredentialItem.getHttpTransport(), googleCredentialItem.getJsonFactory(), googleCredentialItem.getGoogleCredential())
			      .setApplicationName("My Project").build();
			}
				  
		  return driveService;
		}
		
		
		    
		public static SpreadsheetService getSpreadsheetService(){
    		    SpreadsheetService spreadSheetService = new SpreadsheetService(
    			    "MySpreadsheetIntegration-v1");
    		    spreadSheetService.setProtocolVersion(SpreadsheetService.Versions.V3);
    		    spreadSheetService.setOAuth2Credentials(googleCredentialItem.getGoogleCredential());
		    
		  return spreadSheetService;
		}

		public static Directory getDirectoryService(){
			Directory directoryService = null;
			
			if(googleCredentialItem != null){
				directoryService = new Directory.Builder(googleCredentialItem.getHttpTransport(), googleCredentialItem.getJsonFactory(), null)
			      .setHttpRequestInitializer(googleCredentialItem.getGoogleCredential()).setApplicationName("My Project").build();
			}
				  
		  return directoryService;
		}

		private static GoogleCredentialItem generateGoogleCredentialItem(ArrayList<String> scopes){
			  HttpTransport httpTransport = new NetHttpTransport();
			  JacksonFactory jsonFactory = new JacksonFactory();
			  
			  GoogleCredential googleCredential = null;
			  GoogleCredentialItem googleCredentialItem = null;
			try {
				googleCredential = new GoogleCredential.Builder()
			      .setTransport(httpTransport)
			      .setJsonFactory(jsonFactory)
			      .setServiceAccountId(domainCredentials.getServiceAccountEmail())
			      .setServiceAccountScopes(scopes)
			      .setServiceAccountUser(domainCredentials.getUserEmailAddress())
			      .setServiceAccountPrivateKeyFromP12File(new File(CredentialLoader.class.getResource("/" + domainCredentials.getCertificatePath()).toURI()))
			      .build();
				
				googleCredentialItem = new GoogleCredentialItem();
				googleCredentialItem.setGoogleCredential(googleCredential);
				
			} catch (GeneralSecurityException e) {
				System.out.println("Error1: " + e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("Error2: " + e.getMessage());
				e.printStackTrace();
			} catch (URISyntaxException e) {
				System.out.println("Error3: " + e.getMessage());
				e.printStackTrace();
			}
			
			return googleCredentialItem;
		}
		
		
		private static ArrayList<String> getAllServicesScopes(){
			ArrayList<String> scopes = new ArrayList<String>();
			scopes.addAll(getDirectoryScopes());
			scopes.addAll(getDriveScopes());
			scopes.addAll(getGroupssettingsScopes());
			scopes.add("https://spreadsheets.google.com/feeds");
			return scopes;
		}
		
		/**
		 * 
		 * @return Google Drive API scopes required
		 */
		private static ArrayList<String> getDriveScopes(){
			ArrayList<String> scopes = new ArrayList<String>();
			scopes.add(DriveScopes.DRIVE);
			
			return scopes;
		}	
		
		/**
		 * 
		 * @return Google Drive API scopes required
		 */
		private static ArrayList<String> getDirectoryScopes(){
			ArrayList<String> scopes = new ArrayList<String>();
			scopes.add(DirectoryScopes.ADMIN_DIRECTORY_USER_READONLY);
			scopes.add(DirectoryScopes.ADMIN_DIRECTORY_USER_ALIAS);
			scopes.add(DirectoryScopes.ADMIN_DIRECTORY_GROUP);
			
			return scopes;
		}
		
		/**
		 * 
		 * @return Google Drive API scopes required
		 */
		private static ArrayList<String> getGroupssettingsScopes(){
			ArrayList<String> scopes = new ArrayList<String>();
			scopes.add(GroupssettingsScopes.APPS_GROUPS_SETTINGS);
			
			return scopes;
		}	
		
}