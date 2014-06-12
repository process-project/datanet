package pl.cyfronet.datanet.deployer;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipException;

import org.apache.commons.io.FileUtils;

public class ZipFileMapperBuilder implements MapperBuilder {
	private static final String MODEL_DIR_NAME_DEFAULT = "model";
	private static final String TOKEN_FILE_PATH = "config/.secret";
	
	private File archiveFile;
	private File outputDir;
	private File mapperDir;
	private File modelDir;

	/**
	 * 
	 * @param archiveFile Archive containing mapper.
	 * @param outputDir Directory in which the archive will be extracted. This directory will be created if it does not exist and will be removed on cleanup.
	 * @param mapperDirName Name of the mapper directory which is a subdirectory of the outputDir.
	 */
	public ZipFileMapperBuilder(File archiveFile, File outputDir,
			String mapperDirName) {
		this(archiveFile, outputDir, mapperDirName, null);
	}
	
	/**
	 * 
	 * @param archiveFile Archive containing mapper.
	 * @param outputDir Directory in which the archive will be extracted. This directory will be created if it does not exist and will be removed on cleanup.
	 * @param mapperDirName Name of the mapper directory which is a subdirectory of the outputDir.  
	 * @param modelDirName Name of the directory in which the model files will be placed. 
	 */
	public ZipFileMapperBuilder(File archiveFile, File outputDir,
			String mapperDirName, String modelDirName) {
		super();
		this.archiveFile = archiveFile;
		this.outputDir = outputDir;
		this.mapperDir = new File(outputDir, mapperDirName);
		if (modelDirName == null)
			modelDirName = MODEL_DIR_NAME_DEFAULT;
		this.modelDir = new File(mapperDir, modelDirName);
	}
	
	
	/* (non-Javadoc)
	 * @see pl.cyfronet.datanet.deployer.MapperBuilder#buildMapper(java.util.Map)
	 */
	@Override
	public File buildMapper(Map<String, String> models) throws ZipException, IOException {
		Unzip.extractOverridingAll(archiveFile, outputDir);

		if (!mapperDir.exists() || !mapperDir.isDirectory())
			throw new IllegalStateException("Mapper directory does not exist: " + mapperDir.getAbsolutePath());
		
		if (!modelDir.exists() || !modelDir.isDirectory())
			throw new IllegalStateException("Model directory does not exist: " + modelDir.getAbsolutePath());
		
		for (Entry<String, String> model : models.entrySet()) {
			String modelFileName = String.format("%s.json", model.getKey());
			File modelFile = new File(modelDir, modelFileName);
			FileUtils.writeStringToFile(modelFile, model.getValue());
		}

		return mapperDir;
	}
	
	/* (non-Javadoc)
	 * @see pl.cyfronet.datanet.deployer.MapperBuilder#deleteMapper()
	 */
	@Override
	public void deleteMapper() {
		FileUtils.deleteQuietly(mapperDir);
	}

	@Override
	public void writeToken(String token) throws IOException {
		FileUtils.writeStringToFile(new File(mapperDir, TOKEN_FILE_PATH), token);
	}	
}