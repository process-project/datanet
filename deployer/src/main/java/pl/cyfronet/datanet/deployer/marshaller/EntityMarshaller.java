package pl.cyfronet.datanet.deployer.marshaller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import pl.cyfronet.datanet.model.beans.Entity;
import pl.cyfronet.datanet.model.beans.Field;
import pl.cyfronet.datanet.model.beans.Type;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class EntityMarshaller {
	private ObjectMapper objectMapper;
	private ObjectWriter objectWritter;

	private static final Map<Type, String> typeMap;
	private static final Map<Type, String> arrayTypeMap;

	static {
		Map<Type, String> map = new HashMap<Type, String>();
		
		map.put(Type.ObjectId, JsonFormatTypes.STRING.name().toLowerCase());
		map.put(Type.String, JsonFormatTypes.STRING.name().toLowerCase());
		map.put(Type.Integer, JsonFormatTypes.INTEGER.name().toLowerCase());
		map.put(Type.Float, JsonFormatTypes.NUMBER.name().toLowerCase());
		map.put(Type.Boolean, JsonFormatTypes.BOOLEAN.name().toLowerCase());
		
		typeMap = Collections.unmodifiableMap(map);
		
		Map<Type, String> arrayMap = new HashMap<Type, String>();
		
		arrayMap.put(Type.ObjectIdArray, JsonFormatTypes.STRING.name().toLowerCase());
		arrayMap.put(Type.StringArray, JsonFormatTypes.STRING.name().toLowerCase());
		arrayMap.put(Type.IntegerArray, JsonFormatTypes.INTEGER.name().toLowerCase());
		arrayMap.put(Type.FloatArray, JsonFormatTypes.NUMBER.name().toLowerCase());
		arrayMap.put(Type.BooleanArray, JsonFormatTypes.BOOLEAN.name().toLowerCase());
		
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
		
		if(links.size() == 0) {
			//links arrayNode is empty, so we don't need it
			objectNode.remove("links");
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
		Type type = field.getType();
		
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
		} else if (Type.File.equals(type)) {
			// file type
			ObjectNode fieldObject = rootNode.putObject(field.getName());
			fieldObject.put("type", JsonFormatTypes.STRING.name().toLowerCase());
			fieldObject.put("required", field.isRequired());
			
			ObjectNode linksEntry = linksArray.addObject();
			linksEntry.put("rel", field.getName());
			linksEntry.put("href", String.format("/file/{%s}", field.getName()));
			linksEntry.put("targetSchema", "file");
		} else {
			// unknown type
			throw new MarshallerException("Uknown field type: " + type.name());
		}
		
	}
}