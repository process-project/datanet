package pl.cyfronet.datanet.web.client.widgets.entitydatapanel;

import java.util.List;

import pl.cyfronet.datanet.model.beans.Type;
import pl.cyfronet.datanet.web.client.widgets.entitydatapanel.EntityDataPanelPresenter.View;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.ControlLabel;
import com.github.gwtbootstrap.client.ui.Fieldset;
import com.github.gwtbootstrap.client.ui.FileUpload;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.Form.SubmitCompleteEvent;
import com.github.gwtbootstrap.client.ui.Form.SubmitEvent;
import com.github.gwtbootstrap.client.ui.FormLabel;
import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.constants.LabelType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Hidden;
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
	@UiField Form addEntityRowFormContainer;
	@UiField Button saveEntityRow;
	
	private Presenter presenter;
	
	public EntityDataPanelWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@UiHandler("searchButton")
	void onSearch(ClickEvent event) {
		presenter.onSearch();
	}
	
	@UiHandler("addEntityButton")
	void onAddEntity(ClickEvent event) {
		presenter.onAddNewEntityRow();
	}
	
	@UiHandler("saveEntityRow")
	void onSaveNewEntityRow(ClickEvent event) {
		presenter.onSaveNewEntityRow();
	}
	
	@UiHandler("addEntityRowFormContainer")
	void onBeforeEntitySubmit(SubmitEvent event) {
		presenter.beforeEntitySubmitted();
	}
	
	@UiHandler("addEntityRowFormContainer")
	void onAfterEntitySubmitted(SubmitCompleteEvent event) {
		presenter.afterEntitySubmitted(event.getResults());
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@Override
	public HasText addSearchField(String name, Type type) {
		FormLabel label = new FormLabel(name);
		label.getElement().getStyle().setMarginRight(10, Unit.PX);
		searchFieldSet.insert(label, searchFieldSet.getWidgetCount() - 1);
		
		TextBox field = new TextBox();
		field.getElement().getStyle().setMarginRight(10, Unit.PX);
		searchFieldSet.insert(field, searchFieldSet.getWidgetCount() - 1);
		
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
	public HasText addNewEntityRowField(String name, Type type, int indexOfGivenType) {
		addEntityRowFormContainer.add(new ControlLabel(name));
		
		Widget widget = null;
		
		switch (type) {
			case File:
				FileUpload fileUpload = new FileUpload();
				fileUpload.setName("files['" + indexOfGivenType + "']");
				widget = fileUpload;
			break;
			default:
				TextBox textBox = new TextBox();
				textBox.setName("fields['" + name + "']");
				widget = textBox;
				
			break;
		}
		
		addEntityRowFormContainer.add(widget);
		
		return (HasText) widget;
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

	@Override
	public Form getEntityUploadForm() {
		return addEntityRowFormContainer;
	}

	@Override
	public void setBusyState(boolean state) {
		if (state) {
			saveEntityRow.state().loading();
		} else {
			saveEntityRow.state().reset();
		}
	}

	@Override
	public void initEntityUploadForm(long repositoryId, String entityName) {
		Hidden repositoryIdInput = new Hidden();
		repositoryIdInput.setName("repositoryId");
		repositoryIdInput.setValue(String.valueOf(repositoryId));
		addEntityRowFormContainer.add(repositoryIdInput);
		
		Hidden entityNameInput = new Hidden();
		entityNameInput.setName("entityName");
		entityNameInput.setValue(entityName);
		addEntityRowFormContainer.add(entityNameInput);
	}
}