

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import com.revature.hr.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Path("hr")
public class HRStarService extends javax.ws.rs.core.Application {
	
	public static HRStar app = new HRStar("jdbc:postgresql://localhost:5432/HRStar", "postgres", "password");
	
	@Context
    private HttpServletRequest req;
    @Context
    private HttpServletResponse res;
	
	@POST
	@Path("login")
	@Produces(MediaType.APPLICATION_JSON)
	public Token login (@FormParam("username") String username, @FormParam("password") String password) {
		System.out.println("API was activate with parameters (" + username + "," + password + ")");
		
		return app.authenticate(username, password);
	}
	
}
