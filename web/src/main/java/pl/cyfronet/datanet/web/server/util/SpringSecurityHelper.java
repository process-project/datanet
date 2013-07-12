package pl.cyfronet.datanet.web.server.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SpringSecurityHelper {
	public static String getUserLogin() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if(authentication != null) {
			return authentication.getName();
		}
		
		return null;
	}
}