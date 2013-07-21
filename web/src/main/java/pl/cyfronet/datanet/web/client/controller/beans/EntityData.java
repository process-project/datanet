package pl.cyfronet.datanet.web.client.controller.beans;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class EntityData implements Serializable {
	private static final long serialVersionUID = -19578349783000989L;
	
	private int totalNumberOfEntities;
	private int startEntityNumber;
	private int currentNumberOfEntities; 
	private String entityName;
	private List<Map<String, String>> entityRows;
	
	public int getTotalNumberOfEntities() {
		return totalNumberOfEntities;
	}
	public void setTotalNumberOfEntities(int totalNumberOfEntities) {
		this.totalNumberOfEntities = totalNumberOfEntities;
	}
	public int getStartEntityNumber() {
		return startEntityNumber;
	}
	public void setStartEntityNumber(int startEntityNumber) {
		this.startEntityNumber = startEntityNumber;
	}
	public int getCurrentNumberOfEntities() {
		return currentNumberOfEntities;
	}
	public void setCurrentNumberOfEntities(int currentNumberOfEntities) {
		this.currentNumberOfEntities = currentNumberOfEntities;
	}
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	public List<Map<String, String>> getEntityRows() {
		return entityRows;
	}
	public void setEntityRows(List<Map<String, String>> entityRows) {
		this.entityRows = entityRows;
	}
}