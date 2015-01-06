package com.mcigroup.eventmanager.front.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mcigroup.eventmanager.front.helper.ConnectionUtil;
import com.mcigroup.eventmanager.front.helper.PropertiesManager;
import com.mcigroup.eventmanager.front.model.EventCreation;
import com.mcigroup.eventmanager.front.model.Site;

public class SiteDao {


    public Collection<Site> getAllSites() {
	Connection conn = ConnectionUtil.getConnection();
	List<Site> siteList = new ArrayList<Site>();
	try {
	    try {
		String statement = "SELECT * FROM site";
		PreparedStatement stmt;

		stmt = conn.prepareStatement(statement);

		ResultSet resultSet = stmt.executeQuery();

		while (resultSet.next()) {
		    Site site = new Site();
		    site.setID(resultSet.getInt("id"));
		    site.setName(resultSet.getString("name"));
		    site.setFolder_id(resultSet.getString("folder_id"));
		    site.setCreation_date(resultSet.getDate("creation_date"));

		    siteList.add(site);
		}
	    }
	    finally {
		conn.close();
	    }
	}
	catch (SQLException e1) {
	    System.err.println("connection error");
	}
	
	System.err.println("Nbr of Sites retreived: " + siteList.size());
	
	return siteList;
    }

}
