package pl.cyfronet.datanet.web.client.widgets.modeltree;

import java.util.Arrays;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.TreeViewModel;

public class ModelTreeViewModel implements TreeViewModel {

	private Presenter presenter;
	private TreeItemsAsyncDataProvider rootDataProvider;
	private ModelTreePanelMessageses messages;

	public ModelTreeViewModel(ModelTreePanelMessageses messages) {
		this.messages = messages;
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
		return new DefaultNodeInfo<TreeItem>(dataProvider, cell);
	}

	@Override
	public boolean isLeaf(Object value) {
		return value != null
				|| (presenter != null ? presenter.isLeaf((TreeItem) value)
						: false);
	}

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
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
			loading();
			if (presenter != null) {
				presenter.loadChildren(parent, this);
			}
		}

		private void loading() {
			updateRowCount(1, true);
			updateRowData(0, Arrays.asList(new TreeItem(null, messages
					.loading(), ItemType.LOADING)));
		}
	}
}
