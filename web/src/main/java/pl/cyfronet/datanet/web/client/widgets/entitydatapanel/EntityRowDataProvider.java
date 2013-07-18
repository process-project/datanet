package pl.cyfronet.datanet.web.client.widgets.entitydatapanel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pl.cyfronet.datanet.web.client.controller.RepositoryController;
import pl.cyfronet.datanet.web.client.widgets.entitydatapanel.EntityDataPanelPresenter.DataCallback;

import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class EntityRowDataProvider extends AsyncDataProvider<EntityRow> {
	private Presenter presenter;
	private String entityName;
	private RepositoryController repositoryController;
	private long repositoryId;
	
	@Inject
	public EntityRowDataProvider(@Assisted long repositoryId, @Assisted String entityName,
			@Assisted Presenter presenter, RepositoryController repositoryController) {
		this.repositoryId = repositoryId;
		this.entityName = entityName;
		this.presenter = presenter;
		this.repositoryController = repositoryController;
	}
	
	@Override
	protected void onRangeChanged(HasData<EntityRow> display) {
		final Range range = display.getVisibleRange();
		repositoryController.getEntityRows(repositoryId, entityName, range.getStart(), range.getLength(), new DataCallback() {
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