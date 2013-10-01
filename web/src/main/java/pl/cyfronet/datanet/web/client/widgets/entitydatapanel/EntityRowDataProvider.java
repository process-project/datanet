package pl.cyfronet.datanet.web.client.widgets.entitydatapanel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pl.cyfronet.datanet.model.beans.Repository;
import pl.cyfronet.datanet.model.beans.AccessConfig.Access;
import pl.cyfronet.datanet.web.client.controller.RepositoryController;
import pl.cyfronet.datanet.web.client.controller.RepositoryController.RepositoryCallback;
import pl.cyfronet.datanet.web.client.controller.beans.EntityData;
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
	protected void onRangeChanged(final HasData<EntityRow> display) {
		repositoryController.getRepository(repositoryId, new RepositoryCallback() {
			@Override
			public void setRepository(Repository repository) {
				if (repository.getAccessConfig() != null && repository.getAccessConfig().getAccess() == Access.privateAccess) {
					presenter.retrieveCredentialsAndUpdateData();
				} else {
					updateRange(display);
				}
			}
			
			@Override
			public void setError(String message) {
				//ignoring - event fired by the controller
			}
		});
	}
	
	public void renderData(EntityData data) {
		List<EntityRow> values = new ArrayList<EntityRow>();
		
		for(Map<String, String> map : data.getEntityRows()) {
			values.add(new EntityRow(map));
		}
		
		updateRowCount(data.getTotalNumberOfEntities(), true);
		
		int startNumber = data.getStartEntityNumber() - 1;
		updateRowData(startNumber, values);
	}
	
	private void updateRange(final HasData<EntityRow> display) {
		final Range range = display.getVisibleRange();
		int startNumber = range.getStart() + 1;
		repositoryController.getEntityRows(repositoryId, entityName, startNumber, range.getLength(), null, null, null, new DataCallback() {
			@Override
			public void onData(EntityData data) {
				renderData(data);
			}

			@Override
			public void error() {
				presenter.onDataRetrievalError();
			}
		});
	}
}