package pl.cyfronet.datanet.web.client.widgets.modelbrowserpanel;

@Deprecated
public interface Presenter {
	void onNewModel();
	void onSaveModel();
	void onDeployModel();
	void onModelClicked(long id);
}