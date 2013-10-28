package pl.cyfronet.datanet.web.server.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.pegdown.PegDownProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.servlet.support.RequestContextUtils;

@Controller
public class MarkdownController {
	@Autowired private PegDownProcessor processor;
	
	/**
	 * Serves markdown files from <code>src/main/webapp/{markdownResource}/{markdownResource}_{locale}.md</code>.
	 * E.g. request with the URL <code>/docs/manual</code> will process the <code>/src/main/webapp/manual/manual_en.md</code> file. The locale part
	 * will be set according to the session settings.
	 */
	@RequestMapping("/documentation/{markdownResource}")
	public String processMarkdown(@PathVariable String markdownResource,
			HttpServletRequest request, HttpServletResponse response, Model model) {
		Locale locale = RequestContextUtils.getLocale(request);
		Resource resource = new ServletContextResource(request.getServletContext(), markdownResource + "/" + markdownResource + "_" + locale.getLanguage() + ".md");
		
		if (resource.exists()) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			try {
				IOUtils.copy(resource.getInputStream(), out);
			} catch (IOException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
			
			String html = processor.markdownToHtml(new String(out.toByteArray()));
			model.addAttribute("mdContents", html);
			model.addAttribute("resourceKey", markdownResource);
		}
		
		return "mdWrapper";
	}
}