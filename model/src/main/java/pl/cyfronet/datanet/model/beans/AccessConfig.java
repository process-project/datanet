package pl.cyfronet.datanet.model.beans;

import java.io.Serializable;
import java.util.List;

public class AccessConfig implements Serializable {
	private static final long serialVersionUID = -9049554852117750777L;

	public enum Access {
		publicAccess,
		privateAccess
	}
	
	public enum Isolation {
		isolationEnabled,
		isolationDisabled
	}
	
	public static final String OWNER_SEPARATOR = ",";
	
	private Access access;
	private List<String> owners;
	private List<String> corsOrigins;
	private Isolation isolation;
	
	public AccessConfig() {
	}
	
	public AccessConfig(Access access, List<String> owners, List<String> corsOrigins, Isolation isolation) {
		this.access = access;
		this.owners = owners;
		this.corsOrigins = corsOrigins;
		this.isolation = isolation;
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
		return "AccessConfig [access=" + access + ", owners=" + owners + ", corsOrigins=" + corsOrigins + ", isolation=" + isolation + "]";
	}

	public Isolation getIsolation() {
		return isolation;
	}

	public void setIsolation(Isolation isolation) {
		this.isolation = isolation;
	}
}