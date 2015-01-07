package com.mcigroup.eventmanager.front.model;

import java.util.Collection;

import com.google.gson.annotations.Expose;


public class EventCreation {
	@Expose
	private Site site;
	@Expose
	private String name;
	@Expose
	private String type;
	@Expose
	private String mail;
	@Expose
	private Collection<UserCreation> users;
	private String eventFolderId;
	private String newFolderId;
	private String closedFolderId;
	private String attachmentsFolderId;
	private String groupId;
	private int dbId;
	
	
	public Site getSite() {
	    return site;
	}
	public void setSite(Site site) {
	    this.site = site;
	}

	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	public Collection<UserCreation> getUsers() {
		return users;
	}
	public void setUsers(Collection<UserCreation> users) {
		this.users = users;
	}
	public String getEventFolderId() {
		return eventFolderId;
	}
	public void setEventFolderId(String eventFolderId) {
		this.eventFolderId = eventFolderId;
	}
	public String getNewFolderId() {
		return newFolderId;
	}
	public void setNewFolderId(String newFolderId) {
		this.newFolderId = newFolderId;
	}
	public String getClosedFolderId() {
		return closedFolderId;
	}
	public void setClosedFolderId(String closedFolderId) {
		this.closedFolderId = closedFolderId;
	}
	public String getAttachmentsFolderId() {
		return attachmentsFolderId;
	}
	public void setAttachmentsFolderId(String attachmentsFolderId) {
		this.attachmentsFolderId = attachmentsFolderId;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public int getDbId() {
		return dbId;
	}
	public void setDbId(int dbId) {
		this.dbId = dbId;
	}
	
}
