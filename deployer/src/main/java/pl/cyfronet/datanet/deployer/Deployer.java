package pl.cyfronet.datanet.deployer;

import java.util.Map;

public class Deployer {
	public enum RepositoryType {
		Mongo
	}
	/**
	 * 
	 * @param repositoryType
	 * @param models key: entity name, value: entity JSON Schema
	 */
	public void deployRepository(RepositoryType repositoryType, String repositoryName, Map<String, String> models) {
		
	}
}