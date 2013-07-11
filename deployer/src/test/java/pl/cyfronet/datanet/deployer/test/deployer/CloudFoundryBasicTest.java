package pl.cyfronet.datanet.deployer.test.deployer;

import java.net.MalformedURLException;
import java.util.List;

import org.cloudfoundry.client.lib.CloudApplication;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.CloudInfo;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import pl.cyfronet.datanet.deployer.test.SpringTestContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class,
		classes = SpringTestContext.class)
public class CloudFoundryBasicTest {
	private final static Logger log = LoggerFactory.getLogger(CloudFoundryBasicTest.class);
	
	@Value("${cf.target}") private String cfTarget;
	@Value("${cf.user}") private String cfUser;
	@Value("${cf.pass}") private String cfPass;
	
	private CloudFoundryClient client;
	
	@Before
	public void setup() throws MalformedURLException {
		log.debug("{} {} {} ", new Object[] {cfTarget, cfUser, cfPass});
		client = new CloudFoundryClient(cfUser, cfPass, cfTarget);
		client.login();
	}
	
	@After
	public void cleanup() {
		if(client != null) {
			client.logout();
		}
	}
	
	@Test
	public void cfEnvCheck() {
		CloudInfo cloudInfo = client.getCloudInfo();
		List<CloudApplication> cloudApplications = client.getApplications();
		log.debug("cloud frameworks: {}", cloudInfo.getFrameworks());
		log.debug("cloud applications: {}", cloudApplications);
		Assert.assertTrue(cloudInfo != null);
		Assert.assertTrue(cloudApplications != null);
	}
}