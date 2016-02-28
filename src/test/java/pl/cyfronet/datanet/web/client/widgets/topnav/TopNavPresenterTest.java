package pl.cyfronet.datanet.web.client.widgets.topnav;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import pl.cyfronet.datanet.web.client.controller.ClientController;
import pl.cyfronet.datanet.web.client.controller.timeout.SessionTimeoutController;
import pl.cyfronet.datanet.web.client.event.notification.ModelNotificationMessage;
import pl.cyfronet.datanet.web.client.event.notification.NotificationEvent;
import pl.cyfronet.datanet.web.client.event.notification.NotificationEvent.NotificationType;
import pl.cyfronet.datanet.web.client.event.notification.NotificationMessage;
import pl.cyfronet.datanet.web.client.widgets.topnav.TopNavPanel.MessageType;
import pl.cyfronet.datanet.web.client.widgets.topnav.TopNavPresenter.View;

import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.SimpleEventBus;

@RunWith(GwtMockitoTestRunner.class)
public class TopNavPresenterTest {
	@Mock private View view;
	@Mock private Provider<ClientController> clientControllerProvider;
	@Mock private ClientController clientController;
	@Mock private NotificationMessages notificationMessages;
	@Mock private SessionTimeoutController sessionTimeoutController;

	private TopNavPresenter topNavPresenter;
	private Map<NotificationMessage, String> messagesMap;

	@Before
	public void prepare() {
		MockitoAnnotations.initMocks(this);
		when(clientControllerProvider.get()).thenReturn(clientController);
		topNavPresenter = new TopNavPresenter(view, new SimpleEventBus(), clientControllerProvider,
				notificationMessages, sessionTimeoutController);
		initMessages();
	}

	private void initMessages() {
		messagesMap = new HashMap<>();
		messagesMap.put(ModelNotificationMessage.modelDeployed, "m1");
		messagesMap.put(ModelNotificationMessage.modelSaved, "m2 {0}");

		for (Entry<NotificationMessage, String> entry : messagesMap.entrySet()) {
			String key = entry.getKey().getMessageCode();
			String value = entry.getValue();

			when(notificationMessages.getString(key)).thenReturn(value);
		}
	}

	@Test
	public void shouldDisplayNotification() throws Exception {
		whenNotificationSent(NotificationType.NOTE);
		thenNotificationDisplayed(MessageType.INFO);
	}

	private void whenNotificationSent(NotificationType type) {
		topNavPresenter.onNotification(new NotificationEvent(
				ModelNotificationMessage.modelDeployed, type));
	}

	private void thenNotificationDisplayed(MessageType type) {
		verify(view, times(1)).displayMessage(
				messagesMap.get(ModelNotificationMessage.modelDeployed), type);
	}

	@Test
	public void shouldDisplayError() throws Exception {
		whenNotificationSent(NotificationType.ERROR);
		thenNotificationDisplayed(MessageType.ERROR);
	}

	@Test
	public void shouldDisplayNotificationWithMessage() throws Exception {
		whenSentNotificationWithMessage();
		thenNotificationWithMessageDisplayed();
	}

	private void whenSentNotificationWithMessage() {
		topNavPresenter.onNotification(new NotificationEvent(
				ModelNotificationMessage.modelSaved, NotificationType.NOTE,
				"msg"));
	}

	private void thenNotificationWithMessageDisplayed() {
		verify(view, times(1)).displayMessage("m2 msg", MessageType.INFO);
	}
}
