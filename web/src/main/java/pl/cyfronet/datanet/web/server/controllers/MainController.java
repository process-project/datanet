package pl.cyfronet.datanet.web.server.controllers;

import java.io.IOException;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {
	private static final Logger log = LoggerFactory.getLogger(MainController.class);
	
	@RequestMapping("/")
	public void main(Writer writer) throws IOException {
		log.debug("Serving main page");
		writer.append("Hello, no XML!");
	}
}