package pl.cyfronet.datanet.web.client.widgets.modelbrowserpanel;

public interface Presenter {
	void onNewEntity();
	void onModelNameChanged(String modelName);
	void onModelVersionChanged(String versionName);
	void onNewModel();
	void onSaveModel();
	void onDeployModel();
	void onModelClicked(long id);
}