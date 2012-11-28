package pl.cyfronet.datanet.web.server.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PortalLoginHandler {
	private static final Logger log = LoggerFactory.getLogger(PortalLoginHandler.class);
	
	@Value("${portal.login.base.url}") private String loginBaseUrl;
	
	public void login(String login, String password) {
		log.debug("Login attempt with base URL {}", loginBaseUrl);
	}
}