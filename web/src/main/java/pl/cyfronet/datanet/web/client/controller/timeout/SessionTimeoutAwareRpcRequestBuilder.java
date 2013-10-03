package pl.cyfronet.datanet.web.client.controller.timeout;

import pl.cyfronet.datanet.web.client.controller.ClientController;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.RpcRequestBuilder;
import com.google.inject.Inject;

public class SessionTimeoutAwareRpcRequestBuilder extends RpcRequestBuilder {
	private class SessionTimeoutAwareRequestCallback implements RequestCallback {
		private RequestCallback requestCallback;

		public SessionTimeoutAwareRequestCallback(RequestCallback requestCallback) {
			this.requestCallback = requestCallback;
		}
		
		@Override
		public void onResponseReceived(Request request, Response response) {
			sessionTimeoutController.resetSessionTimeout();
			requestCallback.onResponseReceived(request, response);
		}

		@Override
		public void onError(Request request, Throwable exception) {
			sessionTimeoutController.resetSessionTimeout();
			requestCallback.onError(request, exception);
		}
	}

	private SessionTimeoutController sessionTimeoutController;
	
	@Inject
	public SessionTimeoutAwareRpcRequestBuilder(SessionTimeoutController sessionTimeoutController) {
		this.sessionTimeoutController = sessionTimeoutController;
	}
	
	@Override
	protected void doFinish(RequestBuilder rb) {
		super.doFinish(rb);
		rb.setCallback(new SessionTimeoutAwareRequestCallback(rb.getCallback()));
	}
}