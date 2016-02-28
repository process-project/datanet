package pl.cyfronet.datanet.web.server.controllers;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.github.rjeschke.txtmark.Processor;

@Controller
public class MarkdownController {
	private static final Logger log = LoggerFactory.getLogger(MarkdownController.class);
	
	/**
	 * Serves markdown files from <code>src/main/webapp/{markdownResource}/{markdownResource}_{locale}.md</code>.
	 * E.g. request with the URL <code>/docs/manual</code> will process the <code>/src/main/webapp/manual/manual_en.md</code> file. The locale part
	 * will be set according to the session settings.
	 */
	@RequestMapping("/documentation/{markdownResource}")
	public String processMarkdown(@PathVariable String markdownResource,
			HttpServletRequest request, HttpServletResponse response, Model model) {
		Locale locale = RequestContextUtils.getLocale(request);
		String resourceLocation = markdownResource + "/" + markdownResource + "_" + locale.getLanguage() + ".md";
		Resource resource = new ServletContextResource(request.getServletContext(), resourceLocation);
		
		if (resource.exists()) {
			try {
				String html = Processor.process(resource.getInputStream());
				model.addAttribute("mdContents", html);
				model.addAttribute("resourceKey", markdownResource);
			} catch (IOException e) {
				log.error("Could not process markdown from " + resourceLocation, e);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		} else {
			try {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			} catch (IOException e) {
				log.error("Could not return HTTP error", e);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
			
			return null;
		}
		
		return "mdWrapper";
	}
}