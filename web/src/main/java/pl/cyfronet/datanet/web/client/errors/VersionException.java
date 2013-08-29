package pl.cyfronet.datanet.web.client.errors;


public class VersionException extends Exception {

	private static final long serialVersionUID = 3380349268395951741L;

	public enum Code {
		Unknown,
		VersionRetrievalError, 
		VersionSaveError
	}
	
	private Code errorCode;
	
	public VersionException() {
		errorCode = Code.Unknown;
	}
	
	public VersionException(String message) {
		super(message);
		errorCode = Code.Unknown;
	}
	
	public VersionException(Code errorCode) {
		this.errorCode = errorCode;
	}
	
	public VersionException(Code errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}
	
	public Code getErrorCode() {
		return errorCode;
	}
}
