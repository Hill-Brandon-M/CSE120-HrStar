package com.revature.hr;


public class Person {
	private Integer id;
	private String firstname;
	private String lastname;
	
	public Person (Integer id, String firstname, String lastname) {
		this.id = id;
		this.firstname = firstname;
		this.lastname = lastname;
	}

	public Person (String firstname, String lastname) {
		this(null, firstname, lastname);
	}

	
	public Integer getId () {
	
		return id;
	}

	
	public String getFirstname () {
	
		return firstname;
	}

	
	public String getLastname () {
	
		return lastname;
	}
	
	
}
