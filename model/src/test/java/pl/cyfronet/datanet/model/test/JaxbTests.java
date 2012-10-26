package pl.cyfronet.datanet.model.test;

import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pl.cyfronet.datanet.model.beans.Model;
import pl.cyfronet.datanet.model.util.ModelBuilder;

public class JaxbTests {
	private ModelBuilder modelBuilder;
	
	@Before
	public void prepare() {
		modelBuilder = new ModelBuilder();
	}
	
	@Test
	public void testSerialization() throws JAXBException {
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
}