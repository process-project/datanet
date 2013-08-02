package pl.cyfronet.datanet.web.server.controllers.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public class EntityUpload {
	private Map<String, String> fields;
	private List<MultipartFile> files;
	private long repositoryId;
	private String entityName;
	
	public EntityUpload() {
		fields = new HashMap<>();
		files = new ArrayList<>();
	}
	
	public Map<String, String> getFields() {
		return fields;
	}
	public void setFields(Map<String, String> fields) {
		this.fields = fields;
	}
	public List<MultipartFile> getFiles() {
		return files;
	}
	public void setFiles(List<MultipartFile> files) {
		this.files = files;
	}
	
	@Override
	public String toString() {
		return "EntityUpload [fields=" + fields + ", files=" + files + "]";
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
}