package pl.cyfronet.datanet.deployer.test.marshaller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import pl.cyfronet.datanet.deployer.marshaller.ModelMarchaller;
import pl.cyfronet.datanet.model.beans.Entity;
import pl.cyfronet.datanet.model.beans.Field;
import pl.cyfronet.datanet.model.beans.Model;
import pl.cyfronet.datanet.model.beans.Type;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ModelMarchallerTest {
	private static final String ONE_TO_MANY_FIELD = "oneToManyField";
	private static final String ENTITY_NAME = "e1";
	private static final String ONE_TO_ONE_RELATION = "oneToOneRelation";
	private Model model;
	private Map<String, String> resultSchemas;
	private ObjectMapper mapper = new ObjectMapper();

	@Before
	public void setUp() {
		model = new Model();
		model.setName("modelName");
	}

	// @DataProvider
	private Object[][] getSingleTypes() {
		return new Object[][] { { Type.Boolean, "boolean" },
				{ Type.Float, "number" }, { Type.Integer, "integer" },
				{ Type.String, "string" } };
	}

	@Test
	public void shouldCreateSchemaForModelWithSimpleFields() throws Exception {
		Object[][] types = getSingleTypes();
		for (Object[] typeTuple : types) {
			givenModelWithEntityWithField((Type) typeTuple[0]);
			whenCreateJsonSchema();
			thenJsonWithEntityWithFieldGenerated((String) typeTuple[1]);
		}
	}

	private void givenModelWithEntityWithField(Type type) {
		Entity entity = new Entity();
		entity.setName(ENTITY_NAME);

		Field requiredField = new Field();
		requiredField.setRequired(true);
		requiredField.setName("requiredField");
		requiredField.setType(type);

		Field notRequiredField = new Field();
		notRequiredField.setRequired(false);
		notRequiredField.setName("notRequiredField");
		notRequiredField.setType(type);

		entity.setFields(Arrays.asList(requiredField, notRequiredField));
		model.setEntities(Arrays.asList(entity));
	}

	private void whenCreateJsonSchema() throws Exception {
		resultSchemas = new ModelMarchaller(model).marchall();
	}

	private void thenJsonWithEntityWithFieldGenerated(String typeName)
			throws Exception {
		thenEntityWithFieldGenerated("requiredField", typeName, true);
		thenEntityWithFieldGenerated("notRequiredField", typeName, false);
	}

	private void thenEntityWithFieldGenerated(String fieldName,
			String typeName, boolean required) throws Exception {
		Map<String, Object> property = getProperty(fieldName);
		assertEquals(required, (Boolean) property.get("required"));
		assertEquals(typeName, property.get("type"));
	}

	// @DataProvider
	private Object[][] getArrayTypes() {
		return new Object[][] { { Type.BooleanArray, "boolean" },
				{ Type.FloatArray, "number" },
				{ Type.IntegerArray, "integer" },
				{ Type.StringArray, "string" } };
	}

	@Test
	public void shouldCreateSchemaForModelWithArrayFields() throws Exception {
		Object[][] types = getArrayTypes();
		for (Object[] typeTuple : types) {
			givenModelWithEntityWithField((Type) typeTuple[0]);
			whenCreateJsonSchema();
			thenJsonWithEntityWithArrayFieldGenerated((String) typeTuple[1]);
		}
	}

	private void thenJsonWithEntityWithArrayFieldGenerated(String typeName)
			throws Exception {
		thenEntityWithArrayFieldGenerated("requiredField", typeName, true);
		thenEntityWithArrayFieldGenerated("notRequiredField", typeName, false);
	}

	@SuppressWarnings("unchecked")
	private void thenEntityWithArrayFieldGenerated(String fieldName,
			String typeName, boolean required) throws Exception {
		Map<String, Object> property = getProperty(fieldName);
		assertEquals(required, (Boolean) property.get("required"));
		assertEquals("array", property.get("type"));
		Map<String, String> items = (Map<String, String>) property.get("items");
		assertEquals(typeName, items.get("type"));
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getProperty(String propertyName)
			throws Exception {
		Map<String, Object> e1Data = getE1Data();
		Map<String, Map<String, Object>> properties = (Map<String, Map<String, Object>>) e1Data
				.get("properties");

		return properties.get(propertyName);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getE1Data() throws IOException,
			JsonParseException, JsonMappingException {
		Map<String, Object> e1Data = mapper.readValue(
				resultSchemas.get(ENTITY_NAME), Map.class);
		return e1Data;
	}

	@Test
	public void shouldCreateSchemaWithOneToOneRelation() throws Exception {
		givenModelWithOneToOneRelation();
		whenCreateJsonSchema();
		thenOneToOneRelationCreated();
	}

	private void givenModelWithOneToOneRelation() {
		Entity e1 = new Entity();
		e1.setName(ENTITY_NAME);
		
		Entity e2 = new Entity();
		e2.setName("e2");

		createOneToOneRelation(e1, e2);
		model.setEntities(Arrays.asList(e1, e2));
	}

	private void createOneToOneRelation(Entity e1, Entity e2) {
		Field relation = new Field();
		relation.setName(ONE_TO_ONE_RELATION);
		relation.setType(Type.ObjectId);
		relation.setTarget(e2);
		relation.setRequired(true);
		e1.getFields().add(relation);

		e1.getFields().add(relation);
	}

	
	
	private void thenOneToOneRelationCreated() throws Exception {
		thenRelationIsCreated(ONE_TO_ONE_RELATION, "e2");
	}

	private void thenRelationIsCreated(String fieldName, String target)
			throws Exception {
		thenRelationPropertyCreated(fieldName);
		thenLinkToOneIsCreated(fieldName, target);
	}

	private void thenRelationPropertyCreated(String fieldName) throws Exception {
		Map<String, Object> property = getProperty(fieldName);
		assertNotNull(property);
		assertEquals("string", property.get("type"));
		assertTrue((Boolean) property.get("required"));
	}

	private void thenLinkToOneIsCreated(String fieldName, String target)
			throws Exception {
		Map<String, String> link = getLink(fieldName);

		assertNotNull(link);
		assertEquals(target, link.get("targetSchema"));
		assertEquals(String.format("/%s/{%s}", target, fieldName),
				link.get("href"));
	}

	@SuppressWarnings("unchecked")
	private Map<String, String> getLink(String linkName) throws Exception {
		Map<String, Object> e1Data = getE1Data();
		List<Map<String, String>> links = (List<Map<String, String>>) e1Data
				.get("links");
		for (Map<String, String> link : links) {
			if (link.get("rel").equals(linkName)) {
				return link;
			}
		}
		return null;
	}

	@Test
	public void shouldGenerateSchemasWithFileField() throws Exception {
		givenModelWithFileField();
		whenCreateJsonSchema();
		thenFileRelationCreated();
	}

	private void givenModelWithFileField() {
		Entity e1 = new Entity();
		e1.setName(ENTITY_NAME);

		createFileField(e1);

		model.getEntities().add(e1);
	}

	private void createFileField(Entity e) {
		Field fileField = new Field();
		fileField.setName("fileField");
		fileField.setType(Type.File);
		fileField.setRequired(true);
		e.getFields().add(fileField);
	}

	private void thenFileRelationCreated() throws Exception {
		thenRelationIsCreated("fileField", "file");
	}

	@Test
	public void shouldGenerateSchemasWithOneToManyRelationField()
			throws Exception {
		givenModelWithOneToManyRelation();
		whenCreateJsonSchema();
		thenOneToManyRelationCreated();
	}
	
	private void givenModelWithOneToManyRelation() {
		Entity e1 = new Entity();
		e1.setName(ENTITY_NAME);

		Entity e2 = new Entity();
		e2.setName("e2");
		
		createOneToManyRelation(e1, e2);

		model.setEntities(Arrays.asList(e1, e2));
	}

	private void createOneToManyRelation(Entity e1, Entity e2) {
		Field oneToMany = new Field();
		oneToMany.setName(ONE_TO_MANY_FIELD);
		oneToMany.setType(Type.ObjectIdArray);
		oneToMany.setTarget(e2);
		oneToMany.setRequired(true);
		e1.getFields().add(oneToMany);
	}

	private void thenOneToManyRelationCreated() throws Exception {
		thenEntityWithArrayFieldGenerated(ONE_TO_MANY_FIELD, "string", true);
		thenLinkToManyIsCreated(ONE_TO_MANY_FIELD, "e2");
	}
	
	private void thenLinkToManyIsCreated(String fieldName, String target)
			throws Exception {
		Map<String, String> link = getLink(fieldName);

		assertNotNull(link);
		assertEquals(target, link.get("targetSchema"));
		assertEquals(String.format("/%s?ids={%s}", target, fieldName),
				link.get("href"));
	}
	
	@Test
	public void shouldGenerateSchemaWithManyRelations() throws Exception {
		 givenAllTypesOfRelations();
		 whenCreateJsonSchema();
		 thenAllTypesOfRelationsCreated();
	}

	private void givenAllTypesOfRelations() {
		Entity e1 = new Entity();
		e1.setName(ENTITY_NAME);

		Entity e2 = new Entity();
		e2.setName("e2");
		
		createFileField(e1);
		createOneToOneRelation(e1, e2);
		createOneToManyRelation(e1, e2);
		
		model.setEntities(Arrays.asList(e1, e2));
	}

	private void thenAllTypesOfRelationsCreated() throws Exception {
		thenFileRelationCreated();
		thenOneToOneRelationCreated();
		thenOneToManyRelationCreated();
	}
}
