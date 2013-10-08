package pl.cyfronet.datanet.web.server.services.repositoryclient;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.AbstractResource;

public class InputStreamResource extends AbstractResource {
	private static final Logger log = LoggerFactory.getLogger(InputStreamResource.class);
	
	private InputStream inputStream;
	private String description;

	public InputStreamResource(InputStream inputStream) {
		this.inputStream = inputStream;
		description = "";
	}
	
	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		log.debug("File input stream obtained");
		
		return inputStream;
	}
	
	@Override
	public long contentLength() throws IOException {
		if (inputStream != null) {
			return inputStream.available();
		}
		
		return 0;
	}
}