package pl.cyfronet.datanet.web.client.errors;

public class LoginException extends Exception {
	private static final long serialVersionUID = 7569975052885032757L;

	public enum Code {
		Unknown,
		UserPasswordUnknown,
		OpenIdAssociationFailed
	}
	
	private Code errorCode;
	
	public LoginException() {
		errorCode = Code.Unknown;
	}
	
	public LoginException(Code errorCode) {
		this.errorCode = errorCode;
	}
	
	public Code getErrorCode() {
		return errorCode;
	}
}