package com.revature.hr;

import java.util.ArrayList;

public class User {
	
	private Integer id;
	private String username;
	private String password;
	private Person personalData;
	private Integer super_id;
	
	
	public User (Integer id, String username, String password, Person personalData, Integer super_id) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.personalData = personalData;
		this.super_id = super_id;
	}


	public User (String username, String password, Integer super_id) {
		this(null, username, password, null, super_id);
	}

	public Integer getId () {
	
		return id;
	}


	
	public void setId (Integer id) {
	
		this.id = id;
	}


	
	public String getUsername () {
	
		return username;
	}


	
	public void setUsername (String username) {
	
		this.username = username;
	}


	
	public String getPassword () {
	
		return password;
	}


	
	public void setPassword (String password) {
	
		this.password = password;
	}
	
	public Person getPersonalData () {
	
		return personalData;
	}


	
	public void setPersonalData (Person personalData) {
	
		this.personalData = personalData;
	}


	
	public Integer getSuper_id () {
	
		return super_id;
	}

	
	public void setSuper_id (Integer super_id) {
	
		this.super_id = super_id;
	}
	
	
}
