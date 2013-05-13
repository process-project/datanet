package pl.cyfronet.datanet.web.client.widgets.fieldpanel;

import pl.cyfronet.datanet.model.beans.Field.Type;
import pl.cyfronet.datanet.web.client.widgets.fieldpanel.FieldPanelPresenter.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class FieldPanelWidget extends Composite implements View {
	private static FieldPanelWidgetUiBinder uiBinder = GWT	.create(FieldPanelWidgetUiBinder.class);
	interface FieldPanelWidgetUiBinder extends UiBinder<Widget, FieldPanelWidget> {}

	private Presenter presenter;

	@UiField(provided = true) ListBox type;
	@UiField TextBox name;
	@UiField Button remove;
	@UiField CheckBox required;

	public FieldPanelWidget() {
		createTypeListBox();
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("remove")
	void removeFieldClicked(ClickEvent event) {
		presenter.onRemoveField();
	}
	
	@UiHandler("name")
	void fieldNameChanged(ValueChangeEvent<String> event) {
		presenter.onFieldNameChanged(event.getValue());
	}
	
	@UiHandler("type")
	void fieldTypeChanged(ChangeEvent event) {
		ListBox source = (ListBox) event.getSource();
		int selectedIndex = source.getSelectedIndex();
		presenter.onFieldTypeChanged(Type.valueOf(source.getValue(selectedIndex)));
	}
	
	@UiHandler("required")
	void requiredFieldValueChanged(ValueChangeEvent<Boolean> event) {
		presenter.onFieldRequiredChanged(event.getValue());
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@Override
	public HasText getName() {
		return name;
	}

	@Override
	public void selectType(Type fieldType) {
		type.setSelectedIndex(fieldType.ordinal());
	}

	@Override
	public void setEditable(boolean editable) {
		type.setEnabled(editable);
		name.setEnabled(editable);
		required.setEnabled(false);
		remove.setVisible(editable);
	}

	private void createTypeListBox() {
		type = new ListBox();
		
		for(Type fieldType : Type.values()) {
			type.addItem(fieldType.name());
		}
	}

	@Override
	public void setRequired(boolean requiredValue) {
		required.setValue(requiredValue);
	}
}