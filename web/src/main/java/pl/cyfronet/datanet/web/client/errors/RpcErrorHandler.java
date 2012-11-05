package pl.cyfronet.datanet.web.client.errors;

import com.google.gwt.user.client.Window;

public class RpcErrorHandler {
	public void handleRpcError(Throwable t) {
		Window.alert("Unexpected error occurred: " + t.getMessage());
	}
}