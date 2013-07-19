package pl.cyfronet.datanet.model.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import pl.cyfronet.datanet.model.beans.Entity;
import pl.cyfronet.datanet.model.beans.Model;
import pl.cyfronet.datanet.model.beans.validator.ModelValidator;
import pl.cyfronet.datanet.model.beans.validator.ModelValidator.ModelError;

public class ModelValidatorTest {
	private ModelValidator validator;
	
	@Before
	public void prepare() {
		validator = new ModelValidator();
	}
	
	@Test
	public void testModelNull() {
		List<ModelError> errors = validator.validateModel(null);
		Assert.assertTrue(errors.size() == 1);
		Assert.assertEquals(ModelError.NULL_MODEL, errors.get(0));
	}
	
	@Test
	public void testEmptyModelName() {
		for (String name : new String[] {"", " "}) {
			Model model = new Model();
			model.setName(name);
			
			List<ModelError> errors = validator.validateModel(model);
			Assert.assertTrue(errors.contains(ModelError.EMPTY_MODEL_NAME));
		}
	}
	
	@Test
	public void testValidModelName() {
		for (String name : new String[] {"Model1", "ModelCamelCase"}) {
			Model model = new Model();
			model.setName(name);
			
			List<ModelError> errors = validator.validateModel(model);
			Assert.assertTrue(!errors.contains(ModelError.EMPTY_MODEL_NAME));
			Assert.assertTrue(!errors.contains(ModelError.INVALID_CHARS_MODEL_NAME));
		}
	}
	
	@Test
	public void testInvalidModelName() {
		for (String name : new String[] {"Model 1", "123ModelCamelCase", "NameWith!"}) {
			Model model = new Model();
			model.setName(name);
			
			List<ModelError> errors = validator.validateModel(model);
			Assert.assertTrue(errors.contains(ModelError.INVALID_CHARS_MODEL_NAME));
		}
	}
	
	@Test
	public void shouldNotAllowEmptyEntitiesList() throws Exception {
		Model model = new Model();
		model.setName("name");
		
		//null entities list
		List<ModelError> errors = validator.validateModel(model);
		Assert.assertTrue(errors.contains(ModelError.EMPTY_ENTITIES_LIST));
		
		// empty list
		model.setEntities(new ArrayList<Entity>());
		errors = validator.validateModel(model);
		Assert.assertTrue(errors.contains(ModelError.EMPTY_ENTITIES_LIST));
	}
}