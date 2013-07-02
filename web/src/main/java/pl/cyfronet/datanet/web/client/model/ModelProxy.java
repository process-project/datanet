package pl.cyfronet.datanet.web.client.model;

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
	public String getVersion() {
		return model.getVersion();
	}

	@Override
	public void setVersion(String version) {
		model.setVersion(version);
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
}
