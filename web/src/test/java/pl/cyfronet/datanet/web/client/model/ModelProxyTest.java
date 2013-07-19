package pl.cyfronet.datanet.web.client.model;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import pl.cyfronet.datanet.model.beans.Model;

public class ModelProxyTest {

	private List<ModelProxy> models;
	private ModelProxy model1;
	private ModelProxy model2;
	private ModelProxy model3;
	
	@Test
	public void shouldRemoveNewModelFromList() throws Exception {
		 givenListWith3NewModels();
		 whenRemoveModel();
		 thenModelRemoved();
	}

	private void givenListWith3NewModels() {		
		model1 = new ModelProxy(new Model(), 1l);
		model2 = new ModelProxy(new Model(), 2l);
		model3 = new ModelProxy(new Model(), 3l);
		
		createList();		
	}
	
	

	private void createList() {
		models = new ArrayList<>();
		models.add(model1);
		models.add(model2);
		models.add(model3);
	}

	private void whenRemoveModel() {
		models.remove(model2);
	}

	private void thenModelRemoved() {
		assertEquals(2, models.size());		
		assertEquals(model1.getId(), models.get(0).getId());
		assertEquals(model3.getId(), models.get(1).getId());
	}
	
	@Test
	public void shouldRemoveModel() throws Exception {
		 givenListWith3Models();
		 whenRemoveModel();
		 thenModelRemoved();
	}

	private void givenListWith3Models() {
		Model m1 = new Model();
		m1.setId(1);
		
		Model m2 = new Model();
		m2.setId(2);
		
		Model m3 = new Model();
		m3.setId(3);
		
		model1 = new ModelProxy(m1);
		model2 = new ModelProxy(m2);
		model3 = new ModelProxy(m3);
		
		createList();
	}
}
