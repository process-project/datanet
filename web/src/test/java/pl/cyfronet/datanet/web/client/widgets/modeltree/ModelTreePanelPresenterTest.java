package pl.cyfronet.datanet.web.client.widgets.modeltree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pl.cyfronet.datanet.test.mock.answer.CallbackAnswer.modelCallbackAnswer;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import pl.cyfronet.datanet.model.beans.Model;
import pl.cyfronet.datanet.model.beans.Repository;
import pl.cyfronet.datanet.model.beans.Version;
import pl.cyfronet.datanet.test.mock.matcher.ModelPlaceMatcher;
import pl.cyfronet.datanet.test.mock.matcher.TreeItemMatcher;
import pl.cyfronet.datanet.web.client.controller.RepositoryController;
import pl.cyfronet.datanet.web.client.controller.RepositoryController.RepositoryCallback;
import pl.cyfronet.datanet.web.client.controller.VersionController;
import pl.cyfronet.datanet.web.client.controller.VersionController.VersionCallback;
import pl.cyfronet.datanet.web.client.event.model.ModelChangedEvent;
import pl.cyfronet.datanet.web.client.event.model.NewModelEvent;
import pl.cyfronet.datanet.web.client.model.ModelController;
import pl.cyfronet.datanet.web.client.model.ModelController.ModelCallback;
import pl.cyfronet.datanet.web.client.model.ModelController.ModelsCallback;
import pl.cyfronet.datanet.web.client.model.ModelProxy;
import pl.cyfronet.datanet.web.client.widgets.modeltree.ModelTreePanelPresenter.View;
import pl.cyfronet.datanet.web.client.widgets.modeltree.Presenter.TreeItemCallback;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.web.bindery.event.shared.EventBus;

@RunWith(GwtMockitoTestRunner.class)
public class ModelTreePanelPresenterTest {
	@Mock private View view;
	@Mock private ModelController modelController;
	@Mock private VersionController versionController;
	@Mock private RepositoryController repositoryController;
	@Mock private PlaceController placeController;
	@Mock private EventBus eventBus;

	private ModelTreePanelPresenter presenter;
	private ModelProxy newModel;
	private long selectedModelId = 1l;
	private Model m1;
	private Model m2;
	private Model m3;

