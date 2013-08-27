package pl.cyfronet.datanet.deployer.marshaller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import pl.cyfronet.datanet.model.beans.Entity;
import pl.cyfronet.datanet.model.beans.Field;
import pl.cyfronet.datanet.model.beans.Model;
import pl.cyfronet.datanet.model.beans.Type;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ModelMarchaller {

	private static final Map<Type, String> typeMap;
	private static final Map<Type, String> arrayTypeMap;

	private ObjectMapper objectMapper;
	private ObjectWriter objectWritter;
	private Model model;
	private HashMap<Entity, Map<String, Entity>> oneToManyRelations;

	static {
		Map<Type, String> map = new HashMap<Type, String>();

		map.put(Type.String, JsonFormatTypes.STRING.name().toLowerCase());
		map.put(Type.Integer, JsonFormatTypes.INTEGER.name().toLowerCase());
		map.put(Type.Float, JsonFormatTypes.NUMBER.name().toLowerCase());
		map.put(Type.Boolean, JsonFormatTypes.BOOLEAN.name().toLowerCase());

		typeMap = Collections.unmodifiableMap(map);

		Map<Type, String> arrayMap = new HashMap<Type, String>();

		arrayMap.put(Type.StringArray, JsonFormatTypes.STRING.name()
				.toLowerCase());
		arrayMap.put(Type.IntegerArray, JsonFormatTypes.INTEGER.name()
				.toLowerCase());
		arrayMap.put(Type.FloatArray, JsonFormatTypes.NUMBER.name()
				.toLowerCase());
		arrayMap.put(Type.BooleanArray, JsonFormatTypes.BOOLEAN.name()
				.toLowerCase());

		arrayTypeMap = Collections.unmodifiableMap(arrayMap);
	}

	public ModelMarchaller(Model model) {
		this.model = model;

		objectMapper = new ObjectMapper();
		objectWritter = objectMapper.writer();
	}

	public Map<String, String> marchall() throws MarshallerException {
		discoverOneToManyRelations();

		Map<String, String> schemas = new HashMap<>();
		for (Entity entity : model.getEntities()) {
			schemas.put(entity.getName(), marchall(entity));
		}

		return schemas;
	}

	private void discoverOneToManyRelations() {
		oneToManyRelations = new HashMap<>();

		for (Entity entity : model.getEntities()) {
			for (Field field : entity.getFields()) {
				if (field.getType() == Type.ObjectIdArray) {
					Map<String, Entity> relations = oneToManyRelations
							.get(field.getTarget());
					if (relations == null) {
						relations = new HashMap<>();
						oneToManyRelations.put(field.getTarget(), relations);
					}
					relations.put(field.getName(), entity);
				}
			}
		}
	}

	private String marchall(Entity entity) throws MarshallerException {
		ObjectNode objectNode = objectMapper.createObjectNode();

		objectNode.put("title", entity.getName());
		objectNode.put("type", "object");

		ObjectNode properties = objectNode.putObject("properties");
		ArrayNode links = objectNode.putArray("links");

		for (Field field : entity.getFields()) {
			buildField(field, properties, links);
		}

		if (links.size() == 0) {
			objectNode.remove("links");
		}

		try {
			return objectWritter.writeValueAsString(objectNode);
		} catch (JsonProcessingException jex) {
			throw new MarshallerException(jex);
		}
	}

	private void buildField(Field field, ObjectNode rootNode,
			ArrayNode linksArray) throws MarshallerException {
		Type type = field.getType();

		if (typeMap.keySet().contains(type)) {
			createSimpleTypeField(field, rootNode);
		} else if (arrayTypeMap.keySet().contains(type)) {
			createArrayTypeField(field, rootNode);
		} else if (Type.File.equals(type)) {
			createFileField(field, rootNode, linksArray);
		} else if (Type.ObjectId.equals(type)) {
			createOneToOneRelation(field, rootNode, linksArray);
		} else if (Type.ObjectIdArray.equals(type)) {
			createOneToManyRelation(field, rootNode, linksArray);
		} else {
			// unknown type
			throw new MarshallerException("Uknown field type: " + type.name());
		}
	}

	private void createSimpleTypeField(Field field, ObjectNode rootNode) {
		createProperty(rootNode, field.getName(), typeMap.get(field.getType()),
				field.isRequired());
	}

	private ObjectNode createProperty(ObjectNode parent, String name,
			String type, boolean required) {
		ObjectNode fieldObject = parent.putObject(name);
		fieldObject.put("type", type);
		fieldObject.put("required", required);

		return fieldObject;
	}

	private void createArrayTypeField(Field field, ObjectNode rootNode) {
		createArrayTypeField(field, rootNode, arrayTypeMap.get(field.getType()));
	}

	private void createArrayTypeField(Field field, ObjectNode rootNode,
			String type) {
		ObjectNode fieldObject = createProperty(rootNode, field.getName(),
				"array", field.isRequired());
		ObjectNode arrayInfo = fieldObject.putObject("items");
		arrayInfo.put("type", type);
	}

	private void createFileField(Field field, ObjectNode rootNode,
			ArrayNode linksArray) {
		createOneToOneRelation(field, "file", rootNode, linksArray);
	}

	private void createOneToOneRelation(Field field, ObjectNode rootNode,
			ArrayNode linksArray) {
		createOneToOneRelation(field, field.getTarget().getName(), rootNode,
				linksArray);
	}

	private void createOneToOneRelation(Field field, String target,
			ObjectNode rootNode, ArrayNode linksArray) {
		createProperty(rootNode, String.format("%s_id", field.getName()),
				"string", field.isRequired());
		createLink(linksArray, field.getName(), target);
	}

	private void createLink(ArrayNode linksArray, String name, String target) {
		ObjectNode linksEntry = linksArray.addObject();
		linksEntry.put("rel", name);
		linksEntry.put("href", String.format("/%s/{%s_id}", target, name));
		linksEntry.put("targetSchema", target);
	}

	private void createOneToManyRelation(Field field, ObjectNode rootNode,
			ArrayNode linksArray) {
		createArrayTypeField(field, rootNode, "string");
		createLink(linksArray, field.getName(), field.getTarget().getName());
	}
}
