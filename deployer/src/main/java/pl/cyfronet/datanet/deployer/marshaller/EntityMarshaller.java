package pl.cyfronet.datanet.deployer.marshaller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import pl.cyfronet.datanet.model.beans.Entity;
import pl.cyfronet.datanet.model.beans.Field;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class EntityMarshaller {
	private ObjectMapper objectMapper;
	private ObjectWriter objectWritter;

	private static final Map<Field.Type, String> typeMap;
	private static final Map<Field.Type, String> arrayTypeMap;

	static {
		Map<Field.Type, String> map = new HashMap<Field.Type, String>();
		
		map.put(Field.Type.Id, JsonFormatTypes.INTEGER.name().toLowerCase());
		map.put(Field.Type.ObjectId, JsonFormatTypes.INTEGER.name().toLowerCase());
		map.put(Field.Type.String, JsonFormatTypes.STRING.name().toLowerCase());
		map.put(Field.Type.Integer, JsonFormatTypes.INTEGER.name().toLowerCase());
		map.put(Field.Type.Float, JsonFormatTypes.NUMBER.name().toLowerCase());
		map.put(Field.Type.Boolean, JsonFormatTypes.BOOLEAN.name().toLowerCase());
		
		typeMap = Collections.unmodifiableMap(map);
		
		Map<Field.Type, String> arrayMap = new HashMap<Field.Type, String>();
		
		arrayMap.put(Field.Type.ObjectIdArray, JsonFormatTypes.INTEGER.name().toLowerCase());
		arrayMap.put(Field.Type.StringArray, JsonFormatTypes.STRING.name().toLowerCase());
		arrayMap.put(Field.Type.IntegerArray, JsonFormatTypes.INTEGER.name().toLowerCase());
		arrayMap.put(Field.Type.FloatArray, JsonFormatTypes.NUMBER.name().toLowerCase());
		arrayMap.put(Field.Type.BooleanArray, JsonFormatTypes.BOOLEAN.name().toLowerCase());
		
		arrayTypeMap = Collections.unmodifiableMap(arrayMap);
	}

	public EntityMarshaller() {
		this(false);
	}

	public EntityMarshaller(boolean prettyOutput) {
		objectMapper = new ObjectMapper();
		if (prettyOutput == true) {
			objectWritter = objectMapper.writerWithDefaultPrettyPrinter();
		} else {
			objectWritter = objectMapper.writer();
		}
	}

	public String marshall(Entity entity) throws MarshallerException {
		ObjectNode objectNode = objectMapper.createObjectNode();

		// construct header
		objectNode.put("title", entity.getName());
		objectNode.put("type", "object");

		// construct properties
		ObjectNode properties = objectNode.putObject("properties");
		ArrayNode links = objectNode.putArray("links");

		// construct fields
		for (Field field : entity.getFields()) {
			buildField(field, properties, links);
		}

		// serialize and return
		try {
			return objectWritter.writeValueAsString(objectNode);
		} catch (JsonProcessingException jex) {
			throw new MarshallerException(jex);
		}
	}

	private void buildField(Field field, ObjectNode rootNode, ArrayNode linksArray)
			throws MarshallerException {
		Field.Type type = field.getType();
		
		if (typeMap.keySet().contains(type)) {
			// primitive type
			ObjectNode fieldObject = rootNode.putObject(field.getName());
			fieldObject.put("type", typeMap.get(type));
			fieldObject.put("required", field.isRequired());
		} else if (arrayTypeMap.keySet().contains(type)) {
			// array type
			ObjectNode fieldObject = rootNode.putObject(field.getName());
			fieldObject.put("type", "array");
			ObjectNode arrayInfo = fieldObject.putObject("items");
			arrayInfo.put("type", arrayTypeMap.get(type));
			fieldObject.put("required", field.isRequired());
		} else if (Field.Type.File.equals(type)) {
			// file type
			ObjectNode fieldObject = rootNode.putObject(field.getName() + "_id");
			fieldObject.put("type", JsonFormatTypes.STRING.name().toLowerCase());
			fieldObject.put("required", field.isRequired());
			
			ObjectNode linksEntry = linksArray.addObject();
			linksEntry.put("rel", field.getName() + "_id");
			linksEntry.put("href", "/entity/file/{" + field.getName() + "_id}");
			linksEntry.put("targetSchema", "file");
		} else {
			// unknown type
			throw new MarshallerException("Uknown field type: " + type.name());
		}
		
	}

}
