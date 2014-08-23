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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Message {

	private int remoteId;
	private int remoteSubjectId;
	private String comment;
	private String firstName;
	private String lastName;
	private String createDate;
	private long localSubjectId;
	public long getLocalSubjectId() {
		return localSubjectId;
	}
	public void setLocalSubjectId(long localSubjectId) {
		this.localSubjectId = localSubjectId;
	}
	public int getRemoteId() {
		return remoteId;
	}
	public void setRemoteId(int remote_id) {
		this.remoteId = remote_id;
	}
	public Message(int remoteId, int remoteSubjectId, String comment,
			String firstName, String lastName, String createDate) {
		super();
		this.remoteId = remoteId;
		this.remoteSubjectId = remoteSubjectId;
		this.comment = comment;
		this.firstName = firstName;
		this.lastName = lastName;
		this.createDate = createDate;
	}
	public Message(){
		
	}
	
	public Message(int subjectId, String comment, String createDate) {
		super();
		this.localSubjectId = subjectId;
		this.comment = comment;
		this.createDate = createDate;
	}
	public int getRemoteSubjectId() {
		return remoteSubjectId;
	}
	public void setRemoteSubjectId(int remote_subject_id) {
		this.remoteSubjectId = remote_subject_id;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
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
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
		
}
