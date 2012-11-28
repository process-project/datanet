package pl.cyfronet.datanet.web.server.services.portallogin;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "userData")
public class UserData {
	private String login;
	private String token;
	private String name;
	private String surname;
	private String email;
	
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	@Override
	public String toString() {
		return "UserData [login=" + login + ", token=" + token + ", name="
				+ name + ", surname=" + surname + ", email=" + email + "]";
	}
}