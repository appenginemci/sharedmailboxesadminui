package com.mcigroup.eventmanager.front.model;

import java.util.Date;

import com.google.gson.annotations.Expose;


public class Site {
	@Expose
	private int ID;
	@Expose
	private String name;
	@Expose
	private Date creation_date;
	@Expose
	private String folder_id;


	public int getID() {
		return ID;
	}
	public void setID(int ID) {
		this.ID = ID;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Date getCreation_date() {
	    return creation_date;
	}
	public void setCreation_date(Date creation_date) {
	    this.creation_date = creation_date;
	}
	
	public String getFolder_id() {
	    return folder_id;
	}
	public void setFolder_id(String folder_id) {
	    this.folder_id = folder_id;
	}

}
