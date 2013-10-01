package pl.cyfronet.datanet.web.client.widgets.modeltree;

import static pl.cyfronet.datanet.web.client.widgets.modeltree.ItemType.isModel;
import static pl.cyfronet.datanet.web.client.widgets.modeltree.ItemType.isRoot;
import static pl.cyfronet.datanet.web.client.widgets.modeltree.ItemType.isVersion;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.TreeViewModel;

public class ModelTreeViewModel implements TreeViewModel {

	private static final Logger logger = LoggerFactory
			.getLogger(ModelTreeViewModel.class.getName());

	private Presenter presenter;
	private TreeItemsAsyncDataProvider rootDataProvider;
	/**
	 * A mapping from model id to version data provider
	 */
	private Map<Long, TreeItemsAsyncDataProvider> versionDataProviders;  
	private Map<Long, TreeItemsAsyncDataProvider> repositoryDataProviders;  
	private ModelTreePanelMessages messages;
	private SelectionModel<TreeItem> selection;
	
	
	public ModelTreeViewModel(ModelTreePanelMessages messages,
			SelectionModel<TreeItem> selection) {
		this.messages = messages;
		this.selection = selection;
		this.versionDataProviders = new HashMap<Long, TreeItemsAsyncDataProvider>();
		this.repositoryDataProviders = new HashMap<Long, TreeItemsAsyncDataProvider>();
	}

	@Override
	public <T> NodeInfo<?> getNodeInfo(T value) {
		
		TreeItem treeItem = (TreeItem) value;
		TreeItemsAsyncDataProvider dataProvider = null;
		
		Cell<TreeItem> cell = null;
		if (isRoot(treeItem)) {
			dataProvider = new TreeItemsAsyncDataProvider(null);
			rootDataProvider = dataProvider; 
			cell = new AbstractCell<TreeItem>() {
				@Override
				public void render(Context context, TreeItem value,
						SafeHtmlBuilder sb) {
					if (value.isDirty()) {
						sb.appendEscaped("* ");
					}
					sb.appendHtmlConstant("<i style=\"margin-right: 5px\" class=\""
							+ IconType.SITEMAP.get() + "\"></i>");
					sb.appendEscaped(value.getName());
				}
			};
		} else if (isModel(treeItem)) {
			long modelId = treeItem.getId();
			dataProvider = versionDataProviders.get(modelId);
			cell = new AbstractCell<TreeItem>() {
				@Override
				public void render(Context context, TreeItem value,
						SafeHtmlBuilder sb) {
					sb.appendHtmlConstant("<i style=\"margin-right: 5px\" class=\""
							+ IconType.BRIEFCASE.get() + "\"></i>");
					sb.appendEscaped(value.getName());
				}
			};
		} else if (isVersion(treeItem)) {
			long modelId = treeItem.getId();
			dataProvider = repositoryDataProviders.get(modelId);
			cell = new AbstractCell<TreeItem>() {
				@Override
				public void render(Context context, TreeItem value,
						SafeHtmlBuilder sb) {
					sb.appendHtmlConstant("<i style=\"margin-right: 5px\" class=\""
							+ IconType.CLOUD.get() + "\"></i>	");
					sb.appendEscaped(value.getName());
				}
			};
		} else {
			cell = new AbstractCell<TreeItem>() {
				@Override
				public void render(Context context, TreeItem value,
						SafeHtmlBuilder sb) {
					sb.appendEscaped(value.getName());
				}
			};
		}

		return new DefaultNodeInfo<TreeItem>(dataProvider, cell, selection,
				null);
	}

	@Override
	public boolean isLeaf(Object value) {
		return value != null
				&& (presenter != null ? presenter.isLeaf((TreeItem) value)
						: false);
	}

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	public void reload() {
		rootDataProvider.reload();
	}

	public TreeItemsAsyncDataProvider getModelProvider() {
		return rootDataProvider;
	}
	
	public TreeItemsAsyncDataProvider getVersionProvider(long modelId) {
		return versionDataProviders.get(modelId);
	}
	
	public TreeItemsAsyncDataProvider getRepositoryProvider(long versionId) {
		return repositoryDataProviders.get(versionId);
	}

	public class TreeItemsAsyncDataProvider extends AsyncDataProvider<TreeItem> {

		private TreeItem current;
		private List<TreeItem> children;
		
		public TreeItemsAsyncDataProvider(TreeItem current) {
			this.current = current;
		}

		@Override
		protected void onRangeChanged(HasData<TreeItem> display) {
			reload();
		}

		public void reload() {
			logger.debug("Reloading childs for {}", current);
			loading();
			if (presenter != null) {
				presenter.loadChildren(current);
			} else {
				logger.debug("Presenter is null");
			}
		}

		@Override
		public void updateRowData(int start, List<TreeItem> values) {
			children = values;
			createChildrenDataProviders();
			super.updateRowData(start, values);
		}

		private void createChildrenDataProviders() {
			Map<Long, TreeItemsAsyncDataProvider> childrenProviders = null;
			if(isRoot(current)) {
				childrenProviders = versionDataProviders;
			} else if (isModel(current)) {
				childrenProviders = repositoryDataProviders;
			}
			
			if(childrenProviders != null && children != null) {
				childrenProviders.clear();
				
				for (TreeItem child : children) {
					childrenProviders.put(child.getId(), new TreeItemsAsyncDataProvider(child));
				}
			}
		}

		public List<TreeItem> getChildren() {
			return children;
		}

		private void loading() {
			updateRowCount(1, true);
			updateRowData(0, Arrays.asList(TreeItem.newLoading(messages.loading())));
		}
	}
}
