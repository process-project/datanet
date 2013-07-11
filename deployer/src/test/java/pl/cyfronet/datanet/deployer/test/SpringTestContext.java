package pl.cyfronet.datanet.deployer.test;

import java.net.MalformedURLException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class SpringTestContext {
	/**
	 * This property provider will override the file properties with those
	 * coming from the environment by sing the -D flag in the argument list.
	 */
	@Bean
	public static PropertySourcesPlaceholderConfigurer properties() throws MalformedURLException {
		PropertySourcesPlaceholderConfigurer properties = new PropertySourcesPlaceholderConfigurer();
        Resource[] resourceLocations = new Resource[] {
                new ClassPathResource("deployer-test.properties"),
                new ClassPathResource("deployer-test-override.properties"),
                new ClassPathResource(
        				PropertySourcesPlaceholderConfigurer.ENVIRONMENT_PROPERTIES_PROPERTY_SOURCE_NAME)
        };
        properties.setLocations(resourceLocations);
        properties.setIgnoreResourceNotFound(true);
        
        return properties;
	}
}