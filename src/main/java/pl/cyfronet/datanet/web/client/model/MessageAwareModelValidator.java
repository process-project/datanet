package pl.cyfronet.datanet.web.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;

import pl.cyfronet.datanet.model.beans.Model;
import pl.cyfronet.datanet.model.beans.validator.ModelValidator;
import pl.cyfronet.datanet.model.beans.validator.ModelValidator.ModelError;

import com.google.inject.Inject;

/**
 * This validator uses MessageValidator to retrieve model errors and then
 * convert those errors into i18n'd messages. Error codes are converted into
 * camel cased strings and proper GWT messages are retrieved. In case a message code
 * does not exist original error code is returned.
 *
 */
public class MessageAwareModelValidator {
	private ModelValidator modelValidator;
	private ModelValidationMessages validationMessages;
	
	@Inject
	public MessageAwareModelValidator(ModelValidator modelValidator, ModelValidationMessages validationMessages) {
		this.modelValidator = modelValidator;
		this.validationMessages = validationMessages;
	}
	
	public List<String> validateModel(Model model) {
		List<String> results = new ArrayList<String>();
		List<ModelError> errors = modelValidator.validateModel(model);
		
		if(errors != null) {
			for(ModelError modelError : errors) {
				String camelCasedMessageCode = camelCase(modelError.name());
				
				try {
					results.add(validationMessages.getString(camelCasedMessageCode));
				} catch (MissingResourceException e) {
					results.add(modelError.name());
				}
			}
		}
		
		return results;
	}

	private String camelCase(String name) {
		String result = null;
		
		if (name != null) {
			result = name.toLowerCase();
			
			while(result.contains("_")) {
				int underscoreIndex = result.indexOf("_");
				
				if(underscoreIndex == 0) {
					//starting underscore is ignored
					result = result.replaceFirst("_", "");
				} else {
					if(underscoreIndex == result.length() - 1) {
						//ending underscore is ignored
						result = result.replaceFirst("_", "");
					} else {
						result = result.replace("_" + result.charAt(underscoreIndex + 1),
								new String(new char[] {result.charAt(underscoreIndex + 1)}).toUpperCase());
					}
				}
			}
		}
		
		return result;
	}
}