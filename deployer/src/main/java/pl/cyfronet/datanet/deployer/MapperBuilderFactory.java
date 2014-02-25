package pl.cyfronet.datanet.deployer;

import java.io.File;

public abstract class MapperBuilderFactory {
	
	private byte[] zipData;
	private File outputDir;
	private String mapperDirName;

	public MapperBuilderFactory(byte[] zipData, File outputDir,
			String mapperDirName) {
		this.zipData = zipData;
		this.outputDir = outputDir;
		this.mapperDirName = mapperDirName;
	}
	
	public MapperBuilder create() {
		File targetOutputDir = new File(outputDir, System.currentTimeMillis() + "");
		return newBuilder(zipData, targetOutputDir, mapperDirName);
	}
	
	protected abstract MapperBuilder newBuilder(byte[] zipData, File outputDir,
			String mapperDirName);
}
