package pl.cyfronet.datanet.web.client.messages;

public interface MessageDispatcher {
	public enum MessageType {
		INFO,
		ERROR
	}
	public void displayMessage(String message, MessageType type);
}
