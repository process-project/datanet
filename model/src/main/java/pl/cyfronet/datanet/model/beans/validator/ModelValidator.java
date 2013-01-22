package pl.cyfronet.datanet.model.beans.validator;

import java.util.ArrayList;
import java.util.List;

import pl.cyfronet.datanet.model.beans.Entity;
import pl.cyfronet.datanet.model.beans.Field;
import pl.cyfronet.datanet.model.beans.Model;


public class ModelValidator {
	public enum ModelError {
		NULL_MODEL,
		EMPTY_MODEL_NAME,
		EMPTY_MODEL_VERSION,
		NULL_ENTITY_LIST,
		EMPTY_ENTITY_NAME,
		NULL_FIELD_LIST,
		EMPTY_FIELD_NAME,
		NULL_FIELD_TYPE
	}
	
	public List<ModelError> validateModel(Model model) {
		List<ModelError> result = new ArrayList<ModelError>();
		
		if(model != null) {
			if(model.getName() == null || model.getName().trim().isEmpty()) {
				result.add(ModelError.EMPTY_MODEL_NAME);
			}
			
			if(model.getVersion() == null || model.getVersion().trim().isEmpty()) {
				result.add(ModelError.EMPTY_MODEL_VERSION);
			}
			
			validateEntities(model.getEntities(), result);
		} else {
			result.add(ModelError.NULL_MODEL);
		}
		
		return result;
	}

	private void validateEntities(List<Entity> entities, List<ModelError> result) {
		if(entities != null) {
			for(Entity entity : entities) {
				if(entity.getName() == null || entity.getName().trim().isEmpty()) {
					result.add(ModelError.EMPTY_ENTITY_NAME);
				}
				
				validateFields(entity.getFields(), result);
			}
		} else {
			result.add(ModelError.NULL_ENTITY_LIST);
		}
	}

	private void validateFields(List<Field> fields, List<ModelError> result) {
		if(fields != null) {
			for(Field field : fields) {
				if(field.getName() == null || field.getName().trim().isEmpty()) {
					result.add(ModelError.EMPTY_FIELD_NAME);
				}
				
				if(field.getType() == null) {
					result.add(ModelError.NULL_FIELD_TYPE);
				}
			}
		} else {
			result.add(ModelError.NULL_FIELD_LIST);
		}
	}
}