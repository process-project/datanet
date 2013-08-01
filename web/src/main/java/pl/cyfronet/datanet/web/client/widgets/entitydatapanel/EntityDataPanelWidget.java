package pl.cyfronet.datanet.web.client.widgets.entitydatapanel;

import java.util.List;

import pl.cyfronet.datanet.model.beans.Type;
import pl.cyfronet.datanet.web.client.widgets.entitydatapanel.EntityDataPanelPresenter.View;

import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.ControlLabel;
import com.github.gwtbootstrap.client.ui.Fieldset;
import com.github.gwtbootstrap.client.ui.FormLabel;
import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.constants.LabelType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.HasData;

public class EntityDataPanelWidget extends Composite implements View {
	private static EntityDataPanelWidgetUiBinder uiBinder = GWT.create(EntityDataPanelWidgetUiBinder.class);
	interface EntityDataPanelWidgetUiBinder extends UiBinder<Widget, EntityDataPanelWidget> {}
	
	@UiField Fieldset searchFieldSet;
	@UiField SimplePager pager;
	@UiField CellTable<EntityRow> dataTable;
	@UiField EntityDataPanelMessages messages;
	@UiField Modal addRowPopup;
	@UiField HasWidgets addEntityRowContainer;
	
	private Presenter presenter;
	
	public EntityDataPanelWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@UiHandler("searchButton")
	public void onSearch(ClickEvent event) {
		presenter.onSearch();
	}
	
	@UiHandler("addEntityButton")
	public void onAddEntity(ClickEvent event) {
		presenter.onAddNewEntityRow();
	}
	
	@UiHandler("saveEntityRow")
	public void onSaveNewEntityRow(ClickEvent event) {
		presenter.onSaveNewEntityRow();
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@Override
	public HasText addSearchField(String name, Type type) {
		FormLabel label = new FormLabel(name);
		searchFieldSet.add(label);
		
		TextBox field = new TextBox();
		searchFieldSet.add(field);
		
		return field;
	}
	
	@Override
	public HasData<EntityRow> getDataTable() {
		return dataTable;
	}
	
	@Override
	public void initDataTable(List<String> fieldNames) {
		dataTable.setEmptyTableWidget(new Label(LabelType.DEFAULT, messages.noEntityValues()));
		//TODO(DH): set icon spin when API is updated
		dataTable.setLoadingIndicator(new Icon(IconType.SPINNER));
		
		for (String fieldName : fieldNames) {
			dataTable.addColumn(new EntityTextColumn(fieldName), fieldName);
		}
	}
	
	@Override
	public void resetPager(HasData<EntityRow> dataTable) {
		pager.setDisplay(dataTable);
	}

	@Override
	public void showNewEntityRowPopup() {
		addRowPopup.show();
	}

	@Override
	public HasText addNewEntityRowField(String name, Type type) {
		addEntityRowContainer.add(new ControlLabel(name));
		
		TextBox field = new TextBox();
		addEntityRowContainer.add(field);
		
		return field;
	}

	@Override
	public void refreshDataTable() {
		dataTable.setVisibleRangeAndClearData(dataTable.getVisibleRange(), true);
		pager.setDisplay(dataTable);
	}

	@Override
	public void hideNewEntityRowPopup() {
		addRowPopup.hide();
	}
}