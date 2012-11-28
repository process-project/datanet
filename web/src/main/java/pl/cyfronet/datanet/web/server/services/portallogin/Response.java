package pl.cyfronet.datanet.web.server.services.portallogin;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "response")
public class Response {
	private Status status;
	private String challenge;
	private String cause;
	private UserData userData;
	
	public enum Status {
		OK,
		failed
	}
	
	@XmlAttribute
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public String getChallenge() {
		return challenge;
	}
	public void setChallenge(String challenge) {
		this.challenge = challenge;
	}

	public String getCause() {
		return cause;
	}
	public void setCause(String cause) {
		this.cause = cause;
	}
	
	public UserData getUserData() {
		return userData;
	}
	public void setUserData(UserData userData) {
		this.userData = userData;
	}
	
	@Override
	public String toString() {
		return "Response [status=" + status + ", challenge=" + challenge
				+ ", cause=" + cause + ", userData=" + userData + "]";
	}
}