package pl.cyfronet.datanet.web.server.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.pegdown.PegDownProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.RequestContextUtils;

@Controller
public class MarkdownController {
	private static final String BASE_PATH = "markdown";
	
	@Autowired private PegDownProcessor processor;
	
	@RequestMapping("/docs/{markdownResource}")
	public void processMarkdown(@PathVariable String markdownResource,
			HttpServletRequest request, HttpServletResponse response) {
		Locale locale = RequestContextUtils.getLocale(request);
		ClassPathResource resource = new ClassPathResource(BASE_PATH + "/" + markdownResource + "_" + locale.getLanguage() + ".md");
		
		if (!resource.exists()) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		} else {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			try {
				IOUtils.copy(resource.getInputStream(), out);
			} catch (IOException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
			
			String html = processor.markdownToHtml(new String(out.toByteArray()));
			
			try {
				response.getWriter().write(html);
			} catch (IOException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}
	}
}