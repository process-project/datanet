package pl.cyfronet.datanet.test.presenters.login;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import pl.cyfronet.datanet.test.MockingUtil;
import pl.cyfronet.datanet.web.client.ClientController;
import pl.cyfronet.datanet.web.client.errors.LoginException;
import pl.cyfronet.datanet.web.client.errors.LoginException.Code;
import pl.cyfronet.datanet.web.client.errors.RpcErrorHandler;
import pl.cyfronet.datanet.web.client.services.LoginServiceAsync;
import pl.cyfronet.datanet.web.client.widgets.login.LoginPresenter;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class LoginPresenterTest {
	@Mock private LoginPresenter loginPresenter;
	@Mock private LoginServiceAsync loginService;
	@Mock private RpcErrorHandler rpcErrorHandler;
	@Mock private LoginPresenter.View view;
	@Mock private ClientController clientController;
	
	@Before
	public void prepare() {
		MockitoAnnotations.initMocks(this);
		loginPresenter = new LoginPresenter(loginService, rpcErrorHandler, view, clientController);
	}
	
	@Test
	public void emptyLoginOrPasswordFields() {
		when(view.getLogin()).thenReturn(MockingUtil.mockHasText(""));
		when(view.getPassword()).thenReturn(MockingUtil.mockHasText("notEmpty"));
		
		loginPresenter.onLogin();
		
		verify(view).errorLoginOrPasswordEmpty();
	}
	
	@Test
	public void messageCleanup() {
		when(view.getLogin()).thenReturn(MockingUtil.mockHasText("loginValid"));
		when(view.getPassword()).thenReturn(MockingUtil.mockHasText("passwordValid"));
		
		loginPresenter.onLogin();
		
		verify(view).clearErrors();
	}
	
	@Test
	public void rpcServiceSuccess() {
		when(view.getLogin()).thenReturn(MockingUtil.mockHasText("loginValid"));
		when(view.getPassword()).thenReturn(MockingUtil.mockHasText("passwordValid"));
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				AsyncCallback<Void> callback = (AsyncCallback<Void>) invocation.getArguments()[2];
				callback.onSuccess(null);
				return null;
			}}).when(loginService).login(anyString(), anyString(), any(AsyncCallback.class));
		
		loginPresenter.onLogin();
		
		verify(clientController).onLogin();
	}
	
	@Test
	public void wrongLoginOrPassword() {
		when(view.getLogin()).thenReturn(MockingUtil.mockHasText("loginValid"));
		when(view.getPassword()).thenReturn(MockingUtil.mockHasText("passwordValid"));
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				AsyncCallback<Void> callback = (AsyncCallback<Void>) invocation.getArguments()[2];
				callback.onFailure(new LoginException(Code.UserPasswordUnknown));
				return null;
			}}).when(loginService).login(anyString(), anyString(), any(AsyncCallback.class));
		
		loginPresenter.onLogin();
		
		verify(view).errorWrongLoginOrPassword();
	}
	
	@Test
	public void busyStateWhenLoginSuccess() {
		when(view.getLogin()).thenReturn(MockingUtil.mockHasText("loginValid"));
		when(view.getPassword()).thenReturn(MockingUtil.mockHasText("passwordValid"));
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				AsyncCallback<Void> callback = (AsyncCallback<Void>) invocation.getArguments()[2];
				callback.onSuccess(null);
				return null;
			}}).when(loginService).login(anyString(), anyString(), any(AsyncCallback.class));
		
		loginPresenter.onLogin();
		
		InOrder order = inOrder(view);
		order.verify(view).setBusyState(true);
		order.verify(view).setBusyState(false);
	}
	
	@Test
	public void busyStateWhenLoginFailure() {
		when(view.getLogin()).thenReturn(MockingUtil.mockHasText("loginValid"));
		when(view.getPassword()).thenReturn(MockingUtil.mockHasText("passwordValid"));
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				AsyncCallback<Void> callback = (AsyncCallback<Void>) invocation.getArguments()[2];
				callback.onFailure(new LoginException(Code.UserPasswordUnknown));
				return null;
			}}).when(loginService).login(anyString(), anyString(), any(AsyncCallback.class));
		
		loginPresenter.onLogin();
		
		InOrder order = inOrder(view);
		order.verify(view).setBusyState(true);
		order.verify(view).setBusyState(false);
	}
	
	@Test
	public void noBusyStateWhenEmptyFields() {
		when(view.getLogin()).thenReturn(MockingUtil.mockHasText("loginValid"));
		when(view.getPassword()).thenReturn(MockingUtil.mockHasText(""));
		
		loginPresenter.onLogin();
		
		verify(view, never()).setBusyState(true);
		verify(view, never()).setBusyState(false);
	}
}