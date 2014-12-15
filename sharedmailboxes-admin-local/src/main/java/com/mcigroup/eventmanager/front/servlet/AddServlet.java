package com.mcigroup.eventmanager.front.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.mcigroup.eventmanager.front.dao.cache.UserSessionCacheDAO;
import com.mcigroup.eventmanager.front.model.UserSession;
import com.mcigroup.eventmanager.front.security.AuthorizationService;


public class AddServlet extends HttpServlet {

	private static final long serialVersionUID = -5367018104871398635L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException{
        UserSession userSession = null;
		String userEmail = null;
		
		String authorizedPage	= "/addEvent";
		String unauthorizedPage = "/unauthorized";
		
		UserService userService = UserServiceFactory.getUserService();
        String thisURL = req.getRequestURI();		
       
        if (req.getUserPrincipal() != null) {
        	if (req.getUserPrincipal() != null) {
        		if(userService.getCurrentUser() != null){
        		    ServletContext sc = getServletContext();
        		    RequestDispatcher rd = null;
        			
        			userEmail = userService.getCurrentUser().getEmail();
        			userSession = AuthorizationService.getAuthorization(userEmail);
        			
        			if(userSession != null){
        				rd =  sc.getRequestDispatcher(authorizedPage);
        				UserSessionCacheDAO.save(userSession);
//        				req.getParameter("groupId");
//        				String event = DataManager.getUsersForEventGroup(req.getParameter("groupId"));
//        				req.setAttribute("event", event);
        				req.setAttribute("session", userSession);
        				rd.forward(req, resp);
        			}else{
        				rd =  sc.getRequestDispatcher(unauthorizedPage);
        				System.err.println("Redirect to unauthorized page");
        				rd.forward(req, resp);
        			}
        		}
        	}
        }else{
        	System.err.println("Redirect to login page");
        	resp.sendRedirect(userService.createLoginURL(thisURL));
        }
	}
	
}
