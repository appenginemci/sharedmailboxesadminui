package com.mcigroup.eventmanager.front.model;

import com.mcigroup.eventmanager.front.helper.Tools;

public class UserSession {

	private String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String toJson() {
		return Tools.gson.toJson(this);
	}
	
}
