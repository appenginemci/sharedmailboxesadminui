package com.mcigroup.eventmanager.front.model;

public enum UserRoleEnum {
	TEAMMEMBER("teamMember"),
	EVENTHEAD("eventHead"),
	POOLHEAD("poolHead");
	
	private final String userRole;
	private UserRoleEnum(String userRole) {
		this.userRole = userRole;
	}
	public String getUserRole() {
		return userRole;
	}
}
