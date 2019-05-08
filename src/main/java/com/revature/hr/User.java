package com.revature.hr;

import java.util.ArrayList;

public class User {
	
	private Integer id;
	private String username;
	private String password;
	private Token token;
	private ArrayList<User> subordinates;
	private Person personalData;
	
	
	public User (Integer id, String username, String password, Token token, ArrayList <User> subordinates,
				 Person personalData) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.token = token;
		this.subordinates = subordinates;
		this.personalData = personalData;
	}


	public User (String username, String password) {
		this(null, username, password, null, null, null);
	}
	
	public User (String username, String password, Token token) {
		this(null, username, password, token, null, null);
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


	
	public Token getToken () {
	
		return token;
	}


	
	public void setToken (Token token) {
	
		this.token = token;
	}


	
	public ArrayList <User> getSubordinates () {
	
		return subordinates;
	}


	
	public void setSubordinates (ArrayList <User> subordinates) {
	
		this.subordinates = subordinates;
	}


	
	public Person getPersonalData () {
	
		return personalData;
	}


	
	public void setPersonalData (Person personalData) {
	
		this.personalData = personalData;
	}
	
	
}
