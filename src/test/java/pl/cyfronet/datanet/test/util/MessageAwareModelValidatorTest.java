package pl.cyfronet.datanet.test.util;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import pl.cyfronet.datanet.model.beans.Model;
import pl.cyfronet.datanet.model.beans.validator.ModelValidator;
import pl.cyfronet.datanet.model.beans.validator.ModelValidator.ModelError;
import pl.cyfronet.datanet.web.client.model.MessageAwareModelValidator;
import pl.cyfronet.datanet.web.client.model.ModelValidationMessages;

public class MessageAwareModelValidatorTest {
	@Mock private ModelValidationMessages modelValidationMessages;
	@Mock private ModelValidator modelValidator;
	
	private MessageAwareModelValidator validator;
	
	@Before
	public void prepare() {
		MockitoAnnotations.initMocks(this);
		validator = new MessageAwareModelValidator(modelValidator, modelValidationMessages);
	}
	
	@Test
	public void testModelNull() {
		when(modelValidator.validateModel(any(Model.class))).thenReturn(Arrays.asList(new ModelError[] {ModelError.NULL_MODEL}));
		validator.validateModel(null);
		verify(modelValidationMessages).getString("nullModel");
	}
	
	@Test
	public void testSeveralModelErrors() {
		when(modelValidator.validateModel(any(Model.class))).thenReturn(Arrays.asList(
				new ModelError[] {ModelError.NULL_MODEL, ModelError.EMPTY_ENTITY_NAME, ModelError.INVALID_CHARS_ENTITY_NAME}));
		validator.validateModel(null);
		verify(modelValidationMessages).getString("nullModel");
		verify(modelValidationMessages).getString("emptyEntityName");
		verify(modelValidationMessages).getString("invalidCharsEntityName");
	}
}