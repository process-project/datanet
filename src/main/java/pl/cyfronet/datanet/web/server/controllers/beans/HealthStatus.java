package pl.cyfronet.datanet.web.server.controllers.beans;

import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "healthdata")
public class HealthStatus {
	public enum Status {
		ok,
		failed,
		warning
	}
	
	private Date date;
	private Status status;
	private String message;
	private ModuleStatus database;
	private ModuleStatus cloudFoundry;
	
	@XmlAttribute
	@XmlJavaTypeAdapter(JaxbDateAdapter.class)
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	@XmlAttribute
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	
	@XmlAttribute
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public ModuleStatus getCloudFoundry() {
		return cloudFoundry;
	}
	public void setCloudFoundry(ModuleStatus cloudFoundry) {
		this.cloudFoundry = cloudFoundry;
	}
	public ModuleStatus getDatabase() {
		return database;
	}
	public void setDatabase(ModuleStatus database) {
		this.database = database;
	}
	
	@Override
	public String toString() {
		return "HealthStatus [date=" + date + ", status=" + status
				+ ", message=" + message + ", database=" + database
				+ ", cloudFoundry=" + cloudFoundry + "]";
	}
}