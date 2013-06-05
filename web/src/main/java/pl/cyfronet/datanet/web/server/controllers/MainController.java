package pl.cyfronet.datanet.web.server.controllers;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.RequestContextUtils;

@Controller
public class MainController {
	private static final Logger log = LoggerFactory.getLogger(MainController.class);
	
	@RequestMapping("/")
	public String main(Model model, HttpServletRequest request) throws IOException {	
		Locale locale = RequestContextUtils.getLocale(request);
		model.addAttribute("locale", locale.getLanguage());
		
		return "main";
	}
	
	@RequestMapping("/help")
	public String help() throws IOException {
		return "help";
	}
}