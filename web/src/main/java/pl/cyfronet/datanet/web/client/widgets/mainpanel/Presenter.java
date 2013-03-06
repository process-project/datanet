package pl.cyfronet.datanet.web.client.widgets.mainpanel;

public interface Presenter {
	void onLogout();
	void onNewModel();
	void onSaveModel();
	void onModelClicked(long id);
}