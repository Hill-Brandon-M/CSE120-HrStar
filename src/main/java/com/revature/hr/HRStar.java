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
		return this.db.refreshToken(this.db.getToken(username, password));
	}
	
	public void invalidateToken (String t_value, Token.Type type) {
		this.db.invalidateToken(t_value, type);
	}
	
	public Token getToken (String value) {
		//Find Token on database by value
		//Return token object
		return this.db.getToken(value);
	}
	
	public User getUser (String t_value, Token.Type type) {
		//Find User on database by access token
		//return User object
		
		return this.db.getUser(t_value, type);
	}
	
	public ArrayList<User> getSubordinates (Integer u_id) {
		return this.db.getSubordinates(u_id);		
	}
	
	public Integer punch (String t_value, String typeString, Timestamp made, Timestamp submitted) {
		//Find User by Token
		Token t = this.db.getToken(t_value, Token.Type.AUTHENTICATION);
		
		if (t == null)
			return 5;
		
		ClockPunch.Type type;		
		try {
			type = ClockPunch.Type.valueOf(typeString);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return 3;
		}
		
		if (made == null || submitted == null || t.getExpires().before(new Timestamp(System.currentTimeMillis())))
			return 2;
		
		ArrayList<ClockPunch> previousPunches = this.db.getPunches(t.getU_id());
		
		if (!previousPunches.isEmpty() && previousPunches.get(0).getType() == type) 
			return 4;
				
		//Construct ClockPunch object
		//Store ClockPunch to database
		if (this.db.storePunch(new ClockPunch(t.getU_id(), type, made, submitted, ClockPunch.State.PENDING)))
			return 0;
		
		return 1;
	}
	
	public ArrayList<ClockPunch> getPunches (String t_value, Date since) {
		//Find User by Token
		Token t = this.db.getToken(t_value, Token.Type.AUTHENTICATION);
		
		//Search for punches on database by that user after the since-date
		//return results
		return this.db.getPunches(t.getU_id(), since);
	}
	
	public ArrayList<ClockPunch> getAllPunches (Integer u_id) {
		return this.db.getPunches(u_id);
	}
	
	public Token createUser (String username, String password, String reg_token, String firstname, String lastname) {
		Token t = this.db.createUser(username, password, reg_token);
		if (t != null)
			this.db.createPersonalData(t.getU_id(), firstname, lastname);
		return t;
	}
	
	public Token createRegistrationToken (String t_value) {
		User user = this.db.getUser(t_value, Token.Type.AUTHENTICATION);
		
		if(user == null)
			return null;
		
		return this.db.createToken(user.getId(), Token.Type.REGISTRATION);
	}
	
	public Person getPersonalData (String t_value) {

		User u = this.getUser(t_value, Token.Type.AUTHENTICATION);
		
		if (u == null)
			return null;
		
		return this.db.getPersonalData(u.getId());
		
	}
}
