package pl.cyfronet.datanet.deployer;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import pl.cyfronet.datanet.deployer.ApplicationConfig;
import pl.cyfronet.datanet.deployer.Deployer;
import pl.cyfronet.datanet.deployer.MapperBuilder;
import pl.cyfronet.datanet.deployer.test.CloudFoundryTest;

public class DeployerTest extends CloudFoundryTest {
	
	private static final String REPOSITORY_NAME = "bwtest-mongodb";
	private static final String ZIP_NAME = "datanet-skel-mongodb.zip";
	private static final String UNZIP_PATH = "/tmp/cloudfoundry-test-trash";
	private static final String APP_FOLDER_NAME = "datanet-skel-mongodb";
	
	private Deployer deployer;
	
	public DeployerTest() throws URISyntaxException, IOException  {
		super();
		File zip = new File(this.getClass().getClassLoader().getResource(ZIP_NAME).toURI());
		Map <Deployer.RepositoryType, MapperBuilder> builderMap = new HashMap<Deployer.RepositoryType, MapperBuilder>();
		builderMap.put(Deployer.RepositoryType.Mongo, new MapperBuilder(zip, new File(UNZIP_PATH), APP_FOLDER_NAME));
		deployer = new Deployer(CF_USER,
			CF_PASS, 
			CF_TARGET,
			new ApplicationConfig(), 
			builderMap);
	}
	
	@Test
	public void sampleDeploy() {
		Map<String, String> models = new HashMap<String, String>();
		models.put("entity123", "test{\"type\": \"object\"}");
		deployer.deployRepository(Deployer.RepositoryType.Mongo, REPOSITORY_NAME, models);
	}
	
	@Test
	public void sampleUndeploy() {
		deployer.undeployRepository(REPOSITORY_NAME);
	}

}
