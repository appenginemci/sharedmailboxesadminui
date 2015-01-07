package com.mcigroup.eventmanager.front.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mcigroup.eventmanager.front.helper.ConnectionUtil;
import com.mcigroup.eventmanager.front.model.Site;

public class SiteDao {


    public Collection<Site> getAllSites() {
	Connection conn = ConnectionUtil.getConnection();
	List<Site> siteList = new ArrayList<Site>();
	try {
	    try {
		String statement = "SELECT * FROM site order by name asc";
		PreparedStatement stmt;

		stmt = conn.prepareStatement(statement);

		ResultSet resultSet = stmt.executeQuery();

		while (resultSet.next()) {
		    Site site = new Site();
		    site.setID(resultSet.getInt("id"));
		    site.setName(resultSet.getString("name"));
		    site.setFolder_id(resultSet.getString("folder_id"));
//		    site.setCreation_date(resultSet.getDate("creation_date"));

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

    public int createSite(String siteName, String siteFolderID) {
	System.out.println("in createSite SQL method");
	Connection conn = ConnectionUtil.getConnection();
	int result = 0;
	try {
		try {
			String statement = "INSERT INTO site(name, folder_id, creation_date)  VALUES  (?,?, CURRENT_DATE)";
			PreparedStatement stmt;

			stmt = conn.prepareStatement(statement);
			stmt.setString(1, siteName);
			stmt.setString(2, siteFolderID);

			result = stmt.executeUpdate();
			 
		} finally {
			conn.close();
		}
	} catch (SQLException e1) {
		System.err.println("SQL error :" + e1);
	}
	return result;
}
    
}
