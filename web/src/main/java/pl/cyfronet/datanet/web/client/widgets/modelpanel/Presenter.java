package pl.cyfronet.datanet.web.client.widgets.modelpanel;

public interface Presenter {
	void onNewEntity();
	void onModelNameChanged(String modelName);
	void onModelVersionChanged(String versionName);
}
