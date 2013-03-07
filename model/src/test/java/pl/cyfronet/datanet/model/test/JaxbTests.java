package pl.cyfronet.datanet.model.test;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pl.cyfronet.datanet.model.beans.Entity;
import pl.cyfronet.datanet.model.beans.Model;
import pl.cyfronet.datanet.model.util.JaxbEntityListBuilder;
import pl.cyfronet.datanet.model.util.ModelBuilder;

public class JaxbTests {
	private ModelBuilder modelBuilder;
	private JaxbEntityListBuilder entitiesListBuilder;
	
	@Before
	public void prepare() {
		modelBuilder = new ModelBuilder();
		entitiesListBuilder = new JaxbEntityListBuilder();
	}
	
	@Test
	public void testSerializationOfModel() throws JAXBException {
		String modelName = "Test name";
		String modelVersion = "1.0";
		Model model = new Model();
		model.setName(modelName);
		model.setVersion(modelVersion);
		
		String document = modelBuilder.serialize(model);
		Model result = modelBuilder.deserialize(document);
		Assert.assertEquals(modelName, result.getName());
		Assert.assertEquals(modelVersion, result.getVersion());
	}
	
	@Test
	public void testSerializationOfEntities() throws JAXBException {
		List<Entity> list = new LinkedList<Entity>();
		
		Entity e1 = new Entity();
		e1.getFields().get(0).setName("wafle");
		list.add(e1);
		Entity e2 = new Entity();
		e2.getFields().get(0).setName("wafle2");
		list.add(e2);
		
		
		String document = entitiesListBuilder.serialize(list);
		List<Entity> result = entitiesListBuilder.deserialize(document);
		
		Assert.assertEquals(list, result);
	}
}