package com.solutiosoft.android.inithub.entities;

public class Subject {
	
	private String shortDesc;
	private String longDesc;
	private String initiative;
	private String firstName;
	private String lastName;
	private int remoteId;
	
	public Subject(String shortDesc, String longDesc, String initiative,
			String firstName, String lastName, int remoteId) {
		super();
		this.shortDesc = shortDesc;
		this.longDesc = longDesc;
		this.initiative = initiative;
		this.firstName = firstName;
		this.lastName = lastName;
		this.remoteId = remoteId;
	}
	
	public Subject() {
		// TODO Auto-generated constructor stub
	}

	public String getShortDesc() {
		return shortDesc;
	}
	public void setShortDesc(String shortDesc) {
		this.shortDesc = shortDesc;
	}
	public String getLongDesc() {
		return longDesc;
	}
	public void setLongDesc(String longDesc) {
		this.longDesc = longDesc;
	}
	public String getInitiative() {
		return initiative;
	}
	public void setInitiative(String initiative) {
		this.initiative = initiative;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public int getRemoteId() {
		return remoteId;
	}
	public void setRemoteId(int remoteId) {
		this.remoteId = remoteId;
	}

}
