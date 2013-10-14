package pl.cyfronet.datanet.web.client.controller;

import static com.google.gwtmockito.AsyncAnswers.returnSuccess;
import static com.google.gwtmockito.AsyncAnswers.returnFailure;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import pl.cyfronet.datanet.model.beans.Version;
import pl.cyfronet.datanet.test.mock.matcher.NotificationEvenMatcher;
import pl.cyfronet.datanet.web.client.controller.VersionController.VersionCallback;
import pl.cyfronet.datanet.web.client.controller.VersionController.VersionsCallback;
import pl.cyfronet.datanet.web.client.event.notification.NotificationEvent.NotificationType;
import pl.cyfronet.datanet.web.client.model.ModelController;
import pl.cyfronet.datanet.web.client.model.ModelController.ModelCallback;
import pl.cyfronet.datanet.web.client.model.ModelProxy;
import pl.cyfronet.datanet.web.client.services.VersionServiceAsync;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.web.bindery.event.shared.EventBus;

@SuppressWarnings("unchecked")
@RunWith(GwtMockitoTestRunner.class)
public class VersionControllerTest {

	@GwtMock
	private VersionServiceAsync versionService;
	
	@Mock
	private ModelController modelController;
	
	@Mock
	private EventBus eventBus;
	
	@Mock
	private PlaceController placeController;

	private VersionController versionController;
	
	private final Long modelId = (long)123;
	
	private final Long nonexistentModelId = (long)666;
	
	private final Long nonexistentVersionId = (long)666;
	
	private String registeredVersionName = "named_version123";
	
	private List<Version> givenVersions;
	
	private Version givenVersion;
	
	private List<Version> returnedVersions;
	
	private Version returnedVersion;

	protected boolean called;

	protected boolean executed;
	
	@Before
	public void prepare() {
		MockitoAnnotations.initMocks(this);
		versionController = new VersionController(versionService, modelController, eventBus, placeController);
		givenVersions = null;
		returnedVersions = null;
		returnedVersion = null;
		givenVersion = null;
		called = false;
	}

	@Test
	public void testGetVersions() {
		givenModelWith2Versions();
		whenGetVersions();
		thenAppropriateVersionsReturned();
	}
	
