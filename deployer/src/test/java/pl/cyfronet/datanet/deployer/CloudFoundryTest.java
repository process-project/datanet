package pl.cyfronet.datanet.deployer;

import java.io.IOException;
import java.util.Properties;

import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.junit.Ignore;

import pl.cyfronet.datanet.deployer.test.PropertiesLoader;

@Ignore
public class CloudFoundryTest {
	
	protected Properties props;
	
	protected final String CF_TARGET;
	protected final String CF_USER;
	protected final String CF_PASS;

	protected CloudFoundryClient client;
	
	public CloudFoundryTest() throws IOException {
		props = PropertiesLoader.loadEnvProperties();
		CF_TARGET = props.getProperty("cf.target");
		CF_USER = props.getProperty("cf.user");
		CF_PASS = props.getProperty("cf.pass");
	}
	
}
