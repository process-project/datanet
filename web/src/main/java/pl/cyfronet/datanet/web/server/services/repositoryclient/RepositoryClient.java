package pl.cyfronet.datanet.web.server.services.repositoryclient;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import pl.cyfronet.datanet.web.client.controller.beans.EntityData;

@Service
public class RepositoryClient {
	private static final Logger log = LoggerFactory.getLogger(RepositoryClient.class);
	
	@Autowired private RestTemplate restTemplate;
	
	public RepositoryClient() {
		
	}
	
	public RepositoryClient(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public EntityData retrieveRepositoryData(String repositoryUrl, String entityName, int start, int length, Map<String, String> query) throws RestClientException, URISyntaxException {
		if(start < 1) {
			throw new IllegalArgumentException("start number value has to be at least 1");
		}
		
		String entityUrl = buildEntityUrl(repositoryUrl, entityName, query);
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

	@SuppressWarnings({"unchecked", "rawtypes"})
	public void updateEntityRow(String repositoryUrl, String entityName, String entityRowId, Map<String, String> entityRow, Map<String, MultipartFile> files) throws RestClientException, URISyntaxException, IOException {
		if (entityRowId == null) {
			MultiValueMap<String, Object> values = new LinkedMultiValueMap<>();
			
			for(String key : entityRow.keySet()) {
				values.put(key, (List) Arrays.asList(entityRow.get(key)));
			}
			
			if (files != null) {
				for(String fieldName : files.keySet()) {
					HttpHeaders headers = new HttpHeaders();
					headers.setContentDispositionFormData(fieldName, files.get(fieldName).getOriginalFilename());
					HttpEntity fileEntity = new HttpEntity(files.get(fieldName).getBytes(), headers);
					values.put(fieldName, (List) Arrays.asList(fileEntity));
				}
			}
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			
			HttpEntity request = new HttpEntity(values, headers);
			String response = restTemplate.postForObject(buildEntityUrl(repositoryUrl, entityName, null), request, String.class);
			log.debug("New entity successfully saved with id {}", response);
		} else {
			throw new IllegalArgumentException("Updating entities is not supported yet!");
		}
	}
	
	private String buildEntityInstanceUrl(String repositoryUrl, String entityName, String entityId) throws URISyntaxException {
		String url = buildEntityUrl(repositoryUrl, entityName, null) + "/" + entityId;
		
		return url;
	}

	private String buildEntityUrl(String repositoryUrl, String entityName, Map<String, String> query) throws URISyntaxException {
		if (repositoryUrl != null && !repositoryUrl.endsWith("/")) {
			repositoryUrl = repositoryUrl + "/";
		}
		
		String url = repositoryUrl + entityName;
		
		if(query != null && query.size() > 0) {
			url += "?";
			
			for(String fieldName : query.keySet()) {
				url += fieldName + "=" + query.get(fieldName) + "&";
			}
			
			url = url.substring(0, url.length() - 1);
		}
		
		return url;
	}

}