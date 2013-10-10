package pl.cyfronet.datanet.web.client.widgets.readonly.fieldpanel;

import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import pl.cyfronet.datanet.model.beans.Field;
import pl.cyfronet.datanet.model.beans.Type;
import pl.cyfronet.datanet.web.client.widgets.readonly.fieldpanel.FieldPanelPresenter.View;

import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class FieldPanelPresenterTest {

	private FieldPanelPresenter presenter;
	
	@Mock
	private View view;

	@Before
	public void prepare() {
		MockitoAnnotations.initMocks(this);
		presenter = new FieldPanelPresenter(view);
	}
	
	@Test
	public void shouldRenderCollectionField() throws Exception {
		Field field = new Field();
		field.setName("string_collection");
		field.setType(Type.StringArray);
		
		presenter.setField(field);
		
		verify(view).setType("String[]");
	}
}
