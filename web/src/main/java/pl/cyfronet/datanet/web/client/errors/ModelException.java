package pl.cyfronet.datanet.web.client.errors;

public class ModelException extends Exception {
	private static final long serialVersionUID = 7569975052885032757L;

	public enum Code {
		Unknown,
		ModelSaveError,
		ModelNameNotUnique,
		ModelRetrievalError,
		ModelValidationError,
	}
	
	private Code errorCode;
	
	public ModelException() {
		errorCode = Code.Unknown;
	}
	
	public ModelException(String message) {
		super(message);
		errorCode = Code.Unknown;
	}
	
	public ModelException(Code errorCode) {
		this.errorCode = errorCode;
	}
	
	public ModelException(Code errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}
	
	public Code getErrorCode() {
		return errorCode;
	}
}