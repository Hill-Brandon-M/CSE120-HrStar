package com.revature.hr;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import java.sql.Timestamp;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Path("/hr")
public class HRStarService extends javax.ws.rs.core.Application {
	
	public static final String DEFAULT_URL = "jdbc:postgresql://localhost:5432/HRStar";
	public static final String DEFAULT_USER = "postgres";
	public static final String DEFAULT_PASS = "password";
	
	public static HRStar app = new HRStar(DEFAULT_URL, DEFAULT_USER, DEFAULT_PASS);
	
	@Context
    private HttpServletRequest req;
    @Context
    private HttpServletResponse res;
	
	@POST
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	public Token login (@FormParam("username") String username, @FormParam("password") String password) {
		
		Token t = app.authenticate(username, password);
		
		String t_value;
		if (t == null) {
			t_value = "";
			throw new ClientErrorException(406);
		} else {
			t_value = t.getValue();
			res.addCookie(new Cookie("access_token", t_value));
		}		
		
		return t;
	}
	
	@DELETE
	@Path("/logout")
	public void logout () {
		
		Cookie[] cookies = req.getCookies();
		
		if (cookies == null)
			return;
		
		for (Cookie c : cookies) {
			
			if(c.getName() == "access_token") {
				app.invalidateToken(c.getValue(), Token.Type.AUTHENTICATION);
			}
			
			c.setMaxAge(0);
			c.setValue(null);
			res.addCookie(c);
		}
	}
	
	@GET
	@Path("/about/{token}")
	@Produces(MediaType.APPLICATION_JSON)
	public Person getPersonalData (@PathParam("token") String t_value) {
		
		Person p = app.getPersonalData(t_value);
		
		if (p == null)
			throw new ClientErrorException(404);
		
		return p;
	}
	
	@POST
	@Path("/punch/{type}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void punch(
			@CookieParam("access_token") String t_value, 
			@PathParam("type") String typeString, 
			@FormParam("time") long time
	) {
		
		Timestamp punchTime = new Timestamp(time);
		Timestamp submitTime = new Timestamp(System.currentTimeMillis());
		
		Integer success = app.punch(t_value, typeString.toUpperCase(), punchTime, submitTime);
		
		if (success != 0) {
			int sc;
			String msg;
			switch (success) {
				
				case 2:
					sc = 422;
					msg = "Invalid arguments.";
					break;
				case 3:
					sc = 404;
					msg = "";
					break;
				case 4:
					sc = 409;
					msg = "Cannot make two consecutive punches of the same type.";
					break;
				case 5:
					sc = 401;
					msg = "Illegal Access: Only an authenticated user may access this service";
					break;
					
				case 1:
				default:
					sc = 500;
					msg = "Data Access Operation failed.";
					throw new ServerErrorException(msg, sc);
			
			}			
			throw new ClientErrorException(msg, sc);	
		}		
	}
	
	@POST
	@Path("/register/{reg_token}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Token register (
			@PathParam("reg_token") String reg_token, 
			@FormParam("username") String username, 
			@FormParam("password") String password,
			@FormParam("firstname") String firstname,
			@FormParam("lastname") String lastname
			) {
		
		if (reg_token == null || username == null || password == null || firstname == null || lastname == null){
			throw new ClientErrorException(422);
		}
			
		
		Token t = app.createUser(username, password, reg_token, firstname, lastname);
		
		String t_val = null;
		
		if (t != null)
			t_val = t.getValue();
		
		Cookie c = new Cookie("access_token", t_val);
		
		c.setPath("/");
		c.setDomain("");
		c.setMaxAge(60 * 15);
		
		res.addCookie(c);
		
		return t;
	}
	
	@GET
	@Path("/invite")
	@Produces(MediaType.APPLICATION_JSON)
	public Token createRegistrationToken (@CookieParam("access_token") String t_value) {
		return app.createRegistrationToken(t_value);
	}
	
	@GET
	@Path("/subordinates/get")
	@Produces(MediaType.APPLICATION_JSON)
	public List<User> getSubordinates (@CookieParam("access_token") String t_value) {
		
		//TODO: subordinate data fetch
		throw new ServerErrorException("Resource has not been implemented...", 501);
	}
	
	@POST
	@Path("/invoices")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Invoice[] getTimesheets (
			@FormParam("api_key") String key, 
			@FormParam("start") long start, 
			@FormParam("end") long end
			) {
		
		if (key == null)
			throw new ClientErrorException(401);
		
		Token api_key = app.getToken(key, Token.Type.API_KEY);
		
		if(api_key == null)
			throw new ClientErrorException("No valid key detected.", 401);
		
		return app.getInvoices(start, end);
	}
}
