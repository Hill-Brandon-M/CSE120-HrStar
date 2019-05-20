package com.revature.hr;

import java.io.Serializable;
import java.sql.Timestamp;

public class Invoice implements Serializable{
	Integer user_id;
	Timestamp time;
	Double balance;
	Integer acct_id;
	
	public Invoice(Integer user_id, long time, double balance, int acct_id) {
		this.user_id = user_id;
		this.time = new Timestamp(time);
		this.balance = balance;
		this.acct_id = acct_id;
	}

	public Integer getUser_id() {
		return user_id;
	}

	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}

	public Timestamp getTime() {
		return time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}

	
	public Double getBalance () {
	
		return balance;
	}

	
	public void setBalance (Double balance) {
	
		this.balance = balance;
	}

	
	public Integer getAcct_id () {
	
		return acct_id;
	}

	
	public void setAcct_id (Integer acct_id) {
	
		this.acct_id = acct_id;
	}
	
	
}
