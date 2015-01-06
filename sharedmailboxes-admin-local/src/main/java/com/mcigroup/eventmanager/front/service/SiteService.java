package com.mcigroup.eventmanager.front.service;

import java.util.Collection;

import com.mcigroup.eventmanager.front.dao.EventDao;
import com.mcigroup.eventmanager.front.dao.SiteDao;
import com.mcigroup.eventmanager.front.dao.UserDao;
import com.mcigroup.eventmanager.front.helper.Tools;
import com.mcigroup.eventmanager.front.model.EventCreation;
import com.mcigroup.eventmanager.front.model.Site;
import com.mcigroup.eventmanager.front.model.UserCreation;

public class SiteService {

	public static String getAllSites() {
		SiteDao siteDao = new SiteDao();
		Collection<Site> siteList = siteDao.getAllSites();
		return Tools.gson.toJson(siteList);

	}
	
}
