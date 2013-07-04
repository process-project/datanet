package pl.cyfronet.datanet.web.client.model;

import static com.google.gwtmockito.AsyncAnswers.returnFailure;
import static com.google.gwtmockito.AsyncAnswers.returnSuccess;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import pl.cyfronet.datanet.model.beans.Entity;
import pl.cyfronet.datanet.model.beans.Field;
import pl.cyfronet.datanet.model.beans.Field.Type;
import pl.cyfronet.datanet.model.beans.Model;
import pl.cyfronet.datanet.model.beans.validator.ModelValidator;
import pl.cyfronet.datanet.model.beans.validator.ModelValidator.ModelError;
import pl.cyfronet.datanet.test.mock.matcher.NotificationEvenMatcher;
import pl.cyfronet.datanet.web.client.callback.NextCallback;
import pl.cyfronet.datanet.web.client.event.model.NewModelEvent;
import pl.cyfronet.datanet.web.client.event.notification.NotificationEvent.NotificationType;
import pl.cyfronet.datanet.web.client.model.ModelController;
import pl.cyfronet.datanet.web.client.model.ModelController.ModelCallback;
import pl.cyfronet.datanet.web.client.model.ModelController.ModelsCallback;
import pl.cyfronet.datanet.web.client.model.ModelProxy;
import pl.cyfronet.datanet.web.client.services.ModelService;
import pl.cyfronet.datanet.web.client.services.ModelServiceAsync;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.web.bindery.event.shared.EventBus;

@RunWith(GwtMockitoTestRunner.class)
@SuppressWarnings("unchecked")
public class ModelControllerTest {

	@GwtMock
	private ModelServiceAsync modelService;

	@Mock
	private ModelValidator modelValidator;

	@Mock
	private EventBus eventBus;

	private ModelController modelController;

	private List<ModelProxy> models;

	private ModelProxy model;

	private ModelProxy newModel;

	private ModelProxy savedModel;

	private Model m1;

	private Model m2;

	private boolean modelDeleted;

	@Before
	public void prepare() {
		MockitoAnnotations.initMocks(this);
		ModelServiceAsync service = GWT.create(ModelService.class);
		modelController = new ModelController(service, modelValidator, eventBus);

		m1 = new Model();
		m1.setId(1);
		m1.setName("m1");

		m2 = new Model();
		m2.setId(2);
		m2.setName("m2");
	}

	@Test
	public void shouldLoadModelOnce() throws Exception {
		given2Models();
		whenLoadModelsTwice();
		thenModelLoadedFromServerOnce();
	}

	private void whenLoadModelsTwice() {
		for (int i = 0; i < 2; i++) {
			whenLoadModels();
		}
	}

	private void whenLoadModels() {
		modelController.getModels(new ModelsCallback() {
			@Override
			public void setModels(List<ModelProxy> models) {
				ModelControllerTest.this.models = models;
			}
		}, false);
	}

	private void given2Models() {
		doAnswer(returnSuccess(Arrays.asList(m1, m2))).when(modelService)
				.getModels(any(AsyncCallback.class));
	}

	private void thenModelLoadedFromServerOnce() {
		assertEquals(2, models.size());
		verify(modelService, times(1)).getModels(any(AsyncCallback.class));
	}

	@Test
	public void shouldFireNotificationEventOnLoadModelFailure()
			throws Exception {
		givenModelLoadFailure();
		whenLoadModels();
		thenErrorNotificationEventFired();
	}

	private void givenModelLoadFailure() {
		doAnswer(returnFailure(new Exception())).when(modelService).getModels(
				any(AsyncCallback.class));
	}

	private void thenErrorNotificationEventFired() {
		verify(eventBus, times(1)).fireEvent(
				argThat(new NotificationEvenMatcher(NotificationType.ERROR)));
	}

	@Test
	public void loadModelWhenModelsNotLoaded() throws Exception {
		given2Models();
		whenGetModel();
		thenModelLoaded();
	}

	private void whenGetModel() {
		whenGetModel(m1.getId());
	}

	private void whenGetModel(Long id) {
		modelController.getModel(id, new ModelCallback() {
			@Override
			public void setModel(ModelProxy model) {
				ModelControllerTest.this.model = model;
			}
		});
	}

	private void thenModelLoaded() {
		assertFalse(model.isDirty());
		assertFalse(model.isNew());
		assertEquals(m1.getId(), model.getId());
		assertEquals(m1.getName(), model.getName());
	}

