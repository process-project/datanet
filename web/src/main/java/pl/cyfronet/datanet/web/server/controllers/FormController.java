package pl.cyfronet.datanet.web.server.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

import pl.cyfronet.datanet.web.server.controllers.beans.EntityUpload;
import pl.cyfronet.datanet.web.server.db.HibernateRepositoryDao;
import pl.cyfronet.datanet.web.server.db.beans.RepositoryDbEntity;
import pl.cyfronet.datanet.web.server.services.repositoryclient.RepositoryClient;
import pl.cyfronet.datanet.web.server.services.repositoryclient.RepositoryClientFactory;

@Controller
public class FormController {
	private static final Logger log = LoggerFactory.getLogger(FormController.class);
	
	@Autowired private RepositoryClient repositoryClient;
	@Autowired private HibernateRepositoryDao repositoryDao;
	@Autowired private RepositoryClientFactory repositoryClientFactory;

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public void handleForm(@RequestParam(value = "login", required = false) String login, @RequestParam(value = "password", required = false) String password,
			@ModelAttribute("entityUpload") EntityUpload entityUpload, HttpServletResponse response) {
		log.debug("Uploading entity {}", entityUpload);
		Map<String, String> fieldValues = new HashMap<>();
		
		for(String fieldName : entityUpload.getFields().keySet()) {
			fieldValues.put(fieldName, entityUpload.getFields().get(fieldName));
		}

		RepositoryDbEntity repositoryDbEntity = repositoryDao.getRepository(entityUpload.getRepositoryId());
		
		try {
			if (areFilesPresent(entityUpload.getFiles())) {
				Map<String, MultipartFile> files = new HashMap<>();
				
				//TODO(DH): convert the byte array to stream or request the repository directly from the client
				for (String fieldName : entityUpload.getFiles().keySet()) {
					if (!entityUpload.getFiles().get(fieldName).isEmpty()) {
						files.put(fieldName, entityUpload.getFiles().get(fieldName));
					}
				}
				
				RepositoryClient repositoryClient = repositoryClientFactory.create(login, password);
				repositoryClient.updateEntityRow(repositoryDbEntity.getUrl(), entityUpload.getEntityName(), null, fieldValues, files);
			} else {
				repositoryClient.updateEntityRow(repositoryDbEntity.getUrl(), entityUpload.getEntityName(), null, fieldValues, null);
			}
			response.setStatus(HttpServletResponse.SC_OK);
			
			try {
				response.getWriter().write("SUCCESS");
			} catch (IOException e) {
				log.error("Could not write to response after entity upload", e);
			}
		} catch (RestClientException | URISyntaxException | IOException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private boolean areFilesPresent(Map<String, MultipartFile> files) {
		for(String fieldName : files.keySet()) {
			if(!files.get(fieldName).isEmpty()) {
				return true;
			}
		}
		
		return false;
	}
}