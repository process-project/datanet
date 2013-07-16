package pl.cyfronet.datanet.web.client.widgets.repositorypanel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pl.cyfronet.datanet.web.client.widgets.repositorypanel.RepositoryPanelPresenter.DataCallback;

import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;

public class EntityRowDataProvider extends AsyncDataProvider<EntityRow> {
	private Presenter presenter;
	private String entityName;
	
	public EntityRowDataProvider(String entityName, Presenter presenter) {
		this.entityName = entityName;
		this.presenter = presenter;
	}
	
	@Override
	protected void onRangeChanged(HasData<EntityRow> display) {
		final Range range = display.getVisibleRange();
		presenter.getEntityRows(entityName, range.getStart(), range.getLength(), new DataCallback() {
			@Override
			public void onData(List<Map<String, String>> data) {
				List<EntityRow> values = new ArrayList<EntityRow>();
				
				for(Map<String, String> map : data) {
					values.add(new EntityRow(map));
				}
				
				updateRowCount(100, true);
				updateRowData(range.getStart(), values);
			}
		});
	}
}