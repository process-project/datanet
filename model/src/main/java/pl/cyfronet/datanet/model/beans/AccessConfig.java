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
	private List<String> corsOrigins;

	public AccessConfig() {
	}
	
	public AccessConfig(Access access, List<String> owners, List<String> corsOrigins) {
		this.access = access;
		this.owners = owners;
		this.corsOrigins = corsOrigins;
	}
	
	public Access getAccess() {
		return access;
	}

	public List<String> getOwners() {
		return owners;
	}
	
	public List<String> getCorsOrigins() {
		return corsOrigins;
	}
	
	@Override
	public String toString() {
		return "AccessConfig [access=" + access + ", owners=" + owners
				+ ", corsOrigins=" + corsOrigins + "]";
	}
}