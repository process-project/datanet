package pl.cyfronet.datanet.web.client.widgets.entitypanel;

import com.google.gwt.user.client.ui.IsWidget;

public class EntityPanelPresenter implements Presenter {
	public interface View extends IsWidget {
		void setPresenter(Presenter presenter);
	}
	
	private View view;
	
	public EntityPanelPresenter(View view) {
		this.view = view;
		view.setPresenter(this);
	}
	
	public IsWidget getWidget() {
		return view;
	}

	@Override
	public void onRemoveEntity() {
		// TODO Auto-generated method stub
		
	}
}