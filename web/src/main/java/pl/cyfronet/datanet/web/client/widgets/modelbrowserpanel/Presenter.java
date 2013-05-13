package pl.cyfronet.datanet.web.client.widgets.modelbrowserpanel;

public interface Presenter {
	void onNewModel();
	void onSaveModel();
	void onDeployModel();
	void onModelClicked(long id);
}