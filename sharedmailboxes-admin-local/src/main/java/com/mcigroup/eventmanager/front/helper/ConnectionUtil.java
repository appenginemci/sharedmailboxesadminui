package com.mcigroup.eventmanager.front.helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.google.appengine.api.utils.SystemProperty;

public class ConnectionUtil {

	public static Connection getConnection() {
		String url = null;
		Connection conn = null;
		String user = null;
		String password = null;
		try {
			if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
				// Load the class that provides the new "jdbc:google:mysql://"
				// prefix.
				System.err.println("get SQL cloud connection");
				Class.forName("com.mysql.jdbc.GoogleDriver");
				url = PropertiesManager.getProperty("url_distant");
				user = PropertiesManager.getProperty("user_distant");
				password = PropertiesManager.getProperty("password_distant");
			} else {
				// Local MySQL instance to use during development.
				System.err.println("Try to establish local connection with DB");
				Class.forName("com.mysql.jdbc.Driver");
//				url = "jdbc:mysql://127.0.0.1:3306/eventmanager";
				url = PropertiesManager.getProperty("url_local");
				user = PropertiesManager.getProperty("user_local");
				password = PropertiesManager.getProperty("password_local");

			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return null;
		}
		try {
//			conn = DriverManager.getConnection(url, "evtmgradmin", "sogeTTi$00");
			//conn = DriverManager.getConnection(url, "root", "sogeTTi$00");
			conn = DriverManager.getConnection(url, user, password);
			return conn;
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		} 
		return conn;
	}
}
