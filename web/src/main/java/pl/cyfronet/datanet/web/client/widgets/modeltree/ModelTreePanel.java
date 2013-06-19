package pl.cyfronet.datanet.web.client.widgets.modeltree;

import java.util.List;

import pl.cyfronet.datanet.web.client.widgets.modeltree.ModelTreePanelPresenter.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
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

	private Presenter presenter;

	private ModelTreeViewModel model;

	private ModelTreePanelMessageses messages;

	public ModelTreePanel() {
		initTree();
		initWidget(uiBinder.createAndBindUi(this));
	}

	private void initTree() {
		messages = GWT.create(ModelTreePanelMessageses.class);
		model = new ModelTreeViewModel(messages);
		modelsTree = new CellTree(model, null);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
		model.setPresenter(presenter);
	}

	@Override
	public void showModels(List<TreeItem> models) {

	}
}
