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
import org.slf4j.LoggerFactory;

public class Deployer {
	private static final Logger log = LoggerFactory.getLogger(Deployer.class);
	
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
	 * @param repositoryName
	 * @param models
	 * @return repository URL
	 * @throws DeployerException
	 */
	public String deployRepository(RepositoryType repositoryType, String repositoryName, Map<String, String> models) throws DeployerException {
		MapperBuilder builder = builders.get(repositoryType);
		String url = null;
		
		if (builder == null) {
			log.warn("Mapper builder for service type '{}' not found", repositoryType.getServiceTypeName());
			throw new DeployerException("Mapper builder for service type '" + repositoryType.getServiceTypeName() + "' not found");
		}
		
		log.debug("Deploying repository '{}'", repositoryName);
		
		CloudFoundryClient client = prepareNewClient();
		DeployService deployService = null;
		DeployApplication deployApplication = null;
		
		try {
			checkNamingConflicts(client, repositoryName);
			File mapperDirectory = builder.buildMapper(models);
			deployService = new DeployService(client, repositoryName, repositoryType.getServiceTypeName());
			
			url = createUriForNewRepository(repositoryName);
			CloudService service = deployService.execute();
			deployApplication = new DeployApplication(client, appConfig, repositoryName, service.getName(), mapperDirectory, url);
			deployApplication.execute();
			
			log.debug("Repository '{}' successfully deployed. URL: http://{}", repositoryName, url);
		} catch (IOException e) {
			log.warn("Repository '{}' deployment failed", repositoryName, e);
		} catch (DeployerException e) {
			log.warn("Repository '{}' deployment failed", repositoryName, e);
			
			if (deployService != null) 
				deployService.rollback();
			if (deployApplication != null) 
				deployApplication.rollback();
		}
		
		builder.deleteMapper();
		client.logout();
		
		return "http://" + url;
	}
	
	public List<String> listRepostories() throws DeployerException {
		log.debug("Listing repositories");
		CloudFoundryClient client = prepareNewClient();
		List<CloudApplication> cloudApplications = client.getApplications();
		ArrayList<String> repositories = new ArrayList<>(cloudApplications.size());
		for (CloudApplication application : cloudApplications) {
			repositories.add(application.getName());
		}
		return repositories;
	}
	
	public void undeployRepository(String repositoryName) throws DeployerException {
		log.debug("Undeploying repository '{}'", repositoryName);
		String appNameForRepo = DeployApplication.getAppNameForRepo(repositoryName);
		CloudFoundryClient client = prepareNewClient();
		
		try {
			CloudApplication application = client.getApplication(appNameForRepo);
			List<String> services = application.getServices();
			client.deleteApplication(appNameForRepo);
			for (String serviceName: services) 
				client.deleteService(serviceName);
			log.debug("Repository '{}' successfully undeployed", repositoryName);
		}
		catch (Exception e) {
			log.warn("Repository '{}' undeployment failed", repositoryName, e);
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
		} catch (Exception e) {
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