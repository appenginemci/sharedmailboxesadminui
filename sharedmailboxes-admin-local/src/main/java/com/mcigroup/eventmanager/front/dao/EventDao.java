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

public class EventDao {
	
	
	public Collection<EventCreation> getAllEvents() {
		Connection conn = ConnectionUtil.getConnection();
		List<EventCreation> events = new ArrayList<EventCreation>();
		try {
			try {
				String statement = "SELECT event.* FROM event";
				PreparedStatement stmt;

				stmt = conn.prepareStatement(statement);

//				System.err.println("in getEventByUser with User id = " + user.getId());
				ResultSet resultSet = stmt.executeQuery();
				while (resultSet.next()) {
//					System.out.println("folderId = "
//							+ resultSet.getString("folderId"));
					EventCreation event = new EventCreation();
					event.setDbId(resultSet.getInt("id"));
					event.setEventFolderId(resultSet.getString("folderId"));
					event.setNewFolderId(resultSet
							.getString("inboxNewFolderId"));
					event.setName(resultSet
							.getString("eventName"));
					event.setGroupId(resultSet
							.getString("googleGroupId"));
					event.setType(resultSet.getString("evtType"));
					events.add(event);
				}
			} finally {
				conn.close();
			}
		} catch (SQLException e1) {
			System.err.println(e1);
		}
		return events;
	}
	
	public EventCreation getEventCreationForEventGroup(String groupId) {
		Connection conn = ConnectionUtil.getConnection();
		try {
			try {
				String statement = "SELECT event.* FROM event where googleGroupId = ?";
				PreparedStatement stmt;
				
				stmt = conn.prepareStatement(statement);
				stmt.setString(1, groupId);
				
//				System.err.println("in getEventByUser with User id = " + user.getId());
				ResultSet resultSet = stmt.executeQuery();
				while (resultSet.next()) {
//					System.out.println("folderId = "
//							+ resultSet.getString("folderId"));
					EventCreation event = new EventCreation();
					event.setDbId(resultSet.getInt("id"));
					event.setEventFolderId(resultSet.getString("folderId"));
					event.setNewFolderId(resultSet
							.getString("inboxNewFolderId"));
					event.setName(resultSet
							.getString("eventName"));
					event.setGroupId(resultSet
							.getString("googleGroupId"));
					event.setType(resultSet
							.getString("evtType"));
					return event;
				}
			} finally {
				conn.close();
			}
		} catch (SQLException e1) {
			System.err.println(e1);
		}
		return null;
	}
	
	public int createEvent(EventCreation eventToCreate) {
		Connection conn = ConnectionUtil.getConnection();
		int lastInsertId = 0;
		try {
			try {
				String statement = "INSERT INTO event(eventName,googleGroupId,folderId,email,inboxNewFolderId,attachmentFolderId,closedFolderId,site_id,evtType) VALUES  (?,?,?,?,?,?,?,?,?)";
				PreparedStatement stmt;

				stmt = conn.prepareStatement(statement);
				stmt.setString(1, eventToCreate.getName());
				stmt.setString(2, eventToCreate.getGroupId());
				stmt.setString(3, eventToCreate.getEventFolderId());
				stmt.setString(4, eventToCreate.getMail() + "@" + PropertiesManager.getProperty("domain"));
				stmt.setString(5, eventToCreate.getNewFolderId());
				stmt.setString(6, eventToCreate.getAttachmentsFolderId());
				stmt.setString(7, eventToCreate.getClosedFolderId());
				
				Site siteOfEvent = eventToCreate.getSite();
				stmt.setInt(8, siteOfEvent.getID());
				
				stmt.setString(9, eventToCreate.getType());
//				System.err.println("in getEventByUser with User id = " + user.getId());
				int result = stmt.executeUpdate();
				if(result == 1) {
					statement = "SELECT LAST_INSERT_ID() as id";
					stmt = conn.prepareStatement(statement);
					ResultSet resultSet = stmt.executeQuery();
					resultSet.next();
					lastInsertId = resultSet.getInt(1);
				}
			} finally {
				conn.close();
			}
		} catch (SQLException e1) {
			System.err.println("error :" + e1);
		}
		return lastInsertId;
	}
}
