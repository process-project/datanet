package pl.cyfronet.datanet.test.db;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import pl.cyfronet.datanet.web.server.config.SpringConfiguration;
import pl.cyfronet.datanet.web.server.db.HibernateModelDao;
import pl.cyfronet.datanet.web.server.db.beans.ModelDbEntity;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class,
		classes = SpringConfiguration.class)
public class ModelTest {
	@Autowired HibernateModelDao modelDao;
	
	@Test
	public void testModelDao() {
		String modelName = "Model 1";
		ModelDbEntity model = new ModelDbEntity();
		model.setName(modelName);
		modelDao.saveModel(model);
		Assert.assertTrue(model.getId() > 0);
		
		ModelDbEntity retrievedModel = modelDao.getModel(model.getId());
		Assert.assertEquals(modelName, retrievedModel.getName());
	}
}