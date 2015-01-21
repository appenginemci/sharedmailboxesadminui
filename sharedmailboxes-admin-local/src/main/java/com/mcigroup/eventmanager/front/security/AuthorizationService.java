package com.mcigroup.eventmanager.front.security;


import com.mcigroup.eventmanager.front.model.UserSession;
import com.mcigroup.eventmanager.front.service.DirectoryAPIService;

public class AuthorizationService {
	
//	private static String groupEmailAddress = "googleforwork-zurich2015-reg@mci-group.com";
	
	public static UserSession getAuthorization(String userEmail){
		
		UserSession userSession = null;
		
//		if(DirectoryAPIService.isExistingGroup(groupEmailAddress)){
//			if(DirectoryAPIService.isGroupMember(groupEmailAddress, userEmail)){
//		DirectoryAPIService.listGroup();
		if(DirectoryAPIService.isInDomainUser(userEmail)) {
				userSession = new UserSession();
				userSession.setEmail(userEmail);
		} else {
			System.err.println(userEmail + " is not in the right domain");
		}
//			}else{
//				System.err.println(userEmail + " is not member of " + groupEmailAddress);
//			}
//		}else{
//			System.err.println(groupEmailAddress + " is not existing");
//		}
		
		return userSession;
	}

}
