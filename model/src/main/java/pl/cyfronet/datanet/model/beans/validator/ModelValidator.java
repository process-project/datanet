package pl.cyfronet.datanet.model.beans.validator;

import java.util.ArrayList;
import java.util.List;

import pl.cyfronet.datanet.model.beans.Entity;
import pl.cyfronet.datanet.model.beans.Field;
import pl.cyfronet.datanet.model.beans.Model;

public class ModelValidator {
	private static final String MODEL_NAME_PATTERN = "[A-Za-z]{1}[A-Za-z0-9_-]*";
	private static final String ENTITY_NAME_PATTERN = "[A-Za-z]{1}[A-Za-z0-9_-]*";
	private static final String FIELD_NAME_PATTERN = "[A-Za-z]{1}[A-Za-z0-9_-]*";
	
	public enum ModelError {
		NULL_MODEL,
		EMPTY_MODEL_NAME,
		INVALID_CHARS_MODEL_NAME,
		EMPTY_MODEL_VERSION,
		EMPTY_ENTITIES_LIST,
		EMPTY_ENTITY_NAME,
		EMPTY_FIELDS_LIST,
		EMPTY_FIELD_NAME,
		NULL_FIELD_TYPE,
		INVALID_CHARS_ENTITY_NAME,
		INVALID_CHARS_FIELD_NAME, 
	}
	
	public List<ModelError> validateModel(Model model) {
		List<ModelError> result = new ArrayList<ModelError>();
		
		if(model != null) {
			if(model.getName() == null || model.getName().trim().isEmpty()) {
				result.add(ModelError.EMPTY_MODEL_NAME);
			}
			
			if(model.getName() != null && !model.getName().matches(MODEL_NAME_PATTERN)) {
				result.add(ModelError.INVALID_CHARS_MODEL_NAME);
			}
			
			validateEntities(model.getEntities(), result);
		} else {
			result.add(ModelError.NULL_MODEL);
		}
		
		return result;
	}

	private void validateEntities(List<Entity> entities, List<ModelError> result) {
		if(entities != null && entities.size() > 0) {
			for(Entity entity : entities) {
				if(entity.getName() == null || entity.getName().trim().isEmpty()) {
					result.add(ModelError.EMPTY_ENTITY_NAME);
				}
				
				if(entity.getName() != null && !entity.getName().matches(ENTITY_NAME_PATTERN)) {
					result.add(ModelError.INVALID_CHARS_ENTITY_NAME);
				}
				
				validateFields(entity.getFields(), result);
			}
		} else {
			result.add(ModelError.EMPTY_ENTITIES_LIST);
		}
	}

	private void validateFields(List<Field> fields, List<ModelError> result) {
		if(fields != null && fields.size() > 0) {
			for(Field field : fields) {
				if(field.getName() == null || field.getName().trim().isEmpty()) {
					result.add(ModelError.EMPTY_FIELD_NAME);
				}
				
				if(field.getType() == null) {
					result.add(ModelError.NULL_FIELD_TYPE);
				}
				
				if(field.getName() != null && !field.getName().matches(FIELD_NAME_PATTERN)) {
					result.add(ModelError.INVALID_CHARS_FIELD_NAME);
				}
			}
		} else {
			result.add(ModelError.EMPTY_FIELDS_LIST);
		}
	}
}