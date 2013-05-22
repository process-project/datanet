package pl.cyfronet.datanet.deployer;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipException;

import org.cloudfoundry.client.lib.CloudApplication;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.CloudFoundryException;
import org.cloudfoundry.client.lib.CloudService;
import org.slf4j.Logger;

public class Deployer {
	
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(Deployer.class);
	
	public enum RepositoryType {
		Mongo("mongodb");
		private String serviceTypeName;
			
		public String getServiceTypeName() {
			return serviceTypeName;
		}
		RepositoryType(String serviceTypeName) {
			this.serviceTypeName = serviceTypeName;
		}
	}
	
	private URL cloudControllerUrl;
	private String email;
	private String password;

	private ApplicationConfig appConfig;
	private Map<RepositoryType, MapperBuilder> builders;
	
	public Deployer(String email, String password, String cloudControllerUrl, ApplicationConfig appConfig, Map<RepositoryType, MapperBuilder> builders) 
			throws MalformedURLException {
		this.email = email;
		this.password = password;
		this.cloudControllerUrl = new URL(cloudControllerUrl);
		this.builders = builders;
		this.appConfig = appConfig;
	}
	
	/**
	 * 
	 * @param repositoryType
	 * @param models key: entity name, value: entity JSON Schema
	 * @throws DeployerException 
	 * @throws IOException 
	 * @throws ZipException 
	 */
	public void deployRepository(RepositoryType repositoryType, String repositoryName, Map<String, String> models) throws DeployerException {
		MapperBuilder builder = builders.get(repositoryType);
		if (builder == null) {
			logger.warn(String.format("Mapper builder for service type '%s' not found", repositoryType.getServiceTypeName()));
			return; 
		}
		logger.debug(String.format("Deploying repository '%s'", repositoryName));
		
		CloudFoundryClient client = prepareNewClient();
		DeployService deployService = null;
		DeployApplication deployApplication = null;
		
		try {
			checkNamingConflicts(client, repositoryName);
			File mapperDirectory = builder.buildMapper(models);
			deployService = new DeployService(client, repositoryName, repositoryType.getServiceTypeName());
			
			String uri = createUriForNewRepository(repositoryName);
			CloudService service = deployService.execute();
			deployApplication = new DeployApplication(client, appConfig, repositoryName, service.getName(), mapperDirectory, uri);
			deployApplication.execute();
			
			logger.debug(String.format("Repository '%s' successfully deployed. URL: http://%s", repositoryName, uri));
		} catch (IOException e) {
			logger.warn(String.format("Repository '%s' deployment failed", repositoryName), e);
		}
		catch (DeployerException e) {
			logger.warn(String.format("Repository '%s' deployment failed", repositoryName), e);
			if (deployService != null) 
				deployService.rollback();
			if (deployApplication != null) 
				deployApplication.rollback();
		}
		builder.deleteMapper();
		client.logout();
	}
	
	public List<String> listRepostories() throws DeployerException {
		logger.debug("Listing repositories");
		CloudFoundryClient client = prepareNewClient();
		List<CloudApplication> cloudApplications = client.getApplications();
		ArrayList<String> repositories = new ArrayList<>(cloudApplications.size());
		for (CloudApplication application : cloudApplications) {
			repositories.add(application.getName());
		}
		return repositories;
	}
	
	public void undeployRepository(String repositoryName) throws DeployerException {
		logger.debug(String.format("Undeploying repository '%s'", repositoryName));
		String appNameForRepo = DeployApplication.getAppNameForRepo(repositoryName);
		CloudFoundryClient client = prepareNewClient();
		try {
			CloudApplication application = client.getApplication(appNameForRepo);
			List<String> services = application.getServices();
			client.deleteApplication(appNameForRepo);
			for (String serviceName: services) 
				client.deleteService(serviceName);
			logger.debug(String.format("Repository '%s' successfully undeployed", repositoryName));
		}
		catch (Exception e) {
			logger.warn(String.format("Repository '%s' undeployment failed", repositoryName), e);
		}
		client.logout();
	}

	private String createUriForNewRepository(String repositoryName) {
		return String.format("%s.%s", repositoryName, appConfig.getUriPostfix());
	}
	
	private CloudFoundryClient prepareNewClient() throws DeployerException {
		CloudFoundryClient client = new CloudFoundryClient(email, password, null, cloudControllerUrl);
		try {
			client.login();
		} catch (CloudFoundryException cfe) {
			throw new DeployerException("Authorization failure.");
		}
		return client;
	}
	
	static void checkNamingConflicts(CloudFoundryClient client, String repositoryName) throws DeployerException {
		CloudApplication application = null;
		CloudService service = null;
		String appNameForRepo = DeployApplication.getAppNameForRepo(repositoryName);
		String serviceNameForRepo = DeployService.getServiceNameForRepo(repositoryName);
		try {
			application = client.getApplication(appNameForRepo);
			service = client.getService(serviceNameForRepo);
		}
		catch (Exception e) {
			// do nothing  
		}
		if (application != null && service != null) 
			throw new DeployerException(String.format("Repository '%s' is already registerred", repositoryName));
		if (application != null) 
			throw new DeployerException(String.format("Repository application '%s' is already registerred", appNameForRepo));
		if (service != null) 
			throw new DeployerException(String.format("Repository service '%s' is already registerred", serviceNameForRepo));
	}


}