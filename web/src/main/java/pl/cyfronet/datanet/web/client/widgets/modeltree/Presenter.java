package pl.cyfronet.datanet.web.client.widgets.modeltree;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;

public interface Presenter {
	IsWidget getWidget();
	List<TreeItem> getModels();
}
