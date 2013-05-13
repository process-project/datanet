package pl.cyfronet.datanet.deployer.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import pl.cyfronet.datanet.deployer.marshaller.EntityMarshaller;
import pl.cyfronet.datanet.deployer.marshaller.MarshallerException;
import pl.cyfronet.datanet.model.beans.Entity;
import pl.cyfronet.datanet.model.beans.Field;
import pl.cyfronet.datanet.model.beans.Model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class MarshallerTest {

	private Model generateModel() {
		// model
		Model model = new Model();
		model.setName("ble");
		List<Entity> entities = new ArrayList<Entity>();
		model.setEntities(entities);

		// entity1
		Entity entity = new Entity();
		entity.setName("byt");

		List<Field> fields = new ArrayList<Field>();

		Field field1 = new Field();
		field1.setType(Field.Type.String);
		field1.setName("nazwa");
		Field field2 = new Field();
		field2.setType(Field.Type.Integer);
		field2.setName("ilosc");
		Field field3 = new Field();
		field3.setType(Field.Type.StringArray);
		field3.setName("tagi");
		field3.setRequired(false);
		Field field4 = new Field();
		field4.setType(Field.Type.File);
		field4.setName("plik");
		field4.setRequired(true);
		Field field5 = new Field();
		field5.setType(Field.Type.File);
		field5.setName("pliksss");
		field5.setRequired(true);

		fields.add(field1);
		fields.add(field2);
		fields.add(field3);
		fields.add(field4);
		fields.add(field5);

		entity.setFields(fields);
		model.getEntities().add(entity);

		// entity2
		entity = new Entity();
		entity.setName("drugi");

		fields = new ArrayList<Field>();

		field1 = new Field();
		field1.setName("wafle");
		field1.setType(Field.Type.String);

		fields.add(field1);

		entity.setFields(fields);
		model.getEntities().add(entity);

		return model;
	}

	@Test
	public void generateJson() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode oNode = mapper.createObjectNode();
		oNode.put("type", "object");

		ObjectNode object = oNode.putObject("properties");

		ObjectNode object1 = object.putObject("name");
		object1.put("type", "string");
		object1.put("required", true);

		ObjectNode object2 = object.putObject("type");
		object2.put("type", "string");
		object2.put("required", true);

		ObjectNode object3 = object.putObject("count");
		object3.put("type", "integer");
		object3.put("required", false);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectWriter ow = mapper.writerWithDefaultPrettyPrinter();
		ow.writeValue(baos, oNode);
		Assert.assertTrue(!ow.toString().isEmpty());
	}

	@Test
	public void marshallEntity() throws MarshallerException {
		Entity entity = generateModel().getEntities().get(0);
		EntityMarshaller m = new EntityMarshaller(true);
		String result = m.marshall(entity);
		System.out.println(result);
		Assert.assertFalse(result.isEmpty());
	}
}
