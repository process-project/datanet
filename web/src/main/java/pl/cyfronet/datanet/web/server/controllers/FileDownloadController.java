package pl.cyfronet.datanet.web.server.controllers;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import pl.cyfronet.datanet.web.server.services.repositoryclient.RepositoryClient;
import pl.cyfronet.datanet.web.server.services.repositoryclient.RepositoryClientFactory;

@Controller
public class FileDownloadController {
	private static final Logger log = LoggerFactory.getLogger(FileDownloadController.class);
	
	@Autowired private RepositoryClientFactory repositoryClientFactory;
	
	@RequestMapping("/download")
	@Secured("ROLE_USER")
	public void download(@RequestParam String fileUrl, @RequestParam String fileName,
			HttpServletRequest request, HttpServletResponse response) {
		log.debug("Processing file download request for file URL {}", fileUrl);
		
		try {
			RepositoryClient repositoryClient = repositoryClientFactory.create(
					(String) SecurityContextHolder.getContext().getAuthentication().getCredentials());
			response.setContentType(request.getServletContext().getMimeType(fileName));
			response.setHeader("Content-Disposition","attachment; filename=" + fileName);
			repositoryClient.getFile(fileUrl, response.getOutputStream());
		} catch (KeyManagementException | NoSuchAlgorithmException | IOException e) {
			log.error("Could not fetch file {}", fileUrl);
			log.error("Exception for the above error was", e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
}