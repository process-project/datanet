package pl.cyfronet.datanet.web.server.controllers.beans;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public class EntityUpload {
	private Map<String, String> fields;
	private Map<String, MultipartFile> files;
	private long repositoryId;
	private String entityName;
	
	public EntityUpload() {
		fields = new HashMap<>();
		files = new HashMap<>();
	}
	
	public Map<String, String> getFields() {
		return fields;
	}
	public void setFields(Map<String, String> fields) {
		this.fields = fields;
	}
	
	@Override
	public String toString() {
		return "EntityUpload [fields=" + fields + ", files=" + getFiles() + "]";
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public long getRepositoryId() {
		return repositoryId;
	}

	public void setRepositoryId(long repositoryId) {
		this.repositoryId = repositoryId;
	}

	public Map<String, MultipartFile> getFiles() {
		return files;
	}

	public void setFiles(Map<String, MultipartFile> files) {
		this.files = files;
	}
}