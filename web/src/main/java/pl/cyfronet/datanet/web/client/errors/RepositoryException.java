package pl.cyfronet.datanet.web.client.errors;


public class RepositoryException extends Exception {
	private static final long serialVersionUID = 7569975052885032757L;

	public enum Code {
		Unknown,
		RepositoryRetrievalError,
		RepositoryUndeployError,
		ModelDeployError,
		RepositoryDataRetrievalError,
		RepositoryDataSavingError,
		RepositoryRemovalError,
		RepositoryAccessConfigurationUpdateError,
		RepositoryAuthorizationError
	}
	
	private Code errorCode;
	
	public RepositoryException() {
		errorCode = Code.Unknown;
	}
	
	public RepositoryException(String message) {
		super(message);
		errorCode = Code.Unknown;
	}
	
	public RepositoryException(Code errorCode) {
		this.errorCode = errorCode;
	}
	
	public RepositoryException(Code errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}
	
	public RepositoryException(Code errorCode, String message, Exception cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	public Code getErrorCode() {
		return errorCode;
	}
}