package pl.cyfronet.datanet.deployer;

import java.io.File;

public class ZipByteArrayMapperBuilderFactory extends MapperBuilderFactory {

	public ZipByteArrayMapperBuilderFactory(byte[] zipData, File outputDir,
			String mapperDirName) {
		super(zipData, outputDir, mapperDirName);
	}

	@Override
	protected MapperBuilder newBuilder(byte[] zipData, File outputDir,
			String mapperDirName) {
		return new ZipByteArrayMapperBuilder(zipData, outputDir, mapperDirName);
	}
}
