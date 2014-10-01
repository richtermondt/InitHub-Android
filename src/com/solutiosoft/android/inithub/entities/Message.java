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
