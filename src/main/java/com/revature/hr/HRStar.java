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
	
	public void close () {
		this.db.close();
	}
	
	public Token authenticate (String username, String password) {		
		//Find User on database by credentials		
		//Return the user's access token		
		return this.db.getToken(username, password);
	}
	
	public void invalidateToken (Token t) {
		this.db.invalidateToken(t);
	}
	
	public Token getToken (String value) {
		//Find Token on database by value
		//Return token object
		return this.db.getToken(value);
	}
	
	public User getUser (String t_value) {
		//Find User on database by access token
		//return User object
		
		return this.db.getUser(t_value);
	}
	
	public ArrayList<User> getSubordinates (Integer u_id) {
		return this.db.getSubordinates(u_id);		
	}
	
	public Integer punch (String t_value, String typeString, Timestamp made, Timestamp submitted) {
		//Find User by Token
		User u = this.db.getUser(t_value);
		
		if (u == null)
			return -1;
		
		ClockPunch.Type type;		
		try {
			type = ClockPunch.Type.valueOf(typeString);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return -2;
		}
		
		if (made == null || submitted == null || made.after(submitted))
			return -3;
				
		//Construct ClockPunch object
		//Store ClockPunch to database
		if (this.db.storePunch(new ClockPunch(u.getId(), type, made, submitted, ClockPunch.State.PENDING)))
			return 0;
		
		return 1;
	}
	
	public ArrayList<ClockPunch> getPunches (String t_value, Date since) {
		//Find User by Token
		User u = this.db.getUser(t_value);
		
		//Search for punches on database by that user after the since-date
		//return results
		return this.db.getPunches(u, since);
	}
	
	public ArrayList<ClockPunch> getAllPunches (User u) {
		return this.db.getPunches(u);
	}
	
	public Token createUser (String username, String password, String reg_token) {
		return this.db.createUser(username, password, reg_token);
	}
	
	public Token createRegistrationToken (String t_value) {
		User user = this.getUser(t_value);
		
		if(user == null)
			return null;
		
		return this.db.createToken(user.getId(), Token.Type.REGISTRATION);
	}
}
