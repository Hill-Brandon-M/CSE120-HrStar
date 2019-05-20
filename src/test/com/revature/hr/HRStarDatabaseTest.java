package com.revature.hr;

import static org.junit.Assert.*;

import java.sql.Date;
import java.sql.DriverManager;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.revature.hr.ClockPunch;
import com.revature.hr.HRStarDatabase;
import com.revature.hr.Person;
import com.revature.hr.Token;
import com.revature.hr.User;
import com.revature.hr.ClockPunch.State;
import com.revature.hr.ClockPunch.Type;


public class HRStarDatabaseTest {
	
	private static HRStarDatabase db;
	
	private static String url = "jdbc:postgresql://localhost:5432/HRStarTest";
	private static String username = "postgres";
	private static String password = "password";

	@BeforeClass
	public static void setUpBeforeClass () throws Exception {
		
		db = new HRStarDatabase(url, username, password);
		db.open();
	}

	@AfterClass
	public static void tearDownAfterClass () throws Exception {
		
		db.close();
		
	}

	@Before
	public void setUp () throws Exception {}

	@After
	public void tearDown () throws Exception {}

	@Test
	public void testGetUserStringString () {
		
		assertNotNull(db.getUser("admin", "password"));
		assertNotNull(db.getUser("extra", "extra"));
		
	}

	@Test
	public void testGetUserToken () {

		assertNotNull(db.getUser("e946f790253d61c01d8f908e6b6386c3a5988e8e49afea0c4fffed6ec1aae5ec0e43c90d612019f8c4184d4a2b1b8b5b661d1d2d86583341f7904d3afaccf338", Token.Type.AUTHENTICATION));
		assertNotNull(db.getUser("aca5abbf4f339c9ae1a5fe688a9b4af83a780da44c383674d251197c2624df19e22eba73c65584a7fef93801f3f96ca40f1f99547aac59f7dbd0476ef37884e6", Token.Type.AUTHENTICATION));
	}

	@Test
	public void testGetUserInteger () {

		assertNotNull(db.getUser(1));
		assertNotNull(db.getUser(3));
	}

	@Test
	public void testGetSubordinates () {

		ArrayList<User> s = db.getSubordinates(1);
		assertFalse(s.isEmpty());
	}

	@Test
	public void testGetTokenString () {
		Token t = db.getToken("aca5abbf4f339c9ae1a5fe688a9b4af83a780da44c383674d251197c2624df19e22eba73c65584a7fef93801f3f96ca40f1f99547aac59f7dbd0476ef37884e6");
		assertNotNull(t);
		assertTrue(t.getValue().equals("aca5abbf4f339c9ae1a5fe688a9b4af83a780da44c383674d251197c2624df19e22eba73c65584a7fef93801f3f96ca40f1f99547aac59f7dbd0476ef37884e6"));
		
	}

	@Test
	public void testGetTokenStringString () {

		Token t = db.getToken("admin", "password");
		assertNotNull(t);
		assertNotNull(db.getUser(t.getU_id()));
	}

	@Test
	public void testGetPunchesUserDate () {

		ArrayList<ClockPunch> punches = db.getPunches(db.getUser(1).getId(), new Date(2019, 5, 14));
		assertTrue(punches.isEmpty());
		
		punches = db.getPunches(db.getUser(1).getId());
		assertFalse(punches.isEmpty());
	}

	@Test
	public void testGetPersonalData () {

		Person p = db.getPersonalData(1);
		
		assertTrue(p.getFirstname().equals("John"));
	}

	@Test
	public void testStorePunch () {
		
		ClockPunch c = new ClockPunch(1, ClockPunch.Type.IN, new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()), ClockPunch.State.PENDING);
		assertTrue(db.storePunch(c));
	}

	@Test
	public void testStoreUser () {

		User u = db.getUser(3);
		u.setPassword("supersecret");
		assertTrue(db.storeUser(u) > 0);
		
		u = db.getUser(3);
		assertTrue(u.getPassword().equals("supersecret"));
		
		u.setPassword("extra");
		db.storeUser(u);
	}

	@Test
	public void testCreateUser () {

		String user = "Noo";
		String pass = "Bie";
		String reg_token = "a886471d74043f35afbacc2f023cdc8721125714d1a4073a2f9e8a81b4e736aecbe1648f8a9324dea5e50ea0e7d26f03b4eb98ff36cac79353848f88cebd3a87";
		
		Token t = db.createUser(user, pass, reg_token);
		assertNotNull(t);
		
		User u = db.getUser(t.getValue(), Token.Type.AUTHENTICATION);
		assertNotNull(u);
		assertTrue((u.getUsername().equals(user)) && (u.getPassword().equals(pass)) && (u.getSuper_id() == 1));
	}

	@Test
	public void testCreateToken () {

		Token t = db.createToken(3, Token.Type.REGISTRATION);
		
		assertNotNull(t);
		Token u = db.createUser("A", "B", t.getValue());
		
		assertNotNull(db.getUser(u.getValue(), Token.Type.AUTHENTICATION));
		
	}

	@Test
	public void testInvalidateToken () {

		Token t = db.getToken("extra", "extra");
		db.invalidateToken(t.getValue(), Token.Type.AUTHENTICATION);
		t = db.getToken("extra", "extra");
		
		assertNotNull(t);
		
		assertTrue(t.getExpires().before(new Timestamp(System.currentTimeMillis())));
		
	}

	@Test
	public void testExtendToken () {

		Token t = db.getToken("somebody", "secret");
		
		db.extendToken(t, 1000 * 60 * 15);
		
		t = db.getToken("somebody", "secret");
		
		assertNotNull(t);
		assertTrue(t.getExpires().after(new Timestamp(System.currentTimeMillis())));
	}

}
