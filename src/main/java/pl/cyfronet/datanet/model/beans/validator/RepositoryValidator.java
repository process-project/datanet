package pl.cyfronet.datanet.model.beans.validator;


public class RepositoryValidator {

	public boolean isValidName(String repositoryName) {
		return repositoryName != null && 
				repositoryName.matches("[a-zA-Z0-9\\-]*");
	}
}
