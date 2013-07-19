package pl.cyfronet.datanet.web.client.model;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import pl.cyfronet.datanet.model.beans.Entity;
import pl.cyfronet.datanet.model.beans.Model;

/**
 * Main purpose of this proxy is to allow creation of many new models. New model
 * will have generated id.
 * 
 * @author marek
 */
public class ModelProxy extends Model {

	private static final long serialVersionUID = 1727938745935329386L;

	private Long newModelId;

	private Model model;

	private boolean dirty;

	public ModelProxy(@NotNull Model model) {
		this.model = model;
	}

	public ModelProxy(@NotNull Model model, @NotNull Long newModelId) {
		this.model = model;
		this.newModelId = newModelId;
	}

	@Override
	public long getId() {
		return newModelId != null ? newModelId : model.getId();
	}

	public Model getModel() {
		return model;
	}

	@Override
	public void setId(long id) {
		model.setId(id);
	}

	@Override
	public String getName() {
		return model.getName();
	}

	@Override
	public void setName(String name) {
		model.setName(name);
	}

	@Override
	public Date getTimestamp() {
		return model.getTimestamp();
	}

	@Override
	public void setTimestamp(Date timestamp) {
		model.setTimestamp(timestamp);
	}

	@Override
	public List<Entity> getEntities() {
		return model.getEntities();
	}

	@Override
	public void setEntities(List<Entity> entities) {
		model.setEntities(entities);
	}
	
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	
	public boolean isDirty() {
		return dirty;
	}
	
	public boolean isNew() {
		return newModelId != null;
	}

	@Override
	public String toString() {
		return "ModelProxy [newModelId=" + newModelId + ", model=" + model
				+ ", dirty=" + dirty + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((newModelId == null) ? 0 : newModelId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ModelProxy other = (ModelProxy) obj;
		if (getId() != other.getId())
			return false;
		return true;
	}
}
