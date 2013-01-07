package pl.cyfronet.datanet.web.client.widgets.modelpanel;

import com.google.gwt.user.client.ui.Widget;

public class ModelPanelPresenter {
	interface View {
		void setPresenter(Presenter presenter);
	}
	
	private View view;
	
	public ModelPanelPresenter(View view) {
		this.view =  view;
	}
	
	public Widget getWidget() {
		return (Widget) view;
	}
}