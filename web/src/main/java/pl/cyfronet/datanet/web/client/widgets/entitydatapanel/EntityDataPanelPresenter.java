package pl.cyfronet.datanet.web.client.widgets.entitydatapanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.cyfronet.datanet.model.beans.Entity;
import pl.cyfronet.datanet.model.beans.Field;
import pl.cyfronet.datanet.model.beans.Field.Type;
import pl.cyfronet.datanet.web.client.controller.RepositoryController;
import pl.cyfronet.datanet.web.client.controller.RepositoryController.EntityCallback;
import pl.cyfronet.datanet.web.client.controller.beans.EntityData;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.HasData;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class EntityDataPanelPresenter implements Presenter {
	public interface View extends IsWidget {
		void setPresenter(Presenter presenter);
		HasText addSearchField(String name, Type type);
		HasData<EntityRow> getDataTable();
		void initDataTable(List<String> fieldNames);
		void resetPager(HasData<EntityRow> dataTable);
	}
	
	public interface DataCallback {
		void onData(EntityData data);
	}
	
	private RepositoryController repositoryController;
	private View view;
	private String entityName;
	private long repositoryId;
	private EntityRowDataProviderFactory entityRowDataProviderFactory;
	private boolean shown;
	private Map<String, HasText> searchFields;
	private EntityRowDataProvider dataProvider;
	
	@Inject
	public EntityDataPanelPresenter(View view, RepositoryController repositoryController,
			@Assisted long repositoryId, @Assisted String entityName,
			EntityRowDataProviderFactory entityRowDataProviderFactory) {
		this.view = view;
		this.repositoryController = repositoryController;
		this.repositoryId = repositoryId;
		this.entityName = entityName;
		this.entityRowDataProviderFactory = entityRowDataProviderFactory;
		view.setPresenter(this);
		searchFields = new HashMap<String, HasText>();
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
						showFields(entity.getFields());
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
	
	private void showFields(List<Field> fields) {
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
}