package com.revature.hr;

import java.sql.Timestamp;

public class Token {
	
	/**
	 * Describes the use case of a Token.
	 * 
	 * <ol>
	 * 	<li>AUTHENTICATION</li>
	 * 	<li>REGISTRATION</li>
	 * </ol>
	 *
	 */
	public static enum Type {
		AUTHENTICATION, REGISTRATION
	}
	
	private Integer id;
	private Type type;
	private Timestamp expires;
	private String value;
	
	public Token (Integer id, Type type, Timestamp expires, String value) {
		super();
		this.id = id;
		this.type = type;
		this.expires = expires;
		this.value = value;
	}

	
	public Timestamp getExpires () {
	
		return expires;
	}

	
	public void setExpires (Timestamp expires) {
	
		this.expires = expires;
	}

	
	public Integer getId () {
	
		return id;
	}

	
	public Type getType () {
	
		return type;
	}

	
	public String getValue () {
	
		return value;
	}
	
	
		
	
}
