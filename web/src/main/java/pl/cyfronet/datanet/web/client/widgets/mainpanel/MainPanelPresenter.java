package pl.cyfronet.datanet.web.client.widgets.mainpanel;

import pl.cyfronet.datanet.web.client.ClientController;
import pl.cyfronet.datanet.web.client.widgets.modelpanel.ModelPanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.modelpanel.ModelPanelWidget;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class MainPanelPresenter implements Presenter {
	interface View {
		void setPresenter(Presenter presenter);
		HasWidgets getMainContainer();
		void errorNoModelPresent();
	}

	private View view;
	private ClientController clientController;
	private ModelPanelPresenter currentModelPanelPresenter;
	
	public MainPanelPresenter(View view, ClientController clientController) {
		this.view = view;
		this.clientController = clientController;
		view.setPresenter(this);
	}
	
	public Widget getWidget() {
		return (Widget) view;
	}

	@Override
	public void onLogout() {
		clientController.onLogout();
	}

	@Override
	public void onNewModel() {
		view.getMainContainer().clear();
		
		currentModelPanelPresenter = new ModelPanelPresenter(new ModelPanelWidget());
		view.getMainContainer().add(currentModelPanelPresenter.getWidget().asWidget());
	}

	@Override
	public void onSaveModel() {
		if(currentModelPanelPresenter != null) {
			
		} else {
			view.errorNoModelPresent();
		}
	}
}