	private void givenModel() {
		
		final ModelProxy model = Mockito.mock(ModelProxy.class);
		when(model.getId()).thenReturn(modelId);
		when(model.isNew()).thenReturn(false);
		when(model.isDirty()).thenReturn(false);
		
		doAnswer(new Answer<Void>() {

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Object[] arguments = invocation.getArguments();
				((ModelCallback)arguments[1]).setModel(model);
				return null;
			}
			
		}).when(modelController).getModel(eq(modelId), any(ModelCallback.class));
	}
	
	private void givenModelWith2Versions() { 

		givenModel();
		
		givenVersions = new LinkedList<>();
		
		Version version = new Version();
		Long versionId = (long)3214;
		version.setId(versionId);
		version.setModelId(modelId);
		givenVersions.add(version);
		doAnswer(returnSuccess(version)).when(versionService).getVersion(eq(versionId), any(AsyncCallback.class));
		
		versionId = (long)3214;
		version = new Version();
		version.setId(versionId);
		version.setModelId(modelId);
		givenVersions.add(version);
		doAnswer(returnSuccess(version)).when(versionService).getVersion(eq(versionId), any(AsyncCallback.class));
		
		doAnswer(returnSuccess(givenVersions)).when(versionService).getVersions(eq(modelId), any(AsyncCallback.class));
		doAnswer(returnFailure(new Exception("Wrong model"))).when(versionService).getVersions(eq(nonexistentModelId), any(AsyncCallback.class));
		doAnswer(returnFailure(new Exception("Wrong version"))).when(versionService).getVersion(eq(nonexistentVersionId), any(AsyncCallback.class));
		doAnswer(returnSuccess(null)).when(versionService).removeVersion(any(Long.class), any(AsyncCallback.class));
	}

	private void thenAppropriateVersionsReturned() {
		verify(modelController, times(1)).getModel(eq(modelId), any(ModelCallback.class));
		verify(versionService, times(1)).getVersions(eq(modelId), any(AsyncCallback.class));
		assertEquals(givenVersions, returnedVersions);
	}

	private void whenGetVersions() {
		versionController.getVersions(modelId, new VersionsCallback() {
			
			@Override
			public void setVersions(List<Version> versionList) {
				returnedVersions = versionList;
			}
			
		}, true);
	}
	
	@Test
	public void testGetVersion() {
		givenModelWith2Versions();
		whenGetFirstVersion();
		thenReturnedAppropriateVersion();
	}
	
	private void thenReturnedAppropriateVersion() {
		verify(modelController, times(1)).getModel(eq(modelId), any(ModelCallback.class));
		verify(versionService, times(1)).getVersions(eq(modelId), any(AsyncCallback.class));
		verify(versionService, times(1)).getVersion(eq(givenVersions.get(0).getId()), any(AsyncCallback.class));
		assertEquals(givenVersions.get(0), returnedVersion);
	}

	private void whenGetFirstVersion() {
		versionController.getVersion(givenVersions.get(0).getId(), new VersionCallback() {
			@Override
			public void setVersion(Version version) {
				returnedVersion = version;
			}
		});
	}
	
	@Test
	public void testGetNonexistentVersion() {
		givenModelWith2Versions();
		whenGetNonexistentVersion();
		thenReactedWellOnNonexistentVersion();
	}

	private void whenGetNonexistentVersion() {
		versionController.getVersion(nonexistentVersionId, new VersionCallback() {
			@Override
			public void setVersion(Version version) {
				called = true;			
			}
		});
	}
	
	private void thenReactedWellOnNonexistentVersion() {
		assertFalse(called);
		verify(eventBus, times(1)).fireEvent(argThat(new NotificationEvenMatcher(NotificationType.ERROR)));
	}
	
	@Test
	public void testGetVersionsOfNonexistentModel() {
		givenModelWith2Versions();
		whenGetVersionsOfNonexistentModel();
		thenReactedWellOnNonexistentModel();
	}
	
	private void whenGetVersionsOfNonexistentModel() {
		versionController.getVersions(nonexistentModelId, new VersionsCallback() {
			@Override
			public void setVersions(List<Version> versions) {
				called = true;
			}
		}, false);
	}
	
	private void thenReactedWellOnNonexistentModel() {
		assertFalse(called);
	}
	
	@Test
	public void testReleaseVersion() {
		givenNewVersion();
		whenReleaseNewVersion();
		thenAppropriateVersionReturned();
	}
	
	private void givenNewVersion() {
		givenModel();
		givenVersion = new Version();
		givenVersion.setModelId(modelId);
		givenVersion.setName(registeredVersionName);
		doAnswer(returnSuccess(givenVersion)).when(versionService).addVersion(eq(modelId), any(Version.class), any(AsyncCallback.class));
	}

	private void whenReleaseNewVersion() {
		versionController.releaseNewVersion(modelId, registeredVersionName, new VersionCallback() {
			@Override
			public void setVersion(Version version) {		
				returnedVersion = version;	
			}
		});
	}
	
	private void thenAppropriateVersionReturned() {
		verify(modelController, times(1)).getModel(eq(modelId), any(ModelCallback.class));
		verify(versionService, times(1)).addVersion(eq(modelId), argThat(new VersionNameMatcher(givenVersion)), any(AsyncCallback.class));
		assertEquals(givenVersion, returnedVersion);
	}
	
	@Test
	public void testRemoveVersion() {
		givenModelWith2Versions();
		whenRemoveVersion();
		thenVersionRemoved();
	}
	
	private void whenRemoveVersion() {
		versionController.removeVersion(givenVersions.get(0).getId(), new Command() {
			
			@Override
			public void execute() {
				called = true;
			}
		});
	}
		
	private void thenVersionRemoved() {
		verify(versionService, times(1)).removeVersion(eq(givenVersions.get(0).getId()), any(AsyncCallback.class));
		assertTrue(called);
	}	

	@Test
	public void testRemoveVersionFailed() {
		givenModelWith2Versions();
		whenRemoveNonexistentVersion();
		thenVersionNotRemoved();
	}

	private void whenRemoveNonexistentVersion() {
		versionController.removeVersion(nonexistentVersionId, new Command() {
			
			@Override
			public void execute() {
				called = true;
			}
		});
	}
	
	private void thenVersionNotRemoved() {
		verify(versionService, times(0)).removeVersion(eq(nonexistentVersionId), any(AsyncCallback.class));
		verify(eventBus, times(1)).fireEvent(argThat(new NotificationEvenMatcher(NotificationType.ERROR)));
		assertFalse(called);
	}
	
	abstract class VersionMatcher extends ArgumentMatcher<Version> {

		protected Version version; 
		
		public VersionMatcher(Version version) {
			this.version = version;
		}
		
		@Override
		public boolean matches(Object argument) {
			if (argument instanceof Version) {
				Version other = (Version) argument;
				return matchesVersion(other);
			}
			return false;
		}
		
		abstract boolean matchesVersion(Version other);
	}
	
	class VersionNameMatcher extends VersionMatcher {
		
		public VersionNameMatcher(Version version) {
			super(version);
		}

		@Override
		boolean matchesVersion(Version other) {
			return version.getName().equals(other.getName()) && (version.getModelId() == other.getModelId());
		}
		
	}
}
