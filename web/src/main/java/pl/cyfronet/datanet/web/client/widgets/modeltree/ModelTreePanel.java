package pl.cyfronet.datanet.web.client.widgets.modeltree;

import java.util.List;

import pl.cyfronet.datanet.web.client.widgets.modeltree.ModelTreePanelPresenter.View;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

public class ModelTreePanel extends Composite implements View {
	private static ModelTreePanelUiBinder uiBinder = GWT
			.create(ModelTreePanelUiBinder.class);

	interface ModelTreePanelUiBinder extends UiBinder<Widget, ModelTreePanel> {
	}

	@UiField(provided = true)
	CellTree modelsTree;

	@UiField
	Button remove;

	@UiField
	Button save;

	@UiField
	Button deploy;

	private ModelTreeViewModel model;

	private ModelTreePanelMessageses messages;

	private Presenter presenter;

	private SingleSelectionModel<TreeItem> selection;

	public ModelTreePanel() {
		initTree();
		initWidget(uiBinder.createAndBindUi(this));
	}

	private void initTree() {
		messages = GWT.create(ModelTreePanelMessageses.class);
		selection = new SingleSelectionModel<TreeItem>();
		selection.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				TreeItem selectedItem = selection.getSelectedObject();
				if (selectedItem != null
						&& selectedItem.getType() == ItemType.MODEL) {
					presenter.onModelSelected(selectedItem.getId());
				}
			}
		});

		model = new ModelTreeViewModel(messages, selection);
		modelsTree = new CellTree(model, null);
	}

	@UiHandler("add")
	void onAddNewModel(ClickEvent event) {
		presenter.onAddNewModel();
	}

	@UiHandler("remove")
	void onRemoveModel(ClickEvent event) {
		presenter.onRemoveModel(selection.getSelectedObject());
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

	@Override
	public void setSelected(TreeItem item) {
		selection.setSelected(item, true);
	}

	@Override
	public void updateTreeItem(TreeItem item) {
		if (item.getType() == ItemType.MODEL) {
			List<TreeItem> models = model.getModelProvider().getChildren();
			for (TreeItem m : models) {
				if (item.equals(m)) {
					m.setDirty(item.isDirty());
					m.setName(item.getName());
				}
			}
			model.getModelProvider().updateRowData(0, models);
		}
	}

	@Override
	public void setSaveEnabled(boolean enabled) {
		save.setEnabled(enabled);
	}

	@Override
	public void setRemoveEnabled(boolean enabled) {
		remove.setEnabled(enabled);
	}

	@Override
	public void setDeployEnabled(boolean enabled) {
		deploy.setEnabled(enabled);
	}

	@Override
	public TreeItem getSelectedObject() {
		return selection.getSelectedObject();
	}
}
