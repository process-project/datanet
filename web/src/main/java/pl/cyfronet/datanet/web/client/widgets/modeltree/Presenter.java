package pl.cyfronet.datanet.web.client.widgets.modeltree;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.AsyncDataProvider;

public interface Presenter {
	IsWidget getWidget();

	boolean isLeaf(TreeItem value);

	void loadChildren(TreeItem parent, AsyncDataProvider<TreeItem> dataProvider);
}
