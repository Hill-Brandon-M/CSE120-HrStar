package com.revature.hr;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;

public class HRStar {
	
	private HRStarDatabase db;
	
	public HRStar (String url, String username, String password) {
		this.db = new HRStarDatabase(url, username, password);
		this.db.open();
	}
	
	public Token authenticate (String username, String password) {
		//TODO: Implement authentication
		
		//Find User on database by credentials
		//Return the user's access token
		
		return null;
	}
	
	public Token getToken (String value) {
		//TODO: implement token search
		
		//Find Token on database by value
		//Return token object
		return null;
	}
	
	public User getUser (Token t) {
		//TODO: Implement token exchange
		
		//Find User on database by access token
		//return User object
		
		return null;
	}
	
	public Integer punch (Token t, String type, Timestamp made, Timestamp submitted) {
		//TODO: Implement clock punching
		
		//Find User by Token
		//Check for bad input
		//Construct ClockPunch object
		//Store ClockPunch to database
		
		return null;
	}
	
	public ArrayList<ClockPunch> getPunches (Token t, Date since) {
		//TODO: implement punch viewing
		
		//Find User by Token
		//Search for punches on database by that user after the since-date
		//Return results
		
		return null;
	}
}
