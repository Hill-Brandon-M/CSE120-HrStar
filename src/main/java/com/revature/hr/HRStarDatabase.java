package com.revature.hr;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class HRStarDatabase {
	
	private String url;
	private String username;
	private String password;
	private Connection conn;
	
	public HRStarDatabase (String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
	}

	public HRStarDatabase (Connection conn) {
		this.conn = conn;
	}
	
	public User getUser (String username, String password) {
		//TODO: User-Credential search
		return null;
	}
	
	public User getUser (Token t) {
		//TODO: User-Token search
		return null;
	}
	
	public Token getToken (String value) {
		//TODO: Token search
		return null;
	}
	
	public ArrayList<ClockPunch> getPunches (User u, Date since) {
		//TODO: user-date-punch search
		return null;
	}
	
	public ArrayList<ClockPunch> getPunches (User u) {
		return this.getPunches(u, new Date(0));
	}
	
	public Integer storePunch (ClockPunch c) {
		//TODO: punch storage
		return null;
	}
	
	private ArrayList<User> parseUsers (ResultSet rs) {
		//TODO: User result-parsing
		return new ArrayList<User>();
	}
	
	private ArrayList<Token> parseTokens (ResultSet rs) {
		//TODO: Token result-parsing
		return null;
	}
	
	private ArrayList<ClockPunch> parsePunches (ResultSet rs) {
		//TODO: Punch result-parsing
		return null;
	}
	
	private ArrayList<Person> parsePeople (ResultSet rs) {
		//TODO: Person result-parsing
		return null;
	}
	
	public void open () {
		try {
			if (this.conn == null || this.conn.isClosed())
				this.conn = DriverManager.getConnection(url, username, password);
		
		//TODO: Initialize prepared statements
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void close () {
		try {
			this.conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//TODO: SQL
	//TODO: Prepared Statements
}
