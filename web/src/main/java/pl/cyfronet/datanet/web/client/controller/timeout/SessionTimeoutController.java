package pl.cyfronet.datanet.web.client.controller.timeout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.cyfronet.datanet.web.client.services.LoginServiceAsync;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SessionTimeoutController {
	private static final Logger log = LoggerFactory.getLogger(SessionTimeoutController.class);
	
	private Timer sessionTimer;
	private int sessionMillis;
	private SessionTimeoutMessages sessionTimeoutMessages;
	private LoginServiceAsync loginService;

	public SessionTimeoutController(int sessionTimeoutSeconds, SessionTimeoutMessages sessionTimeoutMessages) {
		this.sessionTimeoutMessages = sessionTimeoutMessages;
		sessionMillis = sessionTimeoutSeconds * 1000;
	}
	
	public void setLoginService(LoginServiceAsync loginService) {
		this.loginService = loginService;
	}

	public void resetSessionTimeout() {
		if (sessionTimer != null) {
			sessionTimer.cancel();
			sessionTimer.schedule(sessionMillis);
			log.debug("Session timeout reset successful");
		}
	}
	
	public void start() {
		if (sessionTimer != null) {
			cancel();
		}
		
		sessionTimer = new Timer() {
			@Override
			public void run() {
				onSessionTimeout();
			}
		};
		sessionTimer.schedule(sessionMillis);
		log.debug("Session timeout controller started");
	}
	
	private void onSessionTimeout() {
		log.debug("Session timeout reached");
		Window.alert(sessionTimeoutMessages.timeoutMessage());
		
		//making sure the session is indeed invalidated
		loginService.logout(new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				Window.Location.reload();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.Location.reload();
			}
		});
	}

	public void cancel() {
		if (sessionTimer != null) {
			sessionTimer.cancel();
			sessionTimer = null;
			log.debug("Session timeout canceled");
		}
	}
}