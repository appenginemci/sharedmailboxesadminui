package com.mcigroup.eventmanager.front.model;

import com.google.gson.annotations.Expose;

public class UserCreation {
	@Expose
	private String mail;
	@Expose
	private String role;

	private String name;
	private String inProgressFolderId;
	private String forApprovalId;
	private int dbId;
	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInProgressFolderId() {
		return inProgressFolderId;
	}

	public void setInProgressFolderId(String inProgressFolderId) {
		this.inProgressFolderId = inProgressFolderId;
	}

	public String getForApprovalId() {
		return forApprovalId;
	}

	public void setForApprovalId(String forApprovalId) {
		this.forApprovalId = forApprovalId;
	}

	public int getDbId() {
		return dbId;
	}

	public void setDbId(int dbId) {
		this.dbId = dbId;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof UserCreation) {
			if (((UserCreation) obj).getRole() == null || ((UserCreation) obj).getMail() == null) {
				return false;
			}
			return (this.role.equals(((UserCreation) obj).getRole()) && this.mail.equalsIgnoreCase(((UserCreation) obj).getMail()));
		} else {
			return false;
		}
	}

}
