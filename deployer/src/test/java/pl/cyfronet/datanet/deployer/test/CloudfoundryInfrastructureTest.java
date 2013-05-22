package pl.cyfronet.datanet.deployer.test;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.zip.ZipException;

import org.apache.commons.io.FileUtils;
import org.cloudfoundry.client.lib.CloudApplication;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.CloudService;
import org.cloudfoundry.client.lib.ServiceConfiguration;
import org.cloudfoundry.client.lib.ServiceConfiguration.Tier;
import org.cloudfoundry.client.lib.Staging;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pl.cyfronet.datanet.deployer.CloudFoundryTest;
import pl.cyfronet.datanet.deployer.Unzip;

public class CloudfoundryInfrastructureTest extends CloudFoundryTest {
	
	private static final String MONGODB_SERVICE_TYPE = "mongodb";
	private static final String ZIP_NAME = "datanet-skel-mongodb.zip";
	private static final String UNZIP_PATH = "/tmp/cloudfoundry-test-trash";
	
	private static final String APP_FOLDER_NAME = "datanet-skel-mongodb";
	
	private static final String SERVICE_NAME_BASE = "bw-test-service";
	private static final String APP_NAME_BASE = "bw-test-app";
	private static final int MEMORY = 128;

	private final Staging staging;
	private final List<String> uris;
	private final Map<String, String> envVarsMap;
	private final String appName;
	private final String serviceName;
				
	public CloudfoundryInfrastructureTest() throws IOException {
		
		Date date = new Date();
		Random random = new Random();
		
		staging = new Staging("rack");
		staging.setRuntime("ruby193");
		
		String uniqueComponent = String.format("_%d_%d", date.getTime(), Math.abs(random.nextInt()));
		
		appName = APP_NAME_BASE + uniqueComponent;
		serviceName = SERVICE_NAME_BASE + uniqueComponent;
		uris = asList(String.format("%s.datanet.cyfronet.pl", appName));

		envVarsMap = new HashMap<String, String>();
		envVarsMap.put("BUNDLE_WITHOUT", "test");
		envVarsMap.put("RAILS_ENV", "staging");
	}

	@Before
	public void setup() throws MalformedURLException {
		Assert.assertNotNull(CF_TARGET);
		Assert.assertNotNull(CF_USER);
		Assert.assertNotNull(CF_PASS);
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
	public void uploadAndStartApplication() throws IOException, URISyntaxException {
		uploadApplication(new LinkedList<String>());		
		startApplication();
		CloudApplication app = client.getApplication(appName);
		assertNotNull(app);
		assertEquals(CloudApplication.AppState.STARTED, app.getState());
		deleteApplication();
	}
	
	@Test
	public void createService() {
		createNewService(serviceName);
		assertNotNull("Service not registerred", client.getService(serviceName));
		deleteService(serviceName);
	}
	
	@Test
	public void uploadBindAndStartApplication() throws IOException, URISyntaxException {
		
		createNewService(serviceName);
		uploadApplication(asList(serviceName));
		startApplication();
		
		CloudApplication app = client.getApplication(appName);
		
		assertNotNull(app);
		assertEquals(CloudApplication.AppState.STARTED, app.getState());

		List<String> appServices = app.getServices();
		assertEquals(1, appServices.size());
		assertEquals(serviceName, appServices.get(0));
		
		deleteApplication();
		deleteService(serviceName);
	}

	//@Test
	public void deleteApplication() {
		client.deleteApplication(appName);
	}
	
	//@Test 
	public void deleteService(String serviceName) {
		client.deleteService(serviceName);
	}
	
	@Test
	public void printServices() {
		for (CloudService service: client.getServices()) {
			System.out.println(service.getName());
		}
	}

	//@Test 
	public void printServiceConfiguration() {
		for (ServiceConfiguration sc : client.getServiceConfigurations()) {
			StringBuffer tiers = new StringBuffer();
			if (sc.getTiers() != null)
				for (Tier tier: sc.getTiers()) {
					tiers.append(tier.getType() + ", ");										
				}
			String config = String.format(
					"type: %s, vendor: %s, version: %s, description: %s, tiers: %s",
					sc.getType(), 
					sc.getVendor(), 
					sc.getVersion(),
					sc.getDescription(), 
					tiers
					);
			System.out.println(config);
		}
	}
		
	private void uploadApplication(List<String> services) throws ZipException, IOException, URISyntaxException {
		client.createApplication(appName, staging, MEMORY, uris, services);
		File unzipFolder = new File(UNZIP_PATH);
		Unzip.extractOverridingAll(new File(this.getClass().getClassLoader().getResource(ZIP_NAME).toURI()), unzipFolder);
		client.uploadApplication(appName, new File(unzipFolder, APP_FOLDER_NAME));
		FileUtils.forceDelete(unzipFolder);
	}
	
	private void startApplication() {
		client.updateApplicationEnv(appName, envVarsMap);
		client.startApplication(appName);
	}

	private void createNewService(String serviceName) {
		ServiceConfiguration serviceConfiguration = findServiceConfiguration(MONGODB_SERVICE_TYPE);
		CloudService service = new CloudService();
		service.setName(serviceName);
		service.setTier(serviceConfiguration.getTiers().get(0).getType());
		service.setType(serviceConfiguration.getType());
		service.setVersion(serviceConfiguration.getVersion());
		service.setVendor(serviceConfiguration.getVendor());
		client.createService(service);
	}
	
	private ServiceConfiguration findServiceConfiguration(String name) {
		ServiceConfiguration databaseServiceConfiguration = null;
		List<ServiceConfiguration> serviceConfigurations = client.getServiceConfigurations();
		for (ServiceConfiguration sc : serviceConfigurations) {
			if ((sc.getVendor() != null && sc.getVendor().equals(name))) {
				databaseServiceConfiguration = sc;
				break;
			}
		}
		if (databaseServiceConfiguration == null) {
			throw new IllegalStateException(
					"No ServiceConfiguration found for " + name);
		}
		return databaseServiceConfiguration;
	}

}