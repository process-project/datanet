package pl.cyfronet.datanet.deployer.marshaller;

import java.util.Map;

import pl.cyfronet.datanet.model.beans.Model;

public class ModelSchemaGenerator {

	public Map<String, String> generateSchema(Model model) throws MarshallerException {
		return new ModelMarchaller(model).marchall();
	}
}