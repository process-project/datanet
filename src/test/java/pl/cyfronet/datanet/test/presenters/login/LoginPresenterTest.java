package pl.cyfronet.datanet.test.presenters.login;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import pl.cyfronet.datanet.test.MockingUtil;
import pl.cyfronet.datanet.web.client.controller.ClientController;
import pl.cyfronet.datanet.web.client.errors.LoginException;
import pl.cyfronet.datanet.web.client.errors.LoginException.Code;
import pl.cyfronet.datanet.web.client.services.LoginServiceAsync;
import pl.cyfronet.datanet.web.client.widgets.login.LoginPresenter;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class LoginPresenterTest {
	@Mock private LoginServiceAsync loginService;
	@Mock private LoginPresenter.View view;
	@Mock private ClientController clientController;
	
	private LoginPresenter loginPresenter;
	
	@Before
	public void prepare() {
		MockitoAnnotations.initMocks(this);
		loginPresenter = new LoginPresenter(loginService, view, clientController);
	}
	
	@Test
	public void emptyLoginOrPasswordFields() {
		when(view.getOpenIdLogin()).thenReturn(MockingUtil.mockHasText(""));
		
		loginPresenter.onOpenIdLoginInitiated();
		
		verify(view).errorOpenIdLoginEmpty();
	}
	
	@Test
	public void messageCleanup() {
		when(view.getOpenIdLogin()).thenReturn(MockingUtil.mockHasText("loginValid"));
		
		loginPresenter.onOpenIdLoginInitiated();
		
		verify(view).clearErrors();
	}
	
	@Test
	public void rpcServiceSuccess() {
		when(view.getOpenIdLogin()).thenReturn(MockingUtil.mockHasText("loginValid"));
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				AsyncCallback<Void> callback = (AsyncCallback<Void>) invocation.getArguments()[1];
				callback.onSuccess(null);
				return null;
			}}).when(loginService).initiateOpenIdLogin(anyString(), any(AsyncCallback.class));
		
		loginPresenter.onOpenIdLoginInitiated();
		
		verify(view).clearErrors();
		verify(view, times(2)).setOpenIdBusyState(anyBoolean());
		verify(view).redirect(anyString());
	}
	
	@Test
	public void busyStateWhenLoginSuccess() {
		when(view.getOpenIdLogin()).thenReturn(MockingUtil.mockHasText("loginValid"));
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				AsyncCallback<Void> callback = (AsyncCallback<Void>) invocation.getArguments()[1];
				callback.onSuccess(null);
				return null;
			}}).when(loginService).initiateOpenIdLogin(anyString(), any(AsyncCallback.class));
		
		loginPresenter.onOpenIdLoginInitiated();
		
		InOrder order = inOrder(view);
		order.verify(view).setOpenIdBusyState(true);
		order.verify(view).setOpenIdBusyState(false);
	}
	
	@Test
	public void busyStateWhenLoginFailure() {
		when(view.getOpenIdLogin()).thenReturn(MockingUtil.mockHasText("loginValid"));
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				AsyncCallback<Void> callback = (AsyncCallback<Void>) invocation.getArguments()[1];
				callback.onFailure(new LoginException(Code.UserPasswordUnknown));
				return null;
			}}).when(loginService).initiateOpenIdLogin(anyString(), any(AsyncCallback.class));
		
		loginPresenter.onOpenIdLoginInitiated();
		
		InOrder order = inOrder(view);
		order.verify(view).setOpenIdBusyState(true);
		order.verify(view).setOpenIdBusyState(false);
	}
	
	@Test
	public void noBusyStateWhenEmptyFields() {
		when(view.getOpenIdLogin()).thenReturn(MockingUtil.mockHasText(""));
		
		loginPresenter.onOpenIdLoginInitiated();
		
		verify(view, never()).setOpenIdBusyState(true);
		verify(view, never()).setOpenIdBusyState(false);
	}
}