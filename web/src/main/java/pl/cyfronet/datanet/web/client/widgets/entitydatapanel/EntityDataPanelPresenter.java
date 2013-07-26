package pl.cyfronet.datanet.web.client.widgets.entitydatapanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.cyfronet.datanet.model.beans.Entity;
import pl.cyfronet.datanet.model.beans.Field;
import pl.cyfronet.datanet.model.beans.Field.Type;
import pl.cyfronet.datanet.web.client.controller.RepositoryController;
import pl.cyfronet.datanet.web.client.controller.RepositoryController.DataSavedCallback;
import pl.cyfronet.datanet.web.client.controller.RepositoryController.EntityCallback;
import pl.cyfronet.datanet.web.client.controller.beans.EntityData;
import pl.cyfronet.datanet.web.client.di.factory.EntityRowDataProviderFactory;
import pl.cyfronet.datanet.web.client.event.notification.NotificationEvent;
import pl.cyfronet.datanet.web.client.event.notification.RepositoryNotificationMessage;
import pl.cyfronet.datanet.web.client.event.notification.NotificationEvent.NotificationType;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.HasData;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.web.bindery.event.shared.EventBus;

public class EntityDataPanelPresenter implements Presenter {
	public interface View extends IsWidget {
		void setPresenter(Presenter presenter);
		HasText addSearchField(String name, Type type);
		HasData<EntityRow> getDataTable();
		void initDataTable(List<String> fieldNames);
		void resetPager(HasData<EntityRow> dataTable);
		void showNewEntityRowPopup();
		HasText addNewEntityRowField(String name, Type type);
		void refreshDataTable();
		void hideNewEntityRowPopup();
	}
	
	public interface DataCallback {
		void onData(EntityData data);
		void error();
	}
	
	private RepositoryController repositoryController;
	private View view;
	private String entityName;
	private long repositoryId;
	private EntityRowDataProviderFactory entityRowDataProviderFactory;
	private boolean shown;
	private Map<String, HasText> searchFields;
	private EntityRowDataProvider dataProvider;
	private Map<String, HasText> newEntityRowFields;
	private EventBus eventBus;
	
	@Inject
	public EntityDataPanelPresenter(View view, RepositoryController repositoryController,
			@Assisted long repositoryId, @Assisted String entityName,
			EntityRowDataProviderFactory entityRowDataProviderFactory, EventBus eventBus) {
		this.view = view;
		this.repositoryController = repositoryController;
		this.repositoryId = repositoryId;
		this.entityName = entityName;
		this.entityRowDataProviderFactory = entityRowDataProviderFactory;
		this.eventBus = eventBus;
		view.setPresenter(this);
		searchFields = new HashMap<String, HasText>();
		newEntityRowFields = new HashMap<String, HasText>();
	}

	public IsWidget getWidget() {
		return view;
	}
	
	public void show() {
		if(!shown) {
			repositoryController.getEntity(repositoryId, entityName, new EntityCallback() {
				@Override
				public void setEntity(Entity entity) {
					if (entity.getFields() != null) {
						showSearchFields(entity.getFields());
						addNewRowFields(entity.getFields());
						showData(entity.getFields());
					}
				}
			});
			shown = true;
		}
	}

	@Override
	public void onSearch() {
		//TODO(DH)
	}
	
	@Override
	public void onDataRetrievalError() {
		view.getDataTable().setRowCount(0, true);
		view.resetPager(view.getDataTable());
	}
	
	@Override
	public void onAddNewEntityRow() {
		view.showNewEntityRowPopup();
	}
	
	@Override
	public void onSaveNewEntityRow() {
		Map<String, String> data = new HashMap<String, String>();
		
		for(String fieldName : newEntityRowFields.keySet()) {
			data.put(fieldName, newEntityRowFields.get(fieldName).getText());
		}
		
		repositoryController.addEntityRow(repositoryId, entityName, data, new DataSavedCallback() {
			@Override
			public void onDataSaved(boolean success) {
				view.hideNewEntityRowPopup();
				
				for(String fieldName : newEntityRowFields.keySet()) {
					newEntityRowFields.get(fieldName).setText("");
				}
				
				if (success) {
					view.refreshDataTable();
				} else {
					eventBus.fireEvent(new NotificationEvent(RepositoryNotificationMessage.repositorySaveEntityRowError, NotificationType.ERROR));
				}
			}});
	}
	
	private void showSearchFields(List<Field> fields) {
		for(Field field : fields) {
			searchFields.put(field.getName(), view.addSearchField(field.getName(), field.getType()));
		}
	}
	
	private void showData(List<Field> fields) {
		List<String> fieldNames = new ArrayList<String>();
		
		for(Field field : fields) {
			fieldNames.add(field.getName());
		}
		
		view.initDataTable(fieldNames);
		dataProvider = entityRowDataProviderFactory.create(repositoryId, entityName, this);
		dataProvider.addDataDisplay(view.getDataTable());
		view.resetPager(view.getDataTable());
	}
	
	private void addNewRowFields(List<Field> fields) {
		for(Field field : fields) {
			newEntityRowFields.put(field.getName(), view.addNewEntityRowField(field.getName(), field.getType()));
		}
	}
}