package pl.cyfronet.datanet.web.client.controller.timeout;

import pl.cyfronet.datanet.web.client.util.CsrfUtil;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.MetaElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.RpcRequestBuilder;
import com.google.inject.Inject;

public class SessionTimeoutAndCsrfAwareRpcRequestBuilder extends RpcRequestBuilder {
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
	public SessionTimeoutAndCsrfAwareRpcRequestBuilder(SessionTimeoutController sessionTimeoutController) {
		this.sessionTimeoutController = sessionTimeoutController;
	}
	
	@Override
	protected void doFinish(RequestBuilder rb) {
		super.doFinish(rb);
		rb.setHeader(CsrfUtil.getCsrfHeaderName(), CsrfUtil.getCsrfValue());
		rb.setCallback(new SessionTimeoutAwareRequestCallback(rb.getCallback()));
	}
}