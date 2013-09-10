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

	public EntityData retrieveRepositoryData(String repositoryUrl, String entityName, List<String> fileFields, int start, int length, Map<String, String> query) throws RestClientException, URISyntaxException {
		if(start < 1) {
			throw new IllegalArgumentException("start number value has to be at least 1");
		}
		
		String entityUrl = buildEntityUrl(repositoryUrl, entityName, query);
		@SuppressWarnings("unchecked")
		List<Map<String, String>> entities = restTemplate.getForObject(entityUrl, List.class);
		EntityData entityData = new EntityData();
		entityData.setEntityName(entityName);
		entityData.setTotalNumberOfEntities(entities.size());
		entityData.setEntityRows(new ArrayList<Map<String, String>>());
		
		if(start <= entities.size()) {
			entityData.setStartEntityNumber(start);
			
			int currentIndex = start - 1;
			
			while((entityData.getEntityRows().size() <= length || length < 0) && currentIndex < entities.size()) {				
				Map<String, String> fields = entities.get(currentIndex++);
				
				if (fileFields != null && fileFields.size() > 0) {
					processFileData(fields, fileFields, repositoryUrl);
				}
				
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
	public void updateEntityRow(String repositoryUrl, String entityName, String entityRowId,
			Map<String, String> entityRow, Map<String, MultipartFile> files) throws RestClientException, URISyntaxException, IOException {
		if (entityRowId == null) {
			MultiValueMap<String, Object> values = new LinkedMultiValueMap<>();
			
			for(String key : entityRow.keySet()) {
				values.put(key, (List) Arrays.asList(entityRow.get(key)));
			}
			
			if (files != null) {
				for(String fieldName : files.keySet()) {
					HttpHeaders headers = new HttpHeaders();
					headers.setContentDispositionFormData(fieldName, files.get(fieldName).getOriginalFilename());
					headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
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

	private void processFileData(Map<String, String> data, List<String> fileFields, String repositoryUrl) throws RestClientException, URISyntaxException {
		for (String fileFieldName : fileFields) {
			if (data.keySet().contains(fileFieldName)) {
				String id = data.remove(fileFieldName);
				String fileName = restTemplate.getForObject(buildFileNameUrl(repositoryUrl, id), String.class);
				data.put(fileFieldName, fileName + ";" + repositoryUrl + "/file/" + id);
			}
		}
	}

	private String buildFileNameUrl(String repositoryUrl, String fileEntityId) throws URISyntaxException {
		return buildEntityUrl(repositoryUrl, "file", null) + "/" + fileEntityId + "/file_name";
	}
}