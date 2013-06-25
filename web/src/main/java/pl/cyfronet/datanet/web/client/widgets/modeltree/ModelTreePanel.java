package pl.cyfronet.datanet.web.client.widgets.modeltree;

import pl.cyfronet.datanet.web.client.widgets.modeltree.ModelTreePanelPresenter.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ModelTreePanel extends Composite implements View {
	private static ModelTreePanelUiBinder uiBinder = GWT
			.create(ModelTreePanelUiBinder.class);

	interface ModelTreePanelUiBinder extends UiBinder<Widget, ModelTreePanel> {
	}

	@UiField(provided = true)
	CellTree modelsTree;

	@UiField
	HorizontalPanel buttons;

	private ModelTreeViewModel model;

	private ModelTreePanelMessageses messages;

	private Presenter presenter;

	public ModelTreePanel() {
		initTree();
		initWidget(uiBinder.createAndBindUi(this));
	}

	private void initTree() {
		messages = GWT.create(ModelTreePanelMessageses.class);
		model = new ModelTreeViewModel(messages);
		modelsTree = new CellTree(model, null);
	}	
	
	@UiHandler("add")
	void onAddNewModel(ClickEvent event) {
		presenter.onAddNewModel();
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
		model.setPresenter(presenter);
	}

	@Override
	public void reload() {
		model.reload();
	}
}
