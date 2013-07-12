package pl.cyfronet.datanet.web.server.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import pl.cyfronet.datanet.web.client.errors.LoginException;
import pl.cyfronet.datanet.web.server.services.portallogin.PortalLoginHandler;

public class PortalAuthenticationManager implements AuthenticationManager {
	private final static String USER_ROLE = "ROLE_USER";
	
	@Autowired private PortalLoginHandler portalLoginHandler;
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		try {
			portalLoginHandler.login((String) authentication.getName(),
					(String) authentication.getCredentials());
			List<GrantedAuthority> authorities = new ArrayList<>();
			authorities.add(new SimpleGrantedAuthority(USER_ROLE));
			
			return new UsernamePasswordAuthenticationToken(authentication.getName(),
					"", authorities);
		} catch (LoginException e) {
			throw new AuthenticationException("Authentication failed", e) {
				private static final long serialVersionUID = -5080316863089277312L;
			};
		}
	}
}