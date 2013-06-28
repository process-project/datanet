package pl.cyfronet.datanet.deployer;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;

import pl.cyfronet.datanet.deployer.ApplicationConfig;
import pl.cyfronet.datanet.deployer.Deployer;
import pl.cyfronet.datanet.deployer.MapperBuilder;

public class DeployerTest extends CloudFoundryTest {
	
	private static final String REPOSITORY_NAME = "bwtest-mongodb";
	private static final String ZIP_NAME = "datanet-skel-mongodb.zip";
	private static final String UNZIP_PATH = "/tmp/cloudfoundry-test-trash";
	private static final String APP_FOLDER_NAME = "datanet-skel-mongodb";
	
	private final String repositoryName;
	
	private Deployer deployer;
	
	public DeployerTest() throws URISyntaxException, IOException  {
		super();
		Date date = new Date();
		Random random = new Random();
		
		String uniqueComponent = String.format("_%d_%d", date.getTime(), Math.abs(random.nextInt()));
		
		repositoryName = REPOSITORY_NAME + uniqueComponent;
		
		File zip = new File(this.getClass().getClassLoader().getResource(ZIP_NAME).toURI());
		Map <Deployer.RepositoryType, MapperBuilder> builderMap = new HashMap<Deployer.RepositoryType, MapperBuilder>();
		builderMap.put(Deployer.RepositoryType.Mongo, new ZipFileMapperBuilder(zip, new File(UNZIP_PATH), APP_FOLDER_NAME));
		deployer = new Deployer(CF_USER,
			CF_PASS, 
			CF_TARGET,
			new ApplicationConfig(), 
			builderMap);
	}
	
	@Test
	@Ignore
	public void sampleDeploy() throws DeployerException {
		Map<String, String> models = new HashMap<String, String>();
		models.put("entity123", "test{\"type\": \"object\"}");
		deployer.deployRepository(Deployer.RepositoryType.Mongo, repositoryName, models);
	}
	
	@Test
	@Ignore
	public void sampleUndeploy() throws DeployerException {
		deployer.undeployRepository(repositoryName);
	}
	
	@Ignore
	@Test
	public void deployAndCleanup() throws DeployerException {
		sampleDeploy();
//		sampleUndeploy();
	}

}
