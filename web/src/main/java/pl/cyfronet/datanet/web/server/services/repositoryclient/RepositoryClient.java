package pl.cyfronet.datanet.web.server.services.repositoryclient;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import pl.cyfronet.datanet.web.client.controller.beans.EntityData;

@Service
public class RepositoryClient {
	private static final Logger log = LoggerFactory.getLogger(RepositoryClient.class);
	
	@Autowired private RestTemplate restTemplate;
	
	public EntityData retrieveRepositoryData(String repositoryUrl, String entityName, int start, int length) throws RestClientException, URISyntaxException {
		if(start < 1) {
			throw new IllegalArgumentException("start number value has to be at least 1");
		}
		
		String entityUrl = buildEntityUrl(repositoryUrl, entityName);
		@SuppressWarnings("unchecked")
		List<String> ids = restTemplate.getForObject(entityUrl, List.class);
		EntityData entityData = new EntityData();
		entityData.setEntityName(entityName);
		entityData.setTotalNumberOfEntities(ids.size());
		entityData.setEntityRows(new ArrayList<Map<String, String>>());
		
		if(start <= ids.size()) {
			entityData.setStartEntityNumber(start);
			
			int currentIndex = start - 1;
			
			while((entityData.getEntityRows().size() <= length || length < 0) && currentIndex < ids.size()) {
				@SuppressWarnings("unchecked")
				Map<String, String> fields = restTemplate.getForObject(buildEntityInstanceUrl(repositoryUrl, entityName, ids.get(currentIndex++)), Map.class);
				entityData.getEntityRows().add(fields);
			}
			
			entityData.setCurrentNumberOfEntities(entityData.getEntityRows().size());
		} else {
			entityData.setStartEntityNumber(0);
			entityData.setCurrentNumberOfEntities(0);
		}
		
		
		return entityData;
	}

	public void updateEntityRow(String repositoryUrl, String entityName, String entityRowId, Map<String, String> entityRow) throws RestClientException, URISyntaxException {
		if (entityRowId == null) {
			MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
			
			for(String key : entityRow.keySet()) {
				request.put(key, Arrays.asList(entityRow.get(key)));
			}
			
			String response = restTemplate.postForObject(buildEntityUrl(repositoryUrl, entityName), request, String.class);
			log.debug("New entity successfully saved with id {}", response);
		} else {
			throw new IllegalArgumentException("Updating entities is not supported yet!");
		}
	}
	
	private String buildEntityInstanceUrl(String repositoryUrl, String entityName, String entityId) throws URISyntaxException {
		return buildEntityUrl(repositoryUrl, entityName) + "/" + entityId;
	}

	private String buildEntityUrl(String repositoryUrl, String entityName) throws URISyntaxException {
		if (repositoryUrl != null && !repositoryUrl.endsWith("/")) {
			repositoryUrl = repositoryUrl + "/";
		}
		
		return repositoryUrl + entityName;
	}
}