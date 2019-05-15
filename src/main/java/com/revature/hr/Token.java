package com.revature.hr;

import java.io.Serializable;
import java.sql.Timestamp;


public class Token implements Serializable{
	
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
	private Integer u_id;
	private Type type;
	private Timestamp expires;
	private String value;
	
	public Token (Integer id, Integer u_id, Type type, Timestamp expires, String value) {
		this.id = id;
		this.u_id = u_id;
		this.type = type;
		this.expires = expires;
		this.value = value;
	}
	
	public Token (Integer u_id, Type type, Timestamp expires, String value) {
		this(null, u_id, type, expires, value);
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
	
	public Integer getU_id () {
	
		return u_id;
	}

	public Type getType () {
	
		return type;
	}

	
	public String getValue () {
	
		return value;
	}

	public void setValue (String value) {

		this.value = value;
		
	}
	
	
		
	
}
