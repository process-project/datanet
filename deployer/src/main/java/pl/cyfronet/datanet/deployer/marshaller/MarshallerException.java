package pl.cyfronet.datanet.deployer.marshaller;

public class MarshallerException extends Exception {
	public MarshallerException(String s) {
		super(s);
	}
	
	public MarshallerException(Exception e) {
		super(e);
	}
}
