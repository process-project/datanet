package pl.cyfronet.datanet.web.client.widgets.repositorypanel;

public interface Presenter {
	void onRemoveRepository();
	void onSaveAccessConfig();
	void onShowAccessConfig();
	void publicTypeSelected();
	void privateTypeSelected();
}