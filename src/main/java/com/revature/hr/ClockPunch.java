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
	private Integer u_id;
	private Type type;
	private Timestamp punchTime;
	private Timestamp submitTime;
	private State status;
	
	public ClockPunch (Integer id, Integer u_id, Type type, Timestamp punchTime, Timestamp submitTime, State status) {
		this.id = id;
		this.u_id = u_id;
		this.type = type;
		this.punchTime = punchTime;
		this.submitTime = submitTime;
		this.status = status;
	}

	public ClockPunch (Integer u_id, Type type, Timestamp punchTime, Timestamp submitTime, State status) {
		this(null, u_id, type, punchTime, submitTime, status);
	}

	
	public Integer getId () {
	
		return id;
	}

	
	public Integer getU_id () {
		return u_id;
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
