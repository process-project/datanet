package pl.cyfronet.datanet.web.client.di.factory;

import pl.cyfronet.datanet.web.client.widgets.entitypanel.EntityPanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.modelpanel.ModelPanelPresenter;

public interface EntityPanelPresenterFactory {
	EntityPanelPresenter create(ModelPanelPresenter parent);
}
