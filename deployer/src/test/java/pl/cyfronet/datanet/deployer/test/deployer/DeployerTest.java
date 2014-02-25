package pl.cyfronet.datanet.deployer.test.deployer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import pl.cyfronet.datanet.deployer.ApplicationConfig;
import pl.cyfronet.datanet.deployer.Deployer;
import pl.cyfronet.datanet.deployer.DeployerException;
import pl.cyfronet.datanet.deployer.MapperBuilderFactory;
import pl.cyfronet.datanet.deployer.ZipByteArrayMapperBuilderFactory;
import pl.cyfronet.datanet.deployer.test.SpringTestContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = SpringTestContext.class)
public class DeployerTest {
	private final static Logger log = LoggerFactory.getLogger(CloudfoundryInfrastructureTest.class);
	
	@Value("${cf.target}") private String cfTarget;
	@Value("${cf.user}") private String cfUser;
	@Value("${cf.pass}") private String cfPass;

	private static final String REPOSITORY_NAME = "bwtest-mongodb";
	private static final String ZIP_NAME = "datanet-skel-mongodb.zip";
	private static final String UNZIP_PATH = "/tmp/cloudfoundry-test-trash";
	private static final String APP_FOLDER_NAME = "datanet-skel-mongodb";
	
	private String repositoryName;
	
	private Deployer deployer;
	
	@Before
	public void prepare() throws URISyntaxException, IOException {
		Date date = new Date();
		Random random = new Random();
		
		String uniqueComponent = String.format("_%d_%d", date.getTime(), Math.abs(random.nextInt()));
		repositoryName = REPOSITORY_NAME + uniqueComponent;
		
		InputStream zipStream = this.getClass().getClassLoader()
				.getResourceAsStream(ZIP_NAME);
		
		Map <Deployer.RepositoryType, MapperBuilderFactory> builderMap = new HashMap<Deployer.RepositoryType, MapperBuilderFactory>();
		builderMap.put(Deployer.RepositoryType.Mongo, new ZipByteArrayMapperBuilderFactory(IOUtils.toByteArray(zipStream), new File(UNZIP_PATH), APP_FOLDER_NAME));
		deployer = new Deployer(cfUser, cfPass, cfTarget, new ApplicationConfig(), builderMap);
	}
	
	@Test
	public void deployAndCleanup() throws DeployerException {
		sampleDeploy();
		sampleUndeploy();
	}

	private void sampleDeploy() throws DeployerException {
		Map<String, String> models = new HashMap<String, String>();
		models.put("entity123", "test{\"type\": \"object\"}");
		log.debug("deploying aplication: {}", models);
		deployer.deployRepository(Deployer.RepositoryType.Mongo, repositoryName, models, "token");
	}

	private void sampleUndeploy() throws DeployerException {
		deployer.undeployRepository(repositoryName);
	}
}