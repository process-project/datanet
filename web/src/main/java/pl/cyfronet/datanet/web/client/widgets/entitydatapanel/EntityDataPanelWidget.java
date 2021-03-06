package pl.cyfronet.datanet.web.client.widgets.entitydatapanel;

import java.util.Map;

import pl.cyfronet.datanet.model.beans.Type;
import pl.cyfronet.datanet.web.client.util.CsrfUtil;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
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
	@UiField Modal codeTemplatesModal;
	@UiField FlowPanel bashCode;
	@UiField FlowPanel rubyCode;
	@UiField FlowPanel pythonCode;
	
	private Presenter presenter;
	private FlowPanel credsPanel;
	
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
	
	@UiHandler("showCodeTemplates")
	void  onShowCodeTemplates(ClickEvent event) {
		presenter.onShowCodeTemplates();
	}
	
	@UiHandler("closeCodeTemplatesModal")
	void onCloseCodeTemplatesModal(ClickEvent event) {
		codeTemplatesModal.hide();
	}
	
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@Override
	public HasText addSearchField(String name, Type type) {
		FormLabel label = new FormLabel(name);
		label.getElement().getStyle().setMarginRight(10, Unit.PX);
		searchFieldSet.insert(label, searchFieldSet.getWidgetCount() - 2);
		
		TextBox field = new TextBox();
		field.getElement().getStyle().setMarginRight(10, Unit.PX);
		searchFieldSet.insert(field, searchFieldSet.getWidgetCount() - 2);
		
		return field;
	}
	
	@Override
	public HasData<EntityRow> getDataTable() {
		return dataTable;
	}
	
	@Override
	public void initDataTable(Map<String, Type> fields) {
		dataTable.setEmptyTableWidget(new Label(LabelType.DEFAULT, messages.noEntityValues()));

		Icon loadingIndicator = new Icon(IconType.SPINNER);
		loadingIndicator.getElement().addClassName("icon-spin");
		dataTable.setLoadingIndicator(loadingIndicator);
		
		for (String fieldName : fields.keySet()) {
			if (fields.get(fieldName) == Type.File) {
				dataTable.addColumn(new EntityFileColumn(new FileCell(), fieldName), fieldName);
			} else {
				dataTable.addColumn(new EntityTextColumn(fieldName), fieldName);
			}
		}
		
		dataTable.addColumn(new EntityActionColumn(new RemoveCell(messages.removeRowButtonLabel(), presenter)), messages.actionColumnLabel());
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
				fileUpload.setName("files['" + name + "']");
				widget = fileUpload;
			break;
			default:
				TextBox textBox = new TextBox();
				textBox.setName("fields['" + name + "']");
				textBox.getElement().setAttribute("placeholder", getFieldPlaceholder(type));
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
		addEntityRowFormContainer.add(new Hidden("repositoryId", String.valueOf(repositoryId)));
		addEntityRowFormContainer.add(new Hidden("entityName", entityName));
		addEntityRowFormContainer.add(new Hidden(CsrfUtil.getCsrfParameterName(), CsrfUtil.getCsrfValue()));
	}

	@Override
	public void showTemplatesModal(boolean b) {
		codeTemplatesModal.show();
	}

	@Override
	public void renderCodeTemplates(Map<String, String> codeTemplates) {
		bashCode.getElement().setInnerHTML("<pre><code class='bash'>" + codeTemplates.get("bash") + "</code></pre>");
		bashCode.getElement().setId("hljs-bash");
		rubyCode.getElement().setInnerHTML("<pre><code class='ruby'>" + codeTemplates.get("ruby") + "</code></pre>");
		rubyCode.getElement().setId("hljs-ruby");
		pythonCode.getElement().setInnerHTML("<pre><code class='python'>" + codeTemplates.get("python") + "</code></pre>");
		pythonCode.getElement().setId("hljs-python");
		doHighlightMarkup();
	}
	
	private String getFieldPlaceholder(Type type) {
		String placeholder = messages.fieldPlaceholderPrefix() + " ";
		String postfix = "";
		
		switch(type) {
			case Boolean:
				postfix = "true or false";
			break;
			case BooleanArray:
				postfix = "[true, false]";
			break;
			case Float:
				postfix = "3.14";
			break;
			case FloatArray:
				postfix = "[3.14, -7.56]";
			break;
			case Integer:
				postfix = "42";
			break;
			case IntegerArray:
				postfix = "[42, -56]";
			break;
			case String:
				postfix = "example";
			break;
			case StringArray:
				postfix = "[\"example1\", \"eample2\"]";
			break;
		}
		
		return placeholder + postfix;
	}

	private native void doHighlightMarkup() /*-{
		var block = $doc.getElementById("hljs-bash");
		$wnd.hljs.highlightBlock(block);
		
		block = $doc.getElementById("hljs-ruby");
		$wnd.hljs.highlightBlock(block);
		
		block = $doc.getElementById("hljs-python");
		$wnd.hljs.highlightBlock(block);
	}-*/;

	@Override
	public boolean confirmRowRemoval() {
		return Window.confirm(messages.rowRemovalConfirmation());
	}
}