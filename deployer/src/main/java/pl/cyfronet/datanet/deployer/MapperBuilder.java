package pl.cyfronet.datanet.deployer;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.zip.ZipException;

public interface MapperBuilder {
	File buildMapper(Map<String, String> models) throws ZipException, IOException;
	void deleteMapper();
	void writeToken(String token) throws IOException;
}