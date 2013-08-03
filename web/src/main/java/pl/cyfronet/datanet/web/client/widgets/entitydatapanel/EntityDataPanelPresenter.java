package pl.cyfronet.datanet.web.client.widgets.entitydatapanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.cyfronet.datanet.model.beans.Entity;
import pl.cyfronet.datanet.model.beans.Field;
import pl.cyfronet.datanet.model.beans.Type;
import pl.cyfronet.datanet.web.client.controller.RepositoryController;
import pl.cyfronet.datanet.web.client.controller.RepositoryController.EntityCallback;
import pl.cyfronet.datanet.web.client.controller.beans.EntityData;
import pl.cyfronet.datanet.web.client.di.factory.EntityRowDataProviderFactory;
import pl.cyfronet.datanet.web.client.event.notification.NotificationEvent;
import pl.cyfronet.datanet.web.client.event.notification.NotificationEvent.NotificationType;
import pl.cyfronet.datanet.web.client.event.notification.RepositoryNotificationMessage;

import com.github.gwtbootstrap.client.ui.Form;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.HasData;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.web.bindery.event.shared.EventBus;

public class EntityDataPanelPresenter implements Presenter {
	private static final Logger log = LoggerFactory.getLogger(EntityDataPanelPresenter.class);
	
	public interface View extends IsWidget {
		void setPresenter(Presenter presenter);
		HasText addSearchField(String name, Type type);
		HasData<EntityRow> getDataTable();
		void initDataTable(List<String> fieldNames);
		void resetPager(HasData<EntityRow> dataTable);
		void showNewEntityRowPopup();
		HasText addNewEntityRowField(String name, Type type, int indexOfGivenType);
		void refreshDataTable();
		void hideNewEntityRowPopup();
		Form getEntityUploadForm();
		void setBusyState(boolean b);
		void initEntityUploadForm(long repositoryId, String entityName);
		void addNewEntityRowCredentials();
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
		Map<String, String> query = new HashMap<String, String>();
		
		for (String fieldName : searchFields.keySet()) {
			if (!searchFields.get(fieldName).getText().trim().isEmpty()) {
				query.put(fieldName, searchFields.get(fieldName).getText().trim());
			}
		}
		
		log.info("Search for map {} initiated", query);
		
		repositoryController.getEntityRows(repositoryId, entityName, 1, view.getDataTable().getVisibleRange().getLength(), query, new DataCallback() {
			@Override
			public void onData(EntityData data) {
				dataProvider.renderData(data);
			}
			
			@Override
			public void error() {
				onDataRetrievalError();
			}
		});
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
		view.getEntityUploadForm().submit();
	}
	
	@Override
	public void beforeEntitySubmitted() {
		view.setBusyState(true);
	}
	
	@Override
	public void afterEntitySubmitted(String results) {
		view.setBusyState(false);
		view.hideNewEntityRowPopup();
		
		for(String fieldName : newEntityRowFields.keySet()) {
			newEntityRowFields.get(fieldName).setText("");
		}
		
		if (results.contains("SUCCESS")) {
			view.refreshDataTable();
		} else {
			eventBus.fireEvent(new NotificationEvent(RepositoryNotificationMessage.repositorySaveEntityRowError, NotificationType.ERROR));
		}
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
		Map<Type, Integer> indexes = new HashMap<Type, Integer>();
		view.initEntityUploadForm(repositoryId, entityName);
		
		for(Field field : fields) {
			if (indexes.get(field.getType()) == null) {
				indexes.put(field.getType(), 0);
			}
			
			newEntityRowFields.put(field.getName(), view.addNewEntityRowField(field.getName(), field.getType(), indexes.get(field.getType())));
			indexes.put(field.getType(), indexes.get(field.getType()) + 1);
		}
		
		if (indexes.get(Type.File) != null) {
			view.addNewEntityRowCredentials();
		}
	}
}