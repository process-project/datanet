package pl.cyfronet.datanet.web.client.widgets.model;

import com.google.gwt.user.client.ui.IsWidget;


public class ModelPanelPresenter implements Presenter {
	public interface View {
		void setPresenter(Presenter presenter);
		void setTitle(String title);
	}

	private View view;
	private String modelId;
	
	public ModelPanelPresenter(View view, String modelId) {
		this.view = view;
		this.modelId  = modelId;
		initView();		
	}

	private void initView() {
		view.setPresenter(this);
		view.setTitle("Model " + modelId);		
	}

	public IsWidget getWidget() {
		return (IsWidget) view;
	}
}
