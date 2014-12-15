package com.mcigroup.eventmanager.front.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import com.mcigroup.eventmanager.front.helper.ConnectionUtil;
import com.mcigroup.eventmanager.front.model.EventCreation;
import com.mcigroup.eventmanager.front.model.UserCreation;

public class UserDao {
	
	public boolean linkMemberToEvent(EventCreation eventToCreate, UserCreation user) {
		Connection conn = ConnectionUtil.getConnection();
		try {
			try{
			String statement = 	"SELECT em.id from eventmember em, event ev, member me where ev.folderId = ? and me.userId = ? and em.user_id=me.id and em.event_id=ev.id";
			PreparedStatement stmt;

			stmt = conn.prepareStatement(statement);
			stmt.setString(1, eventToCreate.getEventFolderId());
			stmt.setString(2, user.getMail());	
			
			ResultSet results = stmt.executeQuery();
			if(!results.next()) {
				statement = "INSERT INTO eventmember (event_id,user_id,role,active,in_progress_folder_id,for_approval_folder_id) SELECT ev.id,me.id, '" + user.getRole() + "', 1, '" + user.getInProgressFolderId() + "', '" + user.getForApprovalId() +"' from event ev, member me where ev.folderId=? and me.userId=?";

				stmt = conn.prepareStatement(statement);
				stmt.setString(1, eventToCreate.getEventFolderId());
				stmt.setString(2, user.getMail());
			
				int success = stmt.executeUpdate();
				if(success == 1) {
					return true;
				}
			} else {
				int id = results.getInt("id");
				stmt.close();
				statement = "UPDATE eventmember set role='" + user.getRole() + "',"
						+ "in_progress_folder_id = '" + user.getInProgressFolderId() + "',"
								+ "for_approval_folder_id = '" + user.getForApprovalId() + "',"
								+ "active=1 where id = ?";
				
				stmt = conn.prepareStatement(statement);
				stmt.setInt(1, id);
			
				int success = stmt.executeUpdate();
				if(success == 1) {
					return true;
				}
			}
			
			}finally {
				conn.close();
			}
	} catch (SQLException e1) {
		System.err.println("connection error");
	}
		return false;
	}
	
	public boolean updateLinkMemberToEvent(UserCreation user) {
		Connection conn = ConnectionUtil.getConnection();
		boolean successVal = false;
		try {
			try{
				if(user.getDbId() != 0) {
					String statement = "UPDATE eventmember set in_progress_folder_id = ?, for_approval_folder_id = ?, role = ? where id = ? ";
					PreparedStatement stmt;
		
					stmt = conn.prepareStatement(statement);
					stmt.setString(1, user.getInProgressFolderId());
					stmt.setString(2, user.getForApprovalId());
					stmt.setString(3, user.getRole());
					stmt.setInt(4, user.getDbId());
					
					int success = stmt.executeUpdate();
					if(success == 1) {
						successVal = true;
					}
				}
			
			}finally {
				conn.close();
			}
	} catch (SQLException e1) {
		System.err.println("connection error");
	}
		return successVal;
	}
	
	public boolean removeLinkMemberFromEvent(UserCreation user) {
		Connection conn = ConnectionUtil.getConnection();
		boolean successVal = false;
		try {
			try{
				if(user.getDbId() != 0) {
					String statement = "UPDATE eventmember set active = 0 where id = ? ";
					PreparedStatement stmt;
		
					stmt = conn.prepareStatement(statement);
					stmt.setInt(1, user.getDbId());
					
					int success = stmt.executeUpdate();
					if(success == 1) {
						successVal = true;
					}
				}
			
			}finally {
				conn.close();
			}
	} catch (SQLException e1) {
		System.err.println("connection error");
	}
		return successVal;
	}
	
	
	public boolean addUser(UserCreation user) {
		Connection conn = ConnectionUtil.getConnection();
		try {
			try{
			String statement = "INSERT INTO member (userId,userName) VALUES (?, ?)";
			PreparedStatement stmt;

			stmt = conn.prepareStatement(statement);
			stmt.setString(1, user.getMail());
			stmt.setString(2, user.getName());
			
			int success = stmt.executeUpdate();
			if(success == 1) {
//				statement = "INSERT INTO consumeruser (user_id,consumertype_id) SELECT me.id,co.id from member me, consumertype co where me.userId = ? and co.consumertype = ?";
//				stmt = conn.prepareStatement(statement);
//				stmt.setString(1, user.getMail());
//				if(UserRoleEnum.TEAMMEMBER.getUserRole().equals(user.getRole())) {
//					stmt.setString(2, ConsumerTypeEnum.USER.getConsumerType());
//				} else {
//					stmt.setString(2, ConsumerTypeEnum.MANAGER.getConsumerType());
//				}
//				success = stmt.executeUpdate();
//				if(success == 1) {
					return true;
//				}
			}
			
			}finally {
				conn.close();
			}
	} catch (SQLException e1) {
		System.err.println("connection error");
	}
		return false;
	}
	
	
	public UserCreation getUserByEmail(String userEmail) {
		Connection conn = ConnectionUtil.getConnection();
		try {
			try{
				String statement = "SELECT me.id,me.userName,me.userId FROM member me where me.userId = ?";
				PreparedStatement stmt;
	
				stmt = conn.prepareStatement(statement);
	
				stmt.setString(1, userEmail);
				ResultSet resultSet = stmt.executeQuery();
				while (resultSet.next()) {
	//				System.out.println("userId = " + resultSet.getInt("id"));
	//				return new User(resultSet.getInt("id"),resultSet.getString("userName"),userEmail);
					UserCreation userCreation = new UserCreation();
					userCreation.setDbId(resultSet.getInt("id"));
					userCreation.setName(resultSet.getString("userName"));
					userCreation.setMail(userEmail);
					return userCreation;
				}
			}finally {
				conn.close();
			}
	} catch (SQLException e1) {
		System.err.println("connection error");
	}
		return null;
	}
	
	
	public Collection<UserCreation> getUserCreationsForEventId(int eventDbId) {
		Connection conn = ConnectionUtil.getConnection();
		Collection<UserCreation> users = new ArrayList<UserCreation>();
		try {
			try{
			String statement = "SELECT me.userName,me.userId, em.id, em.in_progress_folder_id, em.for_approval_folder_id, em.role FROM member me, eventmember em where me.id = em.user_id and em.event_id = ? and em.active=1";
			PreparedStatement stmt;

			stmt = conn.prepareStatement(statement);

			stmt.setInt(1, eventDbId);
			ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next()) {
//				System.out.println("userId = " + resultSet.getInt("id"));
				UserCreation user = new UserCreation();
				user.setName(resultSet.getString("userName"));
				user.setForApprovalId(resultSet.getString("for_approval_folder_id"));
				user.setInProgressFolderId(resultSet.getString("in_progress_folder_id"));
				user.setMail(resultSet.getString("userId"));
				user.setDbId(resultSet.getInt("id"));
				user.setRole(resultSet.getString("role"));
				users.add(user);
			}
			}finally {
				conn.close();
			}
	} catch (SQLException e1) {
		System.err.println("connection error");
	}
		return users;
	}
	
	
}
