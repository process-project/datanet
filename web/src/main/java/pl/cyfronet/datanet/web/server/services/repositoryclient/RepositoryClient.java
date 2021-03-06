package pl.cyfronet.datanet.web.server.services.repositoryclient;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static pl.cyfronet.datanet.model.beans.AccessConfig.Access.publicAccess;
import static pl.cyfronet.datanet.model.beans.AccessConfig.Isolation.isolationEnabled;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import pl.cyfronet.datanet.model.beans.AccessConfig;
import pl.cyfronet.datanet.model.beans.AccessConfig.Access;
import pl.cyfronet.datanet.model.beans.AccessConfig.Isolation;
import pl.cyfronet.datanet.web.client.controller.beans.EntityData;

public class RepositoryClient {
	private static final Logger log = LoggerFactory.getLogger(RepositoryClient.class);
	
	private RestTemplate restTemplate;
	
	public RepositoryClient(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public EntityData retrieveRepositoryData(String repositoryId, String repositoryUrl, String entityName, List<String> fileFields, int start, int length, Map<String, String> query) throws RestClientException, URISyntaxException {
		if(start < 1) {
			throw new IllegalArgumentException("start number value has to be at least 1");
		}
		
		String entityUrl = buildEntityUrl(repositoryUrl, entityName, query);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> entities = restTemplate.getForObject(entityUrl, List.class);
		log.trace("Retrieved repository entities: {}", entities);
		EntityData entityData = new EntityData();
		entityData.setEntityName(entityName);
		entityData.setTotalNumberOfEntities(entities.size());
		entityData.setEntityRows(new ArrayList<Map<String, String>>());
		
		if(start <= entities.size()) {
			entityData.setStartEntityNumber(start);
			
			int currentIndex = start - 1;
			
			while((entityData.getEntityRows().size() <= length || length < 0) && currentIndex < entities.size()) {				
				Map<String, Object> fields = entities.get(currentIndex++);
				
				if (fileFields != null && fileFields.size() > 0) {
					processFileData(fields, fileFields, repositoryUrl, repositoryId);
				}
				
				Map<String, String> values = new HashMap<>();
				
				for (String key: fields.keySet()) {
					String value = fields.get(key).toString();
					values.put(key, value);
				}
				
				entityData.getEntityRows().add(values);
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
		log.debug("Updating entity row for repository {} and entity {}", repositoryUrl, entityName);
		
		if (entityRowId == null) {
			MultiValueMap<String, Object> values = new LinkedMultiValueMap<>();
			
			for(String key : entityRow.keySet()) {
				HttpHeaders headers = new HttpHeaders();
				headers.setContentDispositionFormData(key, null);
				headers.setContentType(new MediaType("text", "plain", Charset.forName("utf-8")));
				
				HttpEntity fieldEntity = new HttpEntity(entityRow.get(key), headers);
				values.put(key, (List) Arrays.asList(fieldEntity));
			}
			
			if (files != null) {
				for(String fieldName : files.keySet()) {
					HttpHeaders headers = new HttpHeaders();
					headers.setContentDispositionFormData(fieldName, files.get(fieldName).getOriginalFilename());
					headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
					
					HttpEntity fileEntity = new HttpEntity(new InputStreamResource(files.get(fieldName).getInputStream()), headers);
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
	
	public void waitUntilRepositoryAvailable(String repositoryUrl) {
		HttpStatus statusCode = null;
		
		do {
			try {
				ResponseEntity<String> response = restTemplate.getForEntity(repositoryUrl, String.class);
				statusCode = response.getStatusCode();
			} catch (HttpClientErrorException e) {
				statusCode = e.getStatusCode();
			}
			
			if (statusCode == HttpStatus.NOT_FOUND) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					log.warn("Repository availability sleep unexpectedly interrupted for repository {}", repositoryUrl);
				}
			}
		} while (statusCode != null && statusCode == HttpStatus.NOT_FOUND);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public AccessConfig getAccessConfig(String repositoryUrl, String token) {
		Map<String, Object> result = null;
		
		try {
			result = restTemplate.getForObject(buildConfigUrl(repositoryUrl, token), Map.class);
		} catch (RestClientException e) {
			log.warn("Could not obtain access configuration for repository with url {}", repositoryUrl);
			log.warn("Error for the above was: ", e);
			
			return null;
		}
		
		Access access = null;
		List<String> owners = new ArrayList<>();
		List<String> corsOrigins = new ArrayList<>();
		Isolation isolation = null;
		
		if(result.get("repository_type") != null && result.get("repository_type").equals("private")) {
			access = Access.privateAccess;
		} else if (result.get("repository_type") != null && result.get("repository_type").equals("public")) {
			access = Access.publicAccess;
		}
		
		if(result.get("owners") != null) {
			owners.addAll((List) result.get("owners"));
		}
		
		if(result.get("cors_origins") != null) {
			corsOrigins.addAll((List) result.get("cors_origins"));
		}
		
		if(result.get("data_separation") != null && result.get("data_separation").equals(true)) {
			isolation = Isolation.isolationEnabled;
		} else if(result.get("data_separation") != null && result.get("data_separation").equals(false)) {
			isolation = Isolation.isolationDisabled;
		} else {
			isolation = Isolation.isolationDisabled;
		}
		
		AccessConfig accessConfig = new AccessConfig(access, owners, corsOrigins, isolation);
		log.debug("Access config for repository {} retrieved: {}", repositoryUrl, accessConfig);

		return accessConfig;
	}
	
	public void updateAccessConfiguration(String repositoryUrl, String token, AccessConfig accessConfig) {
		Map<String, Object> request = new HashMap<>();
		request.put("repository_type", accessConfig.getAccess() == publicAccess ? "public" : "private");
		request.put("owners", accessConfig.getOwners());
		request.put("cors_origins", accessConfig.getCorsOrigins());
		request.put("data_separation", accessConfig.getIsolation() == isolationEnabled ? true : false);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(APPLICATION_JSON);
		
		HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(request, headers);
		restTemplate.put(buildConfigUrl(repositoryUrl, token), requestEntity);
	}
	
	public void removeEntityRow(String repositoryUrl, String rowId, String entityName) throws RestClientException, URISyntaxException {
		String deleteUrl = buildEntityRowUrl(repositoryUrl, entityName, rowId);
		log.info("Requesting a delete operation on resource {}", deleteUrl);
		restTemplate.delete(deleteUrl);
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
		
		log.trace("Built entity URL: {}", url);
		
		return url;
	}

	private void processFileData(Map<String, Object> data, List<String> fileFields, String repositoryUrl, String repositoryId) throws RestClientException, URISyntaxException {
		for (String fileFieldName : fileFields) {
			if (data.keySet().contains(fileFieldName)) {
				String id = data.remove(fileFieldName).toString();
				String fileName = restTemplate.getForObject(buildFileNameUrl(repositoryUrl, id), String.class);
				data.put(fileFieldName, fileName + ";" + repositoryId + ";" + id);
			}
		}
	}

	private String buildFileNameUrl(String repositoryUrl, String fileEntityId) throws URISyntaxException {
		return buildEntityUrl(repositoryUrl, "file", null) + "/" + fileEntityId + "/file_name";
	}

	private String buildConfigUrl(String repositoryUrl, String token) {
		return repositoryUrl + "/_configuration?private_token=" + token;
	}

	public void getFile(String fileUrl, final OutputStream outputStream) {
		restTemplate.execute(fileUrl, HttpMethod.GET, new RequestCallback() {
			@Override
			public void doWithRequest(ClientHttpRequest request) throws IOException {
				//nothing to be added here
			}
		}, new ResponseExtractor<Void>() {
			@Override
			public Void extractData(ClientHttpResponse response) throws IOException {
				IOUtils.copy(response.getBody(), outputStream);
				
				return null;
			}
		});
	}

	private String buildEntityRowUrl(String url, String entityName, String rowId) throws URISyntaxException {
		return buildEntityUrl(url, entityName, null) + "/" + rowId;
	}
}