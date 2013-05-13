package pl.cyfronet.datanet.deployer;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.zip.ZipException;

public interface MapperBuilder {

	public File buildMapper(Map<String, String> models)
			throws ZipException, IOException;

	public void deleteMapper();

}