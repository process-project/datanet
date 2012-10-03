package pl.cyfronet.datanet.deployer.test;

import java.net.MalformedURLException;
import java.net.URL;

import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DeploymentPerformance {
	private static final String CF_TARGET = System.getProperty("cf.target");
	private static final String CF_USER = System.getProperty("cf.user");
	private static final String CF_PASS = System.getProperty("cf.pass");
	
	private CloudFoundryClient client;
	
	@Before
	public void setup() throws MalformedURLException {
		client = new CloudFoundryClient(new CloudCredentials(CF_USER, CF_PASS), new URL(CF_TARGET));
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