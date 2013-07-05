package pl.cyfronet.datanet.web.client.widgets.modeltree;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pl.cyfronet.datanet.test.mock.answer.CallbackAnswer.nextCallbackAnswer;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import pl.cyfronet.datanet.model.beans.Model;
import pl.cyfronet.datanet.test.mock.matcher.ModelPlaceMatcher;
import pl.cyfronet.datanet.test.mock.matcher.TreeItemMatcher;
import pl.cyfronet.datanet.web.client.callback.NextCallback;
import pl.cyfronet.datanet.web.client.model.ModelController;
import pl.cyfronet.datanet.web.client.model.ModelController.ModelCallback;
import pl.cyfronet.datanet.web.client.model.ModelController.ModelsCallback;
import pl.cyfronet.datanet.web.client.model.ModelProxy;
import pl.cyfronet.datanet.web.client.mvp.place.WelcomePlace;
import pl.cyfronet.datanet.web.client.widgets.modeltree.ModelTreePanelPresenter.View;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.web.bindery.event.shared.EventBus;

@RunWith(GwtMockitoTestRunner.class)
public class ModelTreePanelPresenterTest {

	@Mock
	private View view;

	@Mock
	private ModelController modelController;

	@Mock
	private PlaceController placeController;

	@Mock
	private EventBus eventBus;

	private ModelTreePanelPresenter presenter;

	private ModelProxy newModel;

	private long selectedModelId = 1l;

	private Model m1;

	private Model m2;

	private Model m3;

	@Before
	public void prepare() {
		MockitoAnnotations.initMocks(this);
		presenter = new ModelTreePanelPresenter(view, modelController,
				placeController, eventBus);

		m1 = new Model();
		m1.setId(1);
		m1.setName("m1");

		m2 = new Model();
		m2.setId(2);
		m2.setName("m2");
		
		m3 = new Model();
		m3.setId(3);
		m3.setName("m3");
	}

	@Test
	public void shouldCreateNewModel() throws Exception {
		givenCreateModelCapability();
		whenAddingNewModel();
		thenNewModelAddedAndOpened();

	}

	private void givenCreateModelCapability() {
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				ModelCallback callback = (ModelCallback) invocation
						.getArguments()[0];
				newModel = new ModelProxy(new Model(), System
						.currentTimeMillis());
				callback.setModel(newModel);
				return null;
			}
		}).when(modelController).createNewModel(any(ModelCallback.class));
	}

	private void whenAddingNewModel() {
		presenter.onAddNewModel();
	}

	private void thenNewModelAddedAndOpened() {
		verify(modelController, times(1)).createNewModel(
				any(ModelCallback.class));
		verify(placeController, times(1)).goTo(
				argThat(new ModelPlaceMatcher(newModel.getId())));
	}

	@Test
	public void shouldOpenModel() throws Exception {
		givenModelSelectedByTheUser();
		whenSelectingModel();
		thenModelOpened();
	}

	private void givenModelSelectedByTheUser() {
		when(view.getSelectedObject()).thenReturn(
				new TreeItem(selectedModelId, null, ItemType.MODEL));
	}

	private void whenSelectingModel() {
		presenter.onSelected();
	}

	private void thenModelOpened() {
		verify(placeController, times(1)).goTo(
				argThat(new ModelPlaceMatcher(selectedModelId)));
	}

	@Test
	public void shouldRemoveModel() throws Exception {
		givenModelToRemove();
		whenRemoveModel();
		thenModelRemovedFromCacheAndServer();
	}

	private void givenModelToRemove() {
		givenSelectedModel(m1.getId());
		doAnswer(nextCallbackAnswer(1)).when(modelController).deleteModel(
				eq(m1.getId()), any(NextCallback.class));
		givenRefresh(new ModelProxy(m2));
	}

	private void givenRefresh(final ModelProxy... models) {
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				ModelsCallback callback = (ModelsCallback) invocation
						.getArguments()[0];
				callback.setModels(Arrays.asList(models));
				return null;
			}
		}).when(modelController)
				.getModels(any(ModelsCallback.class), eq(false));
	}

	private void givenSelectedModel(Long modelId) {
		when(view.getSelectedObject()).thenReturn(TreeItem.model(modelId));
	}

	private void whenRemoveModel() {
		presenter.onRemove();
	}

	private void thenModelRemovedFromCacheAndServer() {
		verify(modelController, times(1)).deleteModel(eq(m1.getId()),
				any(NextCallback.class));
		verify(view, times(1)).setModels(
				argThat(new TreeItemMatcher(TreeItem.model(m2.getId()))));
		verify(placeController, times(1)).goTo(any(WelcomePlace.class));
	}

	@Test
	public void shouldSaveModel() throws Exception {
		givenModelToSave();
		whenModelSaved();
		thenModelListRefreshed();
	}

	private void givenModelToSave() {
		givenSelectedModel(m1.getId());
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				ModelCallback callback = (ModelCallback) invocation
						.getArguments()[1];
				callback.setModel(new ModelProxy(m1));
				return null;
			}
		}).when(modelController).saveModel(eq(m1.getId()),
				any(ModelCallback.class));
		givenRefresh(new ModelProxy(m1), new ModelProxy(m2));
	}

	private void whenModelSaved() {
		presenter.onSave();
	}

	private void thenModelListRefreshed() {
		verify(modelController, times(1)).saveModel(eq(m1.getId()),
				any(ModelCallback.class));
		verify(view, times(1)).setModels(
				argThat(new TreeItemMatcher(TreeItem.model(m1.getId()),
						TreeItem.model(m2.getId()))));
		verify(placeController, times(0)).goTo(any(Place.class));
	}
	
	@Test
	public void shouldSaveNewModel() throws Exception {
		 givenNewModel();
		 whenModelSaved();
		 thenModelSavedAndOpened();
	}

	private void givenNewModel() {
		givenSelectedModel(m1.getId());
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				ModelCallback callback = (ModelCallback) invocation
						.getArguments()[1];
				callback.setModel(new ModelProxy(m3));
				return null;
			}
		}).when(modelController).saveModel(eq(m1.getId()),
				any(ModelCallback.class));
		givenRefresh(new ModelProxy(m3), new ModelProxy(m2));
	}

	private void thenModelSavedAndOpened() {
		verify(modelController, times(1)).saveModel(eq(m1.getId()),
				any(ModelCallback.class));
		verify(view, times(1)).setModels(
				argThat(new TreeItemMatcher(TreeItem.model(m3.getId()),
						TreeItem.model(m2.getId()))));
		verify(placeController, times(1)).goTo(any(Place.class));
	}
}
