package pl.cyfronet.datanet.deployer.marshaller;

import java.util.HashMap;
import java.util.Map;


import pl.cyfronet.datanet.model.beans.Entity;
import pl.cyfronet.datanet.model.beans.Model;

public class ModelMarshaller {
	private EntityMarshaller entityMarshaller;

	public ModelMarshaller() {
		this(false);
	}

	public ModelMarshaller(boolean prettyOutput) {
		entityMarshaller = new EntityMarshaller(prettyOutput);
	}

	public Map<String, String> marshall(Model model) throws MarshallerException {
		Map<String, String> result = new HashMap<String, String>();
		for(Entity entity : model.getEntities()) {
			result.put(entity.getName(), entityMarshaller.marshall(entity));
		}
		return result;
	}
}
