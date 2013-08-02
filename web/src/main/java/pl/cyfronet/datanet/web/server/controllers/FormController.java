package pl.cyfronet.datanet.web.server.controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestClientException;

import pl.cyfronet.datanet.web.server.controllers.beans.EntityUpload;
import pl.cyfronet.datanet.web.server.db.HibernateRepositoryDao;
import pl.cyfronet.datanet.web.server.db.beans.RepositoryDbEntity;
import pl.cyfronet.datanet.web.server.services.repositoryclient.RepositoryClient;

@Controller
public class FormController {
	private static final Logger log = LoggerFactory.getLogger(FormController.class);
	
	@Autowired private RepositoryClient repositoryClient;
	@Autowired private HibernateRepositoryDao repositoryDao;

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public void handleForm(@ModelAttribute("entityUpload") EntityUpload entityUpload, HttpServletResponse response) {
		log.debug("Uploading entity {}", entityUpload);
		Map<String, String> fieldValues = new HashMap<>();
		
		for(String fieldName : entityUpload.getFields().keySet()) {
			fieldValues.put(fieldName, entityUpload.getFields().get(fieldName));
		}

		RepositoryDbEntity repositoryDbEntity = repositoryDao.getRepository(entityUpload.getRepositoryId());
		
		try {
			repositoryClient.updateEntityRow(repositoryDbEntity.getUrl(), entityUpload.getEntityName(), null, fieldValues);
			response.setStatus(HttpServletResponse.SC_OK);
			
			try {
				response.getWriter().write("SUCCESS");
			} catch (IOException e) {
				log.error("Could not write to response after entity upload", e);
			}
		} catch (RestClientException | URISyntaxException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
}