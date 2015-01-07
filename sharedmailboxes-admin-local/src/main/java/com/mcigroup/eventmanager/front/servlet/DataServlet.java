package com.mcigroup.eventmanager.front.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.mcigroup.eventmanager.front.business.DataManager;



public class DataServlet  extends HttpServlet {

	private static final long serialVersionUID = -1488008862968967881L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException{

		String toReturn = null;
//		String userEmail = null;

		UserService userService = UserServiceFactory.getUserService();
		String type = req.getParameter("type");
		
		if (req.getUserPrincipal() != null) {
			if (req.getUserPrincipal() != null) {
				if(userService.getCurrentUser() != null){

//					userEmail = userService.getCurrentUser().getEmail();
//					UserSession userSession = UserSessionCacheDAO.load(userEmail);

						if(type != null && !type.isEmpty()){
							if("getEvents".equals(type)) {
//								UserService userService = UserServiceFactory.getUserService();
//								if(userService.getCurrentUser() != null){
//								String userEmail = userService.getCurrentUser().getEmail();
									
								toReturn = DataManager.getAllEvents();
								resp.setContentType("application/json");
								PrintWriter out = resp.getWriter();
								out.print(toReturn);
								out.flush();
								
//								}
							} else if ("getAllUsers".equals(type)) {
								toReturn = DataManager.getAllUsers();
								resp.setContentType("application/json");
								PrintWriter out = resp.getWriter();
								out.print(toReturn);
								out.flush();
							} else if ("checkUser".equals(type)) {
								String user = req.getParameter("user");
								toReturn = DataManager.checkUser(user);
								resp.setContentType("application/json");
								PrintWriter out = resp.getWriter();
								out.print(toReturn);
								out.flush();
							} else if ("addEvent".equals(type)) {
								String eventToCreate = req.getParameter("eventToCreate");
								toReturn = DataManager.addEvent(eventToCreate);
								resp.setContentType("application/json");
								PrintWriter out = resp.getWriter();
								out.print(toReturn);
								out.flush();
							} else if ("checkEventName".equals(type)) {
								String eventName = req.getParameter("eventName");
								toReturn = DataManager.checkEventName(eventName);
								resp.setContentType("application/json");
								PrintWriter out = resp.getWriter();
								out.print(toReturn);
								out.flush();
							} else if ("checkEventMail".equals(type)) {
								String eventMailPrefix = req.getParameter("eventMailPrefix");
								toReturn = DataManager.checkEventMailPrefix(eventMailPrefix);
								resp.setContentType("application/json");
								PrintWriter out = resp.getWriter();
								out.print(toReturn);
								out.flush();
									
							} else if ("getUserAndEvent".equals(type)) {
									String groupId = req.getParameter("groupId");
									toReturn = DataManager.getUsersForEventGroup(groupId);
									resp.setContentType("application/json");
									PrintWriter out = resp.getWriter();
									out.print(toReturn);
									out.flush();
							} else if ("updEvent".equals(type)) {
								String eventToUpdate = req.getParameter("eventToUpdate");
								toReturn = DataManager.updEvent(eventToUpdate);
								resp.setContentType("application/json");
								PrintWriter out = resp.getWriter();
								out.print(toReturn);
								out.flush();
							} else if ("getAllSites".equals(type)) {
								toReturn = DataManager.getAllSites();
								resp.setContentType("application/json");
								PrintWriter out = resp.getWriter();
								out.print(toReturn);
								out.flush();
							} else if ("createNewSite".equals(type)) {
							    	String newSiteToCreate = req.getParameter("newSite");
							    	System.out.println("new site : " + newSiteToCreate);
								toReturn = DataManager.createNewSite(newSiteToCreate);
								resp.setContentType("application/json");
								PrintWriter out = resp.getWriter();
								out.print(toReturn);
								out.flush();
							}  
						}
						
					}
				}
			}
		
	}
}
