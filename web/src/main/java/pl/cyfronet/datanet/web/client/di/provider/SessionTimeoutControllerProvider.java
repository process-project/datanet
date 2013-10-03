package pl.cyfronet.datanet.web.client.di.provider;

import pl.cyfronet.datanet.web.client.controller.AppProperties;
import pl.cyfronet.datanet.web.client.controller.timeout.SessionTimeoutController;
import pl.cyfronet.datanet.web.client.controller.timeout.SessionTimeoutMessages;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class SessionTimeoutControllerProvider implements Provider<SessionTimeoutController> {
	private AppProperties appProperties;
	private SessionTimeoutMessages sessionTimeoutMessages;

	@Inject
	public SessionTimeoutControllerProvider(AppProperties appProperties, SessionTimeoutMessages sessionTimeoutMessages) {
		this.appProperties = appProperties;
		this.sessionTimeoutMessages = sessionTimeoutMessages;
		
	}

	@Override
	public SessionTimeoutController get() {
		return new SessionTimeoutController(appProperties.sessionTimeoutSeconds(), sessionTimeoutMessages);
	}
}