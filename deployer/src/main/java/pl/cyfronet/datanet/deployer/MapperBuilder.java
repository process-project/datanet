package pl.cyfronet.datanet.deployer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipException;

import org.apache.commons.io.FileUtils;

public class MapperBuilder {
	
	private static final String MODEL_DIR_NAME_DEFAULT = "model";
	
	private File archiveFile;
	private File outputDir;
	private File mapperDir;
	private File modelDir;
	private InputStream zipStream;

	/**
	 * 
	 * @param archiveFile Archive containing mapper.
	 * @param outputDir Directory in which the archive will be extracted. This directory will be created if it does not exist and will be removed on cleanup.
	 * @param mapperDirName Name of the mapper directory which is a subdirectory of the outputDir.
	 * @param models Models stored in a string map. Key: entity name ( equivalent to the model file name /*.json/ ), value: entity JSON Schema.
	 */
	public MapperBuilder(File archiveFile, File outputDir,
			String mapperDirName) {
		this(archiveFile, outputDir, mapperDirName, null);
	}
	
	public MapperBuilder(InputStream zipStream, File outputDir,
			String mapperDirName) {
		this(zipStream, outputDir, mapperDirName, null);
	}
	
	/**
	 * 
	 * @param archiveFile Archive containing mapper.
	 * @param outputDir Directory in which the archive will be extracted. This directory will be created if it does not exist and will be removed on cleanup.
	 * @param mapperDirName Name of the mapper directory which is a subdirectory of the outputDir.  
	 * @param modelDirName Name of the directory in which the model files will be placed. 
	 * @param models Models stored in a string map. Key: entity name ( equivalent to the model file name /*.json/ ), value: entity JSON Schema.
	 */
	public MapperBuilder(File archiveFile, File outputDir,
			String mapperDirName, String modelDirName) {
		super();
		this.archiveFile = archiveFile;
		this.zipStream = null;
		this.outputDir = outputDir;
		this.mapperDir = new File(outputDir, mapperDirName);
		if (modelDirName == null)
			modelDirName = MODEL_DIR_NAME_DEFAULT;
		this.modelDir = new File(mapperDir, modelDirName);
	}
	
	public MapperBuilder(InputStream zipStream, File outputDir,
			String mapperDirName, String modelDirName) {
		super();
		this.archiveFile = null;
		this.zipStream = zipStream;
		this.outputDir = outputDir;
		this.mapperDir = new File(outputDir, mapperDirName);
		if (modelDirName == null)
			modelDirName = MODEL_DIR_NAME_DEFAULT;
		this.modelDir = new File(mapperDir, modelDirName);
	}
	
	public File buildMapper(Map<String, String> models) throws ZipException, IOException {
		if(archiveFile != null) {
			Unzip.extractOverridingAll(archiveFile, outputDir);
		} else if (zipStream != null){
			Unzip.extractOverridingAll(zipStream, outputDir);
		} else {
			throw new ZipException("No file or stream given");
		}

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
	
	public void deleteMapper() {
		FileUtils.deleteQuietly(mapperDir);
	}
	
}
