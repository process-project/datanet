package pl.cyfronet.datanet.model.beans;

import java.io.Serializable;

public class Version extends Model implements Serializable, Comparable<Version> {
	private static final long serialVersionUID = 6566696681377524989L;
	
	private long modelId;
	
	public Version() {
		super();
	}
	
	public Version(Model model, String versionName) {
		super();
		setName(versionName);
		setTimestamp(model.getTimestamp());
		setEntities(model.getEntities());
		setModelId(model.getId());
	}
	
	public long getModelId() {
		return modelId;
	}
	
	public void setModelId(long modelId) {
		this.modelId = modelId;
	}

	@Override
	public int compareTo(Version o) {
		return getName().compareToIgnoreCase(o.getName());
	}	
}