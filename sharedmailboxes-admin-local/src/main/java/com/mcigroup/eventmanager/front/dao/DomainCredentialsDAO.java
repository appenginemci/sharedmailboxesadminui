package com.mcigroup.eventmanager.front.dao;

import com.mcigroup.eventmanager.front.helper.PropertiesManager;
import com.mcigroup.eventmanager.front.model.DomainCredentials;


public class DomainCredentialsDAO {

	public static DomainCredentials loadDomainCredentials(){
		DomainCredentials domainCredentials = new DomainCredentials();
		
		domainCredentials.setUserEmailAddress(PropertiesManager.getProperty("user.email"));
		domainCredentials.setServiceAccountEmail(PropertiesManager.getProperty("service.account.email"));
		domainCredentials.setCertificatePath(PropertiesManager.getProperty("service.certificate.path"));

		return domainCredentials;
	}

}