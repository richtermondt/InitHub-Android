/**
 *   This file is part of InitHub-Android.
 *
 *   InitHub-Android is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   InitHub-Android is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with InitHub-Android.  If not, see <http://www.gnu.org/licenses/>.
 */
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