	@Test
	public void shouldGetModelFromCaseIfModelsLoaded() throws Exception {
		given2Models();
		whenLoadModels();
		whenGetModel();
		thenModelLoadedFromCache();
	}

	private void thenModelLoadedFromCache() {
		verify(modelService, times(1)).getModels(any(AsyncCallback.class));
		verify(modelService, times(0)).getModel(anyLong(),
				any(AsyncCallback.class));
	}

	@Test
	public void shouldCreateNewModel() throws Exception {
		given2Models();
		whenCreateNewModel();
		thanNewModelCreatedAndEvenFired();
	}

	private void whenCreateNewModel() {
		modelController.createNewModel(new ModelCallback() {
			@Override
			public void setModel(ModelProxy model) {
				newModel = model;
			}
		});
	}

	private void thanNewModelCreatedAndEvenFired() {
		whenGetNewModel();
		assertEquals(model.getId(), newModel.getId());
		assertEquals(model.getName(), newModel.getName());

		verify(eventBus, times(1)).fireEvent(any(NewModelEvent.class));
	}

	private void whenGetNewModel() {
		whenGetModel(newModel.getId());
	}

	@Test
	public void shouldSaveNewModel() throws Exception {
		givenNewModel();
		whenSaveModel();
		thanModelSaved();
	}

	private void givenNewModel() {
		given2Models();
		whenCreateNewModel();

		newModel.setName("newModel");
		Entity e1 = new Entity();
		e1.setName("user");

		Field f1 = new Field();
		f1.setName("first_name");
		f1.setType(Type.String);

		e1.setFields(Arrays.asList(f1));
		newModel.setEntities(Arrays.asList(e1));

		Model m = new Model();
		m.setId(3);
		m.setName(newModel.getName());
		doAnswer(returnSuccess(m)).when(modelService).saveModel(
				any(Model.class), any(AsyncCallback.class));
	}

	private void whenSaveModel() {
		modelController.saveModel(newModel.getId(), new ModelCallback() {
			@Override
			public void setModel(ModelProxy model) {
				savedModel = model;
			}
		});
	}

	private void thanModelSaved() {
		whenGetModel(savedModel.getId());
		assertEquals(newModel.getName(), model.getName());
	}

	@Test
	public void shouldFireErrorNotificationWhenSavingNotValidModel()
			throws Exception {
		givenNewModelWithErrors();
		whenSaveModel();
		thenErrorNotificationEventFired();
	}

	private void givenNewModelWithErrors() {
		givenNewModel();
		when(modelValidator.validateModel(any(Model.class))).thenReturn(
				Arrays.asList(ModelError.EMPTY_MODEL_NAME));
	}

	@Test
	public void shouldDeleteModel() throws Exception {
		givenModelToDelete();
		whenDeleteSavedModel();
		thanModelDeletedFromServer();
	}

	private void givenModelToDelete() {
		given2Models();
		doAnswer(returnSuccess(null)).when(modelService).deleteModel(
				eq(m1.getId()), any(AsyncCallback.class));
	}

	private void whenDeleteSavedModel() {
		whenDeleteModel(m1.getId());
	}

	private void whenDeleteModel(Long id) {
		modelDeleted = false;
		modelController.deleteModel(id, new NextCallback() {
			@Override
			public void next() {
				modelDeleted = true;
			}
		});
	}
	
	private void thanModelDeletedFromServer() {
		assertTrue(modelDeleted);
		verify(modelService, times(1)).deleteModel(eq(m1.getId()),
				any(AsyncCallback.class));
	}
	
	@Test
	public void shouldDeleteNewModelWithoutServerInvolment() throws Exception {
		givenNewModel();
		whenDeleteNewModel();
		thanModelDeletedAndNoServerInvolved();
	}	

	private void whenDeleteNewModel() {
		whenDeleteModel(newModel.getId());
	}
	
	private void thanModelDeletedAndNoServerInvolved() {
		assertTrue(modelDeleted);
		verify(modelService, times(0)).deleteModel(anyLong(),
				any(AsyncCallback.class));
	}
	
	@Test
	public void shouldFireErrorNotificationWhenDeleteFailed() throws Exception {
		givenDeleteWithError();
		whenDeleteSavedModel();
		thenErrorNotificationEventFired();
	}

	private void givenDeleteWithError() {
		given2Models();
		doAnswer(returnFailure(new Exception())).when(modelService).deleteModel(
				eq(m1.getId()), any(AsyncCallback.class));
	}
}
