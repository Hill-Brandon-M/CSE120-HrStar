package com.revature.hr;

import java.sql.Timestamp;

public class ClockPunch {
	public static enum Type {
		IN, OUT
	}
	
	public static enum State {
		PENDING, APPROVED, DENIED
	}
	
	private Integer id;
	private User maker;
	private Type type;
	private Timestamp punchTime;
	private Timestamp submitTime;
	private State status;
	
	public ClockPunch (Integer id, User maker, Type type, Timestamp punchTime, Timestamp submitTime, State status) {
		this.id = id;
		this.maker = maker;
		this.type = type;
		this.punchTime = punchTime;
		this.submitTime = submitTime;
		this.status = status;
	}

	public ClockPunch (User maker, Type type, Timestamp punchTime, Timestamp submitTime, State status) {
		this(null, maker, type, punchTime, submitTime, status);
	}

	
	public Integer getId () {
	
		return id;
	}

	
	public User getMaker () {
	
		return maker;
	}

	
	public Type getType () {
	
		return type;
	}

	
	public Timestamp getPunchTime () {
	
		return punchTime;
	}

	
	public Timestamp getSubmitTime () {
	
		return submitTime;
	}

	
	public State getStatus () {
	
		return status;
	}
	
	
}
