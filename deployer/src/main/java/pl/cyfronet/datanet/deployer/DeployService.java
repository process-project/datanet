package pl.cyfronet.datanet.deployer;

import java.util.List;

import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.client.lib.domain.ServiceConfiguration;
import org.slf4j.Logger;

public class DeployService {

	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(DeployService.class);
	
	private CloudFoundryClient client;
	private String repositoryName;
	private String serviceType;
	private String serviceName;
	
	public DeployService(CloudFoundryClient client, String repositoryName, String serviceType) {
		this.serviceType = serviceType;
		this.repositoryName = repositoryName;
		this.client = client;
	}
	
	public CloudService execute() throws DeployerException {
		ServiceConfiguration serviceConfiguration = findServiceConfiguration();
		return createNewService(serviceConfiguration);
	}
	
	public void rollback() {
		if (serviceName != null) {
			logger.debug(String.format("Rollback for service '%s' executed", serviceName));
			try {
				CloudService service = null;
				try {
					service = client.getService(serviceName); // runtime exception on failure - will be caught
				}
				catch (Exception e) {
					// do nothing 
				}
				if (service != null) {
					logger.debug(String.format("Deleting service '%s'", serviceName));
					client.deleteService(serviceName);
					logger.debug(String.format("Service '%s' deleted", serviceName));
				}
				serviceName = null;
			} catch (Exception e) {
				logger.warn("Error while rolling back service deployment");
			}
		}
	}
	
	private CloudService createNewService(ServiceConfiguration serviceConfiguration) throws DeployerException {
		try {
			serviceName = getServiceNameForRepo(repositoryName);
			CloudService service = new CloudService();
			service.setName(serviceName);
			service.setTier(serviceConfiguration.getTiers().get(0).getType());
			service.setType(serviceConfiguration.getType());
			service.setVersion(serviceConfiguration.getVersion());
			service.setVendor(serviceConfiguration.getVendor());
			logger.debug(String.format("Creating new service '%s'", serviceName));
			client.createService(service);
			logger.debug(String.format("Service '%s' created", serviceName));
			return service;
		}
		catch (Exception e) {
			throw new DeployerException("Error while deploying CloudFoundry service for repository", e);
		}
	}
	
	private ServiceConfiguration findServiceConfiguration() throws DeployerException {
		try {
			ServiceConfiguration databaseServiceConfiguration = null;
			List<ServiceConfiguration> serviceConfigurations = client.getServiceConfigurations();
			for (ServiceConfiguration sc : serviceConfigurations) {
				if ((sc.getVendor() != null && sc.getVendor().equals(serviceType))) {
					databaseServiceConfiguration = sc;
					break;
				}
			}
			if (databaseServiceConfiguration == null) {
				throw new IllegalStateException(
						"No ServiceConfiguration found for " + serviceType);
			}
			return databaseServiceConfiguration;	
		}
		catch (Exception e) {
			throw new DeployerException("Error while finding configuration for appropriate service type", e);
		}
	}
	
	public static String getServiceNameForRepo(String repoName) {
		return String.format("%s", DeployApplication.getAppNameForRepo(repoName));
	}
	
}
