package com.revature.hr;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class HRStarDatabase {
	
	private String url;
	private String username;
	private String password;
	private Connection conn;
	
	private HashMap<String, PreparedStatement> queries = new HashMap<>(); 
	
	public HRStarDatabase (String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
	}
	
	public User getUser (String username, String password) {
		// User-Credential search
		
		try {
			PreparedStatement s = this.queries.get("GET_USER_TOKEN_BY_CREDENTIALS");
			
			s.setString(1, username);
			s.setString(2, password);
			
			ResultSet rs = s.executeQuery();
			
			ArrayList<User> results = HRStarDatabase.parseUsers(rs);
			
			if (results.isEmpty())
				return null;
			
			return results.get(0);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public User getUser (String t_value, Token.Type type) {
		// User-Token search
		
		try {
			PreparedStatement s = this.queries.get("GET_USER_TOKEN_BY_TOKEN_VALUE");
			
			s.setString(1, t_value);
			s.setString(2, type.toString());
			
			ResultSet rs = s.executeQuery();
			
			ArrayList<User> results = HRStarDatabase.parseUsers(rs);
			
			if (results.isEmpty())
				return null;
			
			return results.get(0);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public User getUser (Integer id) {
		// User id lookup
		
		try {
			PreparedStatement s = this.queries.get("GET_USER_TOKEN_BY_USER_ID");
			
			s.setInt(1, id);
			
			ResultSet rs = s.executeQuery();
			
			ArrayList<User> results = HRStarDatabase.parseUsers(rs);
			
			if (results.isEmpty())
				return null;
			
			return results.get(0);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public ArrayList<User> getSubordinates (Integer superId) {
		// Subordinate search
		
		try {
			PreparedStatement s = this.queries.get("GET_USER_BY_SUPERVISOR_ID");
			
			s.setInt(1, superId);
			
			ResultSet rs = s.executeQuery();
			
			ArrayList<User> results = HRStarDatabase.parseUsers(rs);
			
			return results;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return new ArrayList<>();
	}
	
	public Token getToken (String value, Token.Type type) {
		// Token search
		
		try {
			PreparedStatement s = this.queries.get("GET_USER_TOKEN_BY_TOKEN_VALUE");
			
			s.setString(1, value);
			s.setString(2, type.toString());
			
			ResultSet rs = s.executeQuery();
			
			ArrayList<Token> results = HRStarDatabase.parseTokens(rs);
			
			if (results.isEmpty())
				return null;
			
			Token t = results.get(0);
			
			// If token has already expired, return null
			if (t.getExpires().before(new Timestamp(System.currentTimeMillis())))
				return null;
			
			return t;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public Token getToken (String value) {
		return getToken(value, Token.Type.AUTHENTICATION);
	}
	
	public Token getToken (String username, String password) {
		// Token search by credentials
		
		try {
			PreparedStatement s = this.queries.get("GET_USER_TOKEN_BY_CREDENTIALS");
			
			s.setString(1, username);
			s.setString(2, password);
			
			ResultSet rs = s.executeQuery();
			
			ArrayList<Token> results = HRStarDatabase.parseTokens(rs);
			
			if (results.isEmpty())
				return null;
			
			Token t = results.get(0);
			
			return refreshToken(t);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public ArrayList<ClockPunch> getPunches (Integer u_id, Date since) {
		// Punch search by user id and date
		
		try {
			PreparedStatement s = this.queries.get("GET_PUNCHES_BY_USER_DATE");
			
			s.setInt(1, u_id);
			s.setTimestamp(2, new Timestamp(since.getTime()));
			
			ResultSet rs = s.executeQuery();
			
			ArrayList<ClockPunch> results = HRStarDatabase.parsePunches(rs);
			
			return results;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return new ArrayList<>();
	}
	
	public ArrayList<ClockPunch> getPunches (Integer u_id) {
		return this.getPunches(u_id, new Date(0));
	}
	
	public Person getPersonalData (Integer u_id) {
		// Personal data search
		
		if (u_id == null)
			return null;
		
		try {
			PreparedStatement s = this.queries.get("GET_PERSONAL_DATA_BY_USER_ID");
			
			s.setInt(1, u_id);
			
			ResultSet rs = s.executeQuery();
			
			ArrayList<Person> results = HRStarDatabase.parsePeople(rs);
			
			if (results.isEmpty())
				return null;
			
			return results.get(0);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public ArrayList<Invoice> getInvoices(Long start, Long end) {
		if (start == null || end == null || start > end)
			return new ArrayList<>();
		
		ArrayList<Invoice> output;
		
		try {
			PreparedStatement s = this.queries.get("GET_INVOICES");
			
			Timestamp start_time = new Timestamp(start);
			Timestamp end_time = new Timestamp(end);
			
			s.setTimestamp(1, start_time);
			s.setTimestamp(2, end_time);
			
			output = parseInvoices(s.executeQuery());
						
		} catch (SQLException e) {
			e.printStackTrace();
			output = new ArrayList<>();
		}
		
		return output;
	}
	
	public boolean storePunch (ClockPunch c) {
		// Punch storage
		
		if (c == null)
			return false;
		
		try {
			PreparedStatement s;
			
			if (c.getId() == null) {
				s = this.queries.get("CREATE_PUNCH");
				
				s.setInt(1, c.getU_id());
				s.setString(2, c.getType().toString());
				s.setTimestamp(3, c.getPunchTime());
				s.setTimestamp(4, c.getSubmitTime());
				s.setString(5, c.getStatus().toString());
				
			} else {
				s = this.queries.get("UPDATE_PUNCH");
				
				s.setTimestamp(1, c.getPunchTime());
				s.setString(2, c.getStatus().toString());
				s.setInt(3, c.getId());
			}
			
			return (s.executeUpdate() == 1);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public Integer storeUser (User u) {
		// User storage
		
		if (u == null)
			return 0; 
		
		try {
			
			PreparedStatement s = this.queries.get("STORE_USER");
			
			s.setString(1, u.getUsername());
			s.setString(2, u.getPassword());
			s.setInt(3, u.getSuper_id());
			
			ResultSet rs = s.executeQuery();
			rs.next();
			
			Integer id = rs.getInt("u_id");
			
			return id;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public Token createUser(String username, String password, String reg_token) {
		// User initialization
		
		Token t = this.getToken(reg_token, Token.Type.REGISTRATION);
		
		if (t ==  null || t.getExpires().before(new Timestamp(System.currentTimeMillis())))
			return null;
		
		User newUser = new User(username, password, t.getU_id());
		
		Integer id = this.storeUser(newUser);
		
		Token output = this.createToken(id, Token.Type.AUTHENTICATION);
		
		if(output != null)
			this.invalidateToken(reg_token, Token.Type.REGISTRATION);
		
		return output;
	}
	
	public Token createToken (Integer user_id, Token.Type type) {
		// Token initialization
		
		try {
			PreparedStatement s = this.queries.get("CREATE_TOKEN");
			
			s.setString(1, type.toString());
			s.setInt(2, user_id);
			
			ResultSet rs = s.executeQuery();
			
			ArrayList<Token> results = HRStarDatabase.parseTokens(rs);
			
			if (results.isEmpty())
				return null;
			
			return extendToken(results.get(0), 1000 * 60 * 15);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;		
	}
	
	public void createPersonalData (Integer u_id, String firstname, String lastname) {
		
		if (u_id == null || firstname == null || lastname == null)
			return;
		
		try {
			PreparedStatement s = this.queries.get("CREATE_PERSONAL_DATA");
			
			s.setInt(1, u_id);
			s.setString(2, firstname);
			s.setString(3, lastname);
			
			s.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void invalidateToken (String t_value, Token.Type type) {
		Token t = this.getToken(t_value, type);
		this.extendToken(t, 0);
	}
	
	public Token refreshToken (Token t) {
		
		if (t == null)
			return null;
		
		t.setValue(genUUID());
		
		Token output = updateToken(t);
		extendToken(output, 1000 * 60 * 5);
		return output;
	}
	
	public Token extendToken (Token t, long interval) {
		// Token time extension
		t.setExpires(new Timestamp(System.currentTimeMillis() + interval));
		return this.updateToken(t);
	}
	
	private Token updateToken (Token t) {
		// Token updating
		
		if (t == null || t.getId() ==  null)
			return null;
		
		try {
			
			PreparedStatement s = this.queries.get("UPDATE_TOKEN");
			
			s.setTimestamp(1, t.getExpires());
			s.setString(2, t.getValue());
			s.setInt(3, t.getId());
			
			if (s.executeUpdate() == 1)
				return t;
			
			return null;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static String genUUID () {
		UUID output = UUID.randomUUID();
		return output.toString();
	}
	
	private static ArrayList<User> parseUsers (ResultSet rs) {
		
		ArrayList<User> output = new ArrayList<>();
		
		try {
			
			if (rs == null || !rs.first()) {
				return new ArrayList<>();
			}
			
			do {
				
				output.add(
					new User(
						rs.getInt("u_id"),
						rs.getString("u_username"),
						rs.getString("u_password"),
						null,
						rs.getInt("u_super_u_id")
					)
				);
				
			} while (rs.next());
			
		} catch (SQLException e) {
			e.printStackTrace();
			output = new ArrayList<>();
		}		
		
		return output;
	}
	
	private static ArrayList<Token> parseTokens (ResultSet rs) {
		ArrayList<Token> output = new ArrayList<>();
		
		try {
			
			if (rs == null || !rs.first()) {
				return new ArrayList<>();
			}
			
			do {
				
				output.add(
					new Token (
						rs.getInt("t_id"),
						rs.getInt("t_u_id"), 
						Token.Type.valueOf(rs.getString("t_type")), 
						rs.getTimestamp("t_expires"), 
						rs.getString("t_value")
					)
				);
				
			} while (rs.next());
			
		} catch (SQLException e) {
			e.printStackTrace();
			output = new ArrayList<>();
		}		
		
		return output;
	}
	
	private static ArrayList<ClockPunch> parsePunches (ResultSet rs) {
		
		ArrayList<ClockPunch> output = new ArrayList<>();
		
		try {
			
			if (rs == null || !rs.first()) {
				return new ArrayList<>();
			}
			
			do {
				
				output.add(
					new ClockPunch (
						rs.getInt("punch_id"),
						rs.getInt("punch_u_id"),
						ClockPunch.Type.valueOf(rs.getString("punch_type")),
						rs.getTimestamp("punch_time"),
						rs.getTimestamp("punch_submitted"),
						ClockPunch.State.valueOf(rs.getString("punch_status"))
					)
				);
				
			} while (rs.next());
			
			return output;
			
		} catch (SQLException e) {
			e.printStackTrace();
			output = new ArrayList<>();
		} catch (IllegalArgumentException e) {
			System.err.println("Illegal argument detected: ");
			e.printStackTrace();
			output = new ArrayList<>();
		}
		
		return output;
	}
	
	private static ArrayList<Person> parsePeople (ResultSet rs) {
		ArrayList<Person> output = new ArrayList<>();
		
		try {
			
			if (rs == null || !rs.first()) {
				return new ArrayList<>();
			}
			
			do {
				
				output.add(
					new Person(
						rs.getInt("p_id"),
						rs.getString("p_firstname"),
						rs.getString("p_lastname")
					)
				);
				
			} while (rs.next());
			
			return output;
			
		} catch (SQLException e) {
			e.printStackTrace();
			output = new ArrayList<>();
		}		
		
		return output;
	}
	
	private static ArrayList<Invoice> parseInvoices (ResultSet rs) {
		ArrayList<Invoice> output = new ArrayList<>();
		
		try {
			
			if (rs == null || !rs.first()) {
				return new ArrayList<>();
			}
			
			do {
				
				output.add(
					new Invoice(
						rs.getInt("invoice_u_id"),
						rs.getTimestamp("invoice_total_time").getTime(),
						rs.getDouble("invoice_balance"),
						rs.getInt("invoice_emp_acct_id")
					)
				);
				
			} while (rs.next());
			
			return output;
			
		} catch (SQLException e) {
			e.printStackTrace();
			output = new ArrayList<>();
		}		
		
		return output;
	}
	
	public void open () {
		try {
			if (this.conn == null || this.conn.isClosed())
				DriverManager.registerDriver(new org.postgresql.Driver());
				this.conn = DriverManager.getConnection(url, username, password);
		
			// Initialize prepared statements
			this.queries.put("GET_USER_TOKEN_BY_CREDENTIALS", this.conn.prepareStatement(SQL_GET_USER_TOKEN_BY_CREDENTIALS, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE));
			this.queries.put("GET_USER_TOKEN_BY_TOKEN_VALUE", this.conn.prepareStatement(SQL_GET_USER_TOKEN_BY_TOKEN_VALUE, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE));
			this.queries.put("GET_USER_TOKEN_BY_USER_ID", this.conn.prepareStatement(SQL_GET_USER_TOKEN_BY_USER_ID, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE));
			this.queries.put("GET_USER_BY_SUPERVISOR_ID", this.conn.prepareStatement(SQL_GET_USER_BY_SUPERVISOR_ID, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE));
			
			this.queries.put("GET_PUNCHES_BY_USER_DATE", this.conn.prepareStatement(SQL_GET_PUNCHES_BY_USER_DATE, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE));
			this.queries.put("GET_PERSONAL_DATA_BY_USER_ID", this.conn.prepareStatement(SQL_GET_PERSONAL_DATA_BY_USER_ID, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE));
			
			this.queries.put("STORE_USER", this.conn.prepareStatement(SQL_STORE_USER, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE));
			
			this.queries.put("CREATE_TOKEN", this.conn.prepareStatement(SQL_CREATE_TOKEN, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE));
			this.queries.put("UPDATE_TOKEN", this.conn.prepareStatement(SQL_UPDATE_TOKEN, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE));
			
			this.queries.put("CREATE_PUNCH", this.conn.prepareStatement(SQL_CREATE_PUNCH, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE));
			this.queries.put("UPDATE_PUNCH", this.conn.prepareStatement(SQL_UPDATE_PUNCH, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE));
			
			this.queries.put("CREATE_PERSONAL_DATA", this.conn.prepareStatement(SQL_CREATE_PERSONAL_DATA, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE));
			
			this.queries.put("GET_SHIFTS_BY_USER_ID", this.conn.prepareStatement(SQL_GET_SHIFTS_BY_USER_ID, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE));
			this.queries.put("GET_ALL_SHIFTS_BY_DATE", this.conn.prepareStatement(SQL_GET_ALL_SHIFTS_BY_DATE, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE));
			this.queries.put("GET_INVOICES", this.conn.prepareStatement(SQL_GET_INVOICES, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE));
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void close () {
		try {
			this.conn.close();
			this.queries.clear();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// SQL
	private static String SQL_GET_USER_TOKEN_BY_CREDENTIALS = "SELECT * FROM users JOIN tokens ON u_id = t_u_id WHERE u_username = ? AND u_password = ? AND t_type = ?";
	private static String SQL_GET_USER_TOKEN_BY_TOKEN_VALUE = "SELECT * FROM users JOIN tokens ON u_id = t_u_id WHERE t_value = ? AND CURRENT_TIMESTAMP < t_expires AND t_type = ?";
	private static String SQL_GET_USER_TOKEN_BY_USER_ID = "SELECT * FROM users JOIN tokens ON u_id = t_u_id WHERE u_id = ?";
	private static String SQL_GET_USER_BY_SUPERVISOR_ID = "SELECT * FROM users WHERE u_super_u_id = ?";
	
	private static String SQL_GET_PUNCHES_BY_USER_DATE = "SELECT * FROM punches WHERE punch_u_id = ? AND punch_time > ? ORDER BY punch_time DESC";	
	private static String SQL_GET_PERSONAL_DATA_BY_USER_ID = "SELECT * FROM people WHERE p_u_id = ?";
	
	private static String SQL_STORE_USER = 
				"INSERT INTO users (u_username, u_password, u_super_u_id) "
			+ 	"VALUES (?,?,?) "
			+ 	"ON CONFLICT (u_username) DO UPDATE SET "
			+ 	"u_password = EXCLUDED.u_password, "
			+ 	"u_super_u_id = EXCLUDED.u_super_u_id "
			+ 	"RETURNING u_id";
	
	private static String SQL_CREATE_TOKEN = "INSERT INTO tokens (t_type, t_u_id) VALUES (?,?) RETURNING *";
	private static String SQL_UPDATE_TOKEN = "UPDATE tokens SET t_expires = ?, t_value = ? WHERE t_id = ?";
  
	private static String SQL_CREATE_PUNCH = "INSERT INTO punches (punch_u_id, punch_type, punch_time, punch_submitted, punch_status) VALUES (?,?,?,?,?)";
	private static String SQL_UPDATE_PUNCH = "UPDATE punches SET punch_time = ?, punch_status = ? WHERE punch_id = ?";
	
	private static String SQL_CREATE_PERSONAL_DATA = "INSERT INTO people (p_u_id, p_firstname, p_lastname) VALUES (?,?,?) ON CONFLICT (p_u_id) DO NOTHING";
	
	private static String SQL_GET_SHIFTS_BY_USER_ID = "SELECT * FROM shifts WHERE shifts_u_id = ?";
	
	private static String SQL_GET_ALL_SHIFTS_BY_DATE = "SELECT * FROM shifts WHERE shift_end > ? AND shift_end < ?";
	
	private static String SQL_GET_SHIFT_SUM = 
			"SELECT shift_u_id as invoice_u_id, SUM(shift_time) AS invoice_total_time "
		+ 	"FROM(" + SQL_GET_ALL_SHIFTS_BY_DATE + ") AS invoices "
		+ 	"GROUP BY shift_u_id";
	
	private static String SQL_GET_INVOICES = 
			"SELECT invoice_u_id, "
		+ 	"invoice_total_time, "
		+ 	"EXTRACT(HOUR FROM invoice_total_time) * emp_payrate AS invoice_balance, "
		+ 	"emp_acct_id AS invoice_emp_acct_id "
		+ 	"FROM (" + SQL_GET_SHIFT_SUM + ") AS shift_sum "
		+ 	"JOIN employees ON emp_u_id = invoice_u_id";
}
