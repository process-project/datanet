package pl.cyfronet.datanet.web.client.widgets.modelpanel;

import java.util.ArrayList;
import java.util.List;

import pl.cyfronet.datanet.web.client.widgets.entitypanel.EntityPanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.entitypanel.EntityPanelWidget;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

public class ModelPanelPresenter implements Presenter {
	interface View extends IsWidget {
		void setPresenter(Presenter presenter);
		HasWidgets getEntityContainer();
	}
	
	private View view;
	private List<EntityPanelPresenter> entityPanelPresenters;
	
	public ModelPanelPresenter(View view) {
		this.view = view;
		view.setPresenter(this);
		entityPanelPresenters = new ArrayList<EntityPanelPresenter>();
	}
	
	public IsWidget getWidget() {
		return view;
	}

	@Override
	public void onNewEntity() {
		EntityPanelPresenter entityPanelPresenter = new EntityPanelPresenter(new EntityPanelWidget());
		entityPanelPresenters.add(entityPanelPresenter);
		view.getEntityContainer().add(entityPanelPresenter.getWidget().asWidget());
	}
}