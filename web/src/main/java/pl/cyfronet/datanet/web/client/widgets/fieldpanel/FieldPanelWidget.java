package pl.cyfronet.datanet.web.client.widgets.fieldpanel;

import java.util.List;

import javax.validation.constraints.NotNull;

import pl.cyfronet.datanet.web.client.widgets.fieldpanel.FieldPanelPresenter.View;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class FieldPanelWidget extends Composite implements View {
	private static FieldPanelWidgetUiBinder uiBinder = GWT
			.create(FieldPanelWidgetUiBinder.class);

	interface FieldPanelWidgetUiBinder extends
			UiBinder<Widget, FieldPanelWidget> {
	}

	private Presenter presenter;

	@UiField
	ListBox type;
	@UiField
	TextBox name;
	@UiField
	Button remove;
	@UiField
	CheckBox required;

	public FieldPanelWidget() {
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
		presenter.onFieldTypeChanged(source.getValue(selectedIndex));
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
	public void selectType(String typeName) {
		type.setSelectedValue(typeName);
	}

	@Override
	public void setRequired(boolean requiredValue) {
		required.setValue(requiredValue);
	}

	@Override
	public void setTypes(@NotNull List<String> types) {
		type.clear();
		for (String typeName : types) {
			type.addItem(typeName);
		}
	}

	@Override
	public void setName(String fieldName) {
		name.setText(fieldName);
	}
}