	@Before
	public void prepare() {
		MockitoAnnotations.initMocks(this);
		presenter = new ModelTreePanelPresenter(view, modelController, versionController,
				repositoryController, placeController, eventBus);

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
						.getArguments()[1];
				newModel = new ModelProxy(new Model(), System
						.currentTimeMillis());
				callback.setModel(newModel);
				return null;
			}
		}).when(modelController).createNewModel(any(String.class), any(ModelCallback.class));
	}

	private void whenAddingNewModel() {
		presenter.onAddNewModel();
	}

	private void thenNewModelAddedAndOpened() {
		verify(modelController, times(1)).createNewModel(any(String.class),
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
				TreeItem.newModel(selectedModelId, null));
	}

	private void whenSelectingModel() {
		presenter.onSelected();
	}

	private void thenModelOpened() {
		verify(placeController, times(1)).goTo(
				argThat(new ModelPlaceMatcher(selectedModelId)));
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

	@Test
	public void shouldSelectCleanModel() throws Exception {
		ModelProxy model = givenModel(false, false);
		whenUserClicksOnModel(model.getId());
		thenModelSelected(model.getId(), false);
	}

	private ModelProxy givenModel(boolean newModel, boolean dirty) {
		ModelProxy proxy;
		if (newModel) {
			proxy = new ModelProxy(m1, System.currentTimeMillis());
		} else {
			proxy = new ModelProxy(m1);
		}
		proxy.setDirty(dirty);

		doAnswer(modelCallbackAnswer(1, proxy)).when(modelController).getModel(
				eq(proxy.getId()), any(ModelCallback.class));

		return proxy;
	}

	private void whenUserClicksOnModel(Long modelId) {
		presenter.setSelected(TreeItem.newModel(modelId));
	}

	private void thenModelSelected(Long modelId,
			boolean saveEnabled) {
		verify(view, times(1)).setSelected(eq(TreeItem.newModel(modelId)));
	}

	@Test
	public void shouldSelectDirtyModel() throws Exception {
		ModelProxy model = givenModel(false, true);
		whenUserClicksOnModel(model.getId());
		thenModelSelected(model.getId(), true);
	}

	@Test
	public void shouldSelectNewModel() throws Exception {
		ModelProxy model = givenModel(true, true);
		whenUserClicksOnModel(model.getId());
		thenModelSelected(model.getId(), true);
	}

	@Test
	public void shouldSelectEmptyItem() throws Exception {
		whenSelectEmptyItem();
		thenNoItemSelected();
	}

	private void whenSelectEmptyItem() {
		presenter.setSelected(null);
	}

	private void thenNoItemSelected() {
		verify(view, times(1)).setSelected(null);
	}

	@Test
	public void shouldReloadModelsOnModelChanged() throws Exception {
		givenModelsToReload();
		whenModelChangedEventFired();
		thenModelsRefreshedAndModelSelected();
	}

	private void givenModelsToReload() {
		ModelProxy m1proxy = new ModelProxy(m1);
		givenRefresh(m1proxy, new ModelProxy(m2));

		doAnswer(modelCallbackAnswer(1, m1proxy)).when(modelController)
				.getModel(eq(m1proxy.getId()), any(ModelCallback.class));
	}

	private void whenModelChangedEventFired() {
		presenter.onModelChanged(new ModelChangedEvent(m1.getId()));
	}

	private void thenModelsRefreshedAndModelSelected() {
		verify(view, times(1)).setModels(
				argThat(new TreeItemMatcher(TreeItem.newModel(m1.getId()),
						TreeItem.newModel(m2.getId()))));
		verify(view, times(1)).setSelected(TreeItem.newModel(m1.getId()));
	}

	@Test
	public void shouldReloadModelsOnNewModel() throws Exception {
		givenModelsToReload();
		whenNewModelEventFired();
		thenModelsRefreshedAndModelSelected();
	}

	private void whenNewModelEventFired() {
		presenter.onNewModel(new NewModelEvent(m1.getId()));
	}
	
	@Test
	public void shouldLoadModels() throws Exception {
		 givenModelsToReload();
		 whenLoadModels();
		 thenModelsRefreshed();
	}

	private void whenLoadModels() {
		presenter.loadChildren(null);
	}

	private void thenModelsRefreshed() {
		verify(view, times(1)).setModels(
				argThat(new TreeItemMatcher(TreeItem.newModel(m1.getId()),
						TreeItem.newModel(m2.getId()))));		
	}
	
	@Test
	public void shouldGetNullForModelParent() throws Exception {
		 presenter.getParent(TreeItem.newModel(1l), new TreeItemCallback() {
			@Override
			public void onTreeItemProvided(TreeItem item) {
				assertNull(item);
			}
		});
	}
	
	@Test
	public void shouldGetModelForVersionParent() throws Exception {
		doAnswer(new Answer<Void>() {

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				VersionCallback callback = (VersionCallback)invocation.getArguments()[1];
				Version version = new Version();
				version.setId(1l);
				callback.setVersion(version);
				
				return null;
			}
			
		}).when(versionController).getVersion(eq(1l), any(VersionCallback.class));
		
		presenter.getParent(TreeItem.newVersion(1l), new TreeItemCallback() {
			@Override
			public void onTreeItemProvided(TreeItem item) {
				assertEquals(item.getType(), ItemType.MODEL);
			}
		});
	}
	
	@Test
	public void shouldGetVersionForRepositoryParent() throws Exception {
		doAnswer(new Answer<Void>() {

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				RepositoryCallback callback = (RepositoryCallback)invocation.getArguments()[1];
				Repository repository = new Repository();				
				repository.setId(1l);				

				Version version = new Version();
				version.setId(1l);
				repository.setSourceModelVersion(version);
				
				callback.setRepository(repository);
				
				return null;
			}
			
		}).when(repositoryController).getRepository(eq(1l), any(RepositoryCallback.class));
		
		presenter.getParent(TreeItem.newRepository(1l), new TreeItemCallback() {
			@Override
			public void onTreeItemProvided(TreeItem item) {
				assertEquals(item.getType(), ItemType.VERSION);
			}
		});
	}
}
