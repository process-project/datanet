package pl.cyfronet.datanet.deployer;

import java.util.HashMap;
import java.util.Map;

import org.cloudfoundry.client.lib.Staging;

/**
 * @author bwilk
 *
 */
public class ApplicationConfig {

	private final String uriPostfix;
	private final Staging staging;
	private final int memory;
	private final Map<String, String> envVars;
	
	public ApplicationConfig(String uriPostfix, Staging staging, int memory,
			Map<String, String> envVarsMap) {
		super();
		this.uriPostfix = uriPostfix;
		this.staging = staging;
		this.memory = memory;
		this.envVars = envVarsMap;
	}

	public ApplicationConfig() {
		this.uriPostfix = "datanet.cyfronet.pl";
		this.staging = new Staging("rack");
		this.staging.setRuntime("ruby193");
		this.memory = 128;
		this.envVars = new HashMap<String, String>();
		this.envVars.put("BUNDLE_WITHOUT", "test");
		this.envVars.put("RAILS_ENV", "staging");
	}

	public Staging getStaging() {
		return staging;
	}

	public int getMemory() {
		return memory;
	}

	public Map<String, String> getEnvVars() {
		return envVars;
	}

	public String getUriPostfix() {
		return uriPostfix;
	}

}
