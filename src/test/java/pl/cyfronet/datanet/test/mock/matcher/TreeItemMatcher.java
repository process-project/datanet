package pl.cyfronet.datanet.test.mock.matcher;

import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import pl.cyfronet.datanet.web.client.widgets.modeltree.TreeItem;

public class TreeItemMatcher extends BaseMatcher<List<TreeItem>> {

	private TreeItem[] treeItems;

	public TreeItemMatcher(TreeItem... treeItems) {
		this.treeItems = treeItems;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean matches(Object item) {		
		List<TreeItem> items = (List<TreeItem>)item;
		if(items != null && items.size() == treeItems.length) {
			for (TreeItem treeItem : treeItems) {
				if(!items.contains(treeItem)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public void describeTo(Description description) {
		
	}


}
