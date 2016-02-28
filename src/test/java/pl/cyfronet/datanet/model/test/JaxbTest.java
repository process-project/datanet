package pl.cyfronet.datanet.model.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pl.cyfronet.datanet.model.beans.Entity;
import pl.cyfronet.datanet.model.beans.Field;
import pl.cyfronet.datanet.model.beans.Model;
import pl.cyfronet.datanet.model.beans.Type;
import pl.cyfronet.datanet.model.util.JaxbEntityListBuilder;
import pl.cyfronet.datanet.model.util.ModelBuilder;

public class JaxbTest {
	private ModelBuilder modelBuilder;
	private JaxbEntityListBuilder entitiesListBuilder;
	private List<Entity> result;
	private List<Entity> list;
	
	@Before
	public void prepare() {
		modelBuilder = new ModelBuilder();
		entitiesListBuilder = new JaxbEntityListBuilder();
	}
	
	@Test
	public void testSerializationOfModel() throws JAXBException {
		String modelName = "Test name";
		Model model = new Model();
		model.setName(modelName);
		
		String document = modelBuilder.serialize(model);
		Model result = modelBuilder.deserialize(document);
		Assert.assertEquals(modelName, result.getName());
	}
	
	@Test
	public void testSerializationOfEntities() throws Exception {
		list = new LinkedList<Entity>();
		
		Entity e1 = new Entity();
		Field f1 = new Field();
		f1.setName("wafle1");
		e1.getFields().add(f1);
		
		Entity e2 = new Entity();
		Field f2 = new Field();
		f2.setName("wafle2");
		e2.getFields().add(f2);
		
		list.add(e1);
		list.add(e2);
		
		
		whenSerializeAndDeserializeEntities();
		
		Assert.assertEquals(list, result);
	}
	
	private void whenSerializeAndDeserializeEntities() throws Exception {
		String document = entitiesListBuilder.serialize(list);
		result = entitiesListBuilder.deserialize(document);		
	}

	@Test
	public void testEntitySerializationWithReferenceField() throws Exception {
		list = new LinkedList<Entity>();
		
		Entity e1 = new Entity();
		e1.setName("e1");
		
		Entity e2 = new Entity();
		e2.setName("e");
		
		Field f1 = new Field();
		f1.setName("wafle1");
		f1.setType(Type.ObjectId);
		f1.setTarget(e1);
		e2.getFields().add(f1);
		
		list.add(e1);
		list.add(e2);
		
		whenSerializeAndDeserializeEntities();
		
		Field field = result.get(1).getFields().get(0);
		assertEquals(field.getTarget(), result.get(0));
		assertTrue(field.getTarget() == result.get(0));
	}
}