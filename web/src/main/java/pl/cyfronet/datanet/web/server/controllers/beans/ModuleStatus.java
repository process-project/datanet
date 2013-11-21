package pl.cyfronet.datanet.web.server.controllers.beans;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

import pl.cyfronet.datanet.web.server.controllers.beans.HealthStatus.Status;

public class ModuleStatus {
	private Status status;
	private String message;
	
	@XmlAttribute
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	
	@XmlValue
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}