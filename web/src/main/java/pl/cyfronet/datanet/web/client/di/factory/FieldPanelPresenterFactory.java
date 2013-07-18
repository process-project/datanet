package pl.cyfronet.datanet.web.client.di.factory;

import pl.cyfronet.datanet.web.client.widgets.entitypanel.EntityPanelPresenter;
import pl.cyfronet.datanet.web.client.widgets.fieldpanel.FieldPanelPresenter;

public interface FieldPanelPresenterFactory {
	FieldPanelPresenter create(EntityPanelPresenter parent);
}
