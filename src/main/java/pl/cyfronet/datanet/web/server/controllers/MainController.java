package pl.cyfronet.datanet.web.server.controllers;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.support.RequestContextUtils;

@Controller
public class MainController {
	private static final Logger log = LoggerFactory.getLogger(MainController.class);
	
	@RequestMapping("/")
	public String main(Model model, HttpServletRequest request) throws IOException {	
		Locale locale = RequestContextUtils.getLocale(request);
		model.addAttribute("locale", locale.getLanguage());
		log.debug("Setting locale to {}", locale.getLanguage());
		
		return "main";
	}
	
	@RequestMapping(value = "/downloadProxy", method = RequestMethod.POST)
	@Secured("ROLE_USER")
	public void fetchProxy(HttpServletResponse response) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if(authentication != null) {
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition","attachment; filename=user-proxy.pem");
			
			try {
				response.getWriter().write((String) authentication.getCredentials());
			} catch (IOException e) {
				log.error("Could not write user proxy in response", e);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}
	}
}