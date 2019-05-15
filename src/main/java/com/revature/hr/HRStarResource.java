package com.revature.hr;

import org.glassfish.jersey.server.ResourceConfig;

public class HRStarResource extends ResourceConfig {
	public HRStarResource() {
		packages("com.revature.hr");
	}
}
