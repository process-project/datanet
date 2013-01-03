package pl.cyfronet.datanet.deployer.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;

import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DeploymentPerformanceTest {

	private final String CF_TARGET;
	private final String CF_USER;
	private final String CF_PASS;
	
	private CloudFoundryClient client;
	
	public DeploymentPerformanceTest() throws IOException {
		Properties props = PropertiesLoader.loadEnvProperties();
		CF_TARGET = props.getProperty("cf.target");
		CF_USER = props.getProperty("cf.user");
		CF_PASS = props.getProperty("cf.pass");
	}
	
	@Before
	public void setup() throws MalformedURLException {
		client = new CloudFoundryClient(CF_USER, CF_PASS, CF_TARGET);
		client.login();
	}
	
	@After
	public void cleanup() {
		if(client != null) {
			client.logout();
		}
	}
	
	@Test
	public void singleAppDeployment() {
		Assert.assertTrue(client.getCloudInfo() != null);
		Assert.assertTrue(client.getApplications() != null);
	}
}