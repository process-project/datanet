package pl.cyfronet.datanet.deployer;

import static java.util.Arrays.asList;

import java.io.File;

import org.cloudfoundry.client.lib.CloudApplication;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.slf4j.Logger;

public class DeployApplication {
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(DeployApplication.class);

	private CloudFoundryClient client;
	private ApplicationConfig config;
	private String applicationName;
	private String repoName;
	private String serviceName;
	private File mapperDirectory;
	private String uri;
		
	public DeployApplication(CloudFoundryClient client, 
			ApplicationConfig config,
			String repoName, 
			String serviceName,
			File mapperDirectory, 
			String uri) {
		super();
		this.config = config;
		this.client = client;
		this.repoName = repoName;
		this.serviceName = serviceName;
		this.mapperDirectory = mapperDirectory;
		this.uri = uri;
	}

	public void execute() throws DeployerException {
		try {
			applicationName = getAppNameForRepo(repoName);
			logger.debug(String.format("Creating new application '%s'", applicationName));
			client.createApplication(applicationName, config.getStaging(), config.getMemory(), asList(uri), asList(serviceName));
			client.uploadApplication(applicationName, mapperDirectory);
			client.updateApplicationEnv(applicationName, config.getEnvVars());
			client.startApplication(applicationName);
			logger.debug(String.format("Application '%s' created", applicationName));
		} catch (Exception e) {
			throw new DeployerException("Error while deploying CloudFoundry application for repository", e);
		}
	}
	
	public void rollback() {
		if (applicationName != null) {
			logger.debug(String.format("Rollback for application '%s' executed", applicationName));
			try {
				CloudApplication application = null;
				try {
					application = client.getApplication(applicationName); // runtime exception on failure - will be caught
				}
				catch (Exception e) {
					// do nothing 
				}
				if (application != null) {
					logger.debug(String.format("Deleting application '%s'", applicationName));
					client.deleteApplication(applicationName);
					logger.debug(String.format("Application '%s' deleted", applicationName));
				}
				applicationName = null;
			}
			catch (Exception e) {
				logger.warn("Error while rolling back application deployment", e);
			}
		}
	}
	
	public static String getAppNameForRepo(String repoName) {
		return String.format("dnr-%s", repoName);
	}
}