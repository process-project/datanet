package pl.cyfronet.datanet.web.client.widgets.repositorypanel;

import java.util.List;

import pl.cyfronet.datanet.web.client.widgets.repositorypanel.RepositoryPanelPresenter.View;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.CellTable;
import com.github.gwtbootstrap.client.ui.Fieldset;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.FormLabel;
import com.github.gwtbootstrap.client.ui.Legend;
import com.github.gwtbootstrap.client.ui.SimplePager;
import com.github.gwtbootstrap.client.ui.SubmitButton;
import com.github.gwtbootstrap.client.ui.Tab;
import com.github.gwtbootstrap.client.ui.TabPanel;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class RepositoryPanelWidget extends ResizeComposite implements View {
	private static RepositoryPanelWidgetUiBinder uiBinder = GWT.create(RepositoryPanelWidgetUiBinder.class);
	interface RepositoryPanelWidgetUiBinder extends UiBinder<Widget, RepositoryPanelWidget> {}
	interface RepositoryPanelWidgetStyles extends CssResource {}

	private Presenter presenter;

	@UiField Button repositoryLink;
	@UiField TabPanel tabPanel;
	@UiField RepositoryPanelMessages messages;
	
	public RepositoryPanelWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setRepositoryLink(String link) {
		repositoryLink.setHref(link);
		repositoryLink.setText(messages.repositoryLocationPrefix(link));
	}

	@Override
	public void addEntity(String entityName, List<String> fieldNames) {
		Tab tab = new Tab();
		tab.setHeading(entityName);
		tabPanel.add(tab);
		
		Form form = new Form();
		Fieldset fieldset = new Fieldset();
		form.add(fieldset);
		tab.add(form);
		
		Legend legend = new Legend(messages.repositorySearchLabel());
		fieldset.add(legend);
		
		for(String fieldName : fieldNames) {
			FormLabel label = new FormLabel(fieldName);
			fieldset.add(label);
			
			TextBox field = new TextBox();
			fieldset.add(field);
		}
		
		SubmitButton submitButton = new SubmitButton(messages.repositorySearchLabel());
		form.add(submitButton);
		
		CellTable<EntityRow> table = new CellTable<EntityRow>();
		table.setEmptyTableWidget(new Label(messages.noEntityValues()));
		table.setStriped(true);
		table.setCondensed(true);
		table.setPageSize(10);
		
		for(String fieldName : fieldNames) {
			table.addColumn(new EntityTextColumn(fieldName), fieldName);
		}
		
		EntityRowDataProvider entityRowDataProvider = new EntityRowDataProvider(entityName, presenter);
		entityRowDataProvider.addDataDisplay(table);
		
		SimplePager simplePager = new SimplePager();
		simplePager.setDisplay(table);
		
		tab.add(simplePager);
		tab.add(table);
	}

	@Override
	public void showEntity(int entityIndex) {
		tabPanel.selectTab(entityIndex);
	}
}