package pl.cyfronet.datanet.web.client.widgets.entitypanel;

import java.util.ArrayList;
import java.util.List;

import pl.cyfronet.datanet.web.client.widgets.fieldpanel.FieldPanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.fieldpanel.FieldPanelWidget;
import pl.cyfronet.datanet.web.client.widgets.modelpanel.ModelPanelPresenter;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

public class EntityPanelPresenter implements Presenter {
	public interface View extends IsWidget {
		void setPresenter(Presenter presenter);
		HasWidgets getFieldContainer();
	}
	
	private View view;
	private ModelPanelPresenter modelPanelPresenter;
	private List<FieldPanelPresenter> fieldPanelPresenters;
	
	public EntityPanelPresenter(ModelPanelPresenter modelPanelPresenter, View view) {
		this.view = view;
		view.setPresenter(this);
		this.modelPanelPresenter = modelPanelPresenter;
		fieldPanelPresenters = new ArrayList<FieldPanelPresenter>();
	}
	
	public IsWidget getWidget() {
		return view;
	}

	@Override
	public void onRemoveEntity() {
		modelPanelPresenter.removeEntity(this);
	}

	@Override
	public void onNewField() {
		FieldPanelPresenter fieldPanelPresenter = new FieldPanelPresenter(this, new FieldPanelWidget());
		fieldPanelPresenters.add(fieldPanelPresenter);
		view.getFieldContainer().add(fieldPanelPresenter.getWidget().asWidget());
	}
}