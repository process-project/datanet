package pl.cyfronet.datanet.model.beans;

import java.io.Serializable;
import java.util.List;

public class AccessConfig implements Serializable {
	private static final long serialVersionUID = 5209968519331332799L;

	public enum Access {
		publicAccess,
		privateAccess
	}
	
	public static final String OWNER_SEPARATOR = ",";
	
	private Access access;
	private List<String> owners;
	
	public AccessConfig() {
	}
	
	public AccessConfig(Access access, List<String> owners) {
		this.access = access;
		this.owners = owners;
	}
	
	public Access getAccess() {
		return access;
	}

	public List<String> getOwners() {
		return owners;
	}
	
	@Override
	public String toString() {
		return "AccessConfig [access=" + access + ", owners=" + owners + "]";
	}
}