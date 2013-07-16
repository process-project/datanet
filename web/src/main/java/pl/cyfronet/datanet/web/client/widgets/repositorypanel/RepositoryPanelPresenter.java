package pl.cyfronet.datanet.web.client.widgets.repositorypanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.cyfronet.datanet.model.beans.Entity;
import pl.cyfronet.datanet.model.beans.Field;
import pl.cyfronet.datanet.model.beans.Repository;
import pl.cyfronet.datanet.web.client.controller.repository.RepositoryController;
import pl.cyfronet.datanet.web.client.controller.repository.RepositoryController.RepositoryCallback;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;

public class RepositoryPanelPresenter implements Presenter {
	private static final String REPO_TEMPLATE = "http://{repo}.datanet.cyfronet.pl";

	public interface View extends IsWidget {
		void setPresenter(Presenter presenter);
		void setRepositoryLink(String link);
		void addEntity(String entityName, List<String> fieldNames);
		void showEntity(int entityIndex);
	}
	
	public interface DataCallback {
		void onData(List<Map<String, String>> data);
	}

	private View view;
	private long repositoryId;
	private RepositoryController repositoryController;
	
	@Inject
	public RepositoryPanelPresenter(View view, RepositoryController repositoryController) {
		this.view = view;
		this.repositoryController = repositoryController;
		view.setPresenter(this);
	}
	
	public void setRepository(long repositoryId) {
		this.repositoryId = repositoryId;
		repositoryController.getRepository(repositoryId, new RepositoryCallback() {
			@Override
			public void setRepository(Repository repository) {
				showRepository(repository);
			}});
	}

	public IsWidget getWidget() {
		return view;
	}
	
	@Override
	public void getEntityRows(String entityName, int start, int length, DataCallback dataCallback) {
		//TODO(DH): make the data call through repository controller
		if(dataCallback != null) {
			List<Map<String, String>> result = new ArrayList<Map<String, String>>();
			
			for(int i = start; i < start + length; i++) {
				Map<String, String> row = new HashMap<String, String>();
				row.put("field1", "value " + i);
				result.add(row);
			}
			
			dataCallback.onData(result);
		}
	}
	
	private void showRepository(Repository repository) {
		view.setRepositoryLink(REPO_TEMPLATE.replace("{repo}", repository.getName()));
		
		if(repository.getSourceModel() != null && repository.getSourceModel().getEntities() != null) {
			for(Entity entity : repository.getSourceModel().getEntities()) {
				List<String> fieldNames = new ArrayList<String>();
				
				if(entity.getFields() != null) {
					for(Field field : entity.getFields()) {
						fieldNames.add(field.getName());
					}
				}
				
				view.addEntity(entity.getName(), fieldNames);
			}
			
			if(repository.getSourceModel().getEntities().size() > 0) {
				view.showEntity(0);
			}
		}
	}
}