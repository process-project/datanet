package pl.cyfronet.datanet.web.client.widgets.modeltree;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;

public class ModelTreeViewModel implements TreeViewModel {

	private static final Logger logger = Logger
			.getLogger(ModelTreeViewModel.class.getName());

	private Presenter presenter;
	private TreeItemsAsyncDataProvider rootDataProvider;
	private ModelTreePanelMessageses messages;
	private DefaultSelectionEventManager<TreeItem> manager;
	private SingleSelectionModel<TreeItem> selection;

	public ModelTreeViewModel(ModelTreePanelMessageses messages) {
		this.messages = messages;
		manager = DefaultSelectionEventManager.createDefaultManager();
		selection = new SingleSelectionModel<TreeItem>();
		selection.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				TreeItem selectedItem = selection.getSelectedObject();
				if(selectedItem.getType() == ItemType.MODEL) {
					presenter.onModelSelected(selectedItem.getId());
				}
			}
		});
	}

	@Override
	public <T> NodeInfo<?> getNodeInfo(T value) {
		TreeItemsAsyncDataProvider dataProvider = new TreeItemsAsyncDataProvider(
				(TreeItem) value);

		if (value == null) {
			rootDataProvider = dataProvider;
		}

		Cell<TreeItem> cell = new AbstractCell<TreeItem>() {
			@Override
			public void render(Context context, TreeItem value,
					SafeHtmlBuilder sb) {
				sb.appendEscaped(value.getName());
			}
		};
		return new DefaultNodeInfo<TreeItem>(dataProvider, cell, selection,
				manager, null);
	}

	@Override
	public boolean isLeaf(Object value) {
		return value != null
				|| (presenter != null ? presenter.isLeaf((TreeItem) value)
						: false);
	}

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	public void reload() {
		rootDataProvider.reload();
	}

	private class TreeItemsAsyncDataProvider extends
			AsyncDataProvider<TreeItem> {

		private TreeItem parent;

		public TreeItemsAsyncDataProvider(TreeItem parent) {
			this.parent = parent;
		}

		@Override
		protected void onRangeChanged(HasData<TreeItem> display) {
			reload();
		}

		public void reload() {
			logger.log(Level.INFO, "Reloading childs for " + parent);
			loading();
			if (presenter != null) {
				presenter.loadChildren(parent, this);
			} else {
				logger.log(Level.INFO, "Presenter is null");
			}
		}

		private void loading() {
			updateRowCount(1, true);
			updateRowData(0, Arrays.asList(new TreeItem(null, messages
					.loading(), ItemType.LOADING)));
		}
	}
}
