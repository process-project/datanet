package pl.cyfronet.datanet.web.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
@EnableWebMvc
@ComponentScan({"pl.cyfronet.datanet.web.server.controllers",
				"pl.cyfronet.datanet.web.server.rpcservices",
				"pl.cyfronet.datanet.web.server.services"})
public class SpringConfiguration extends WebMvcConfigurerAdapter {
	private static final int YEAR = 31556926;
	
	/**
	 * Properties configuration. The properties can later be accessed from
	 * beans by using the <code>@Value</code> annotation (e.g. <code>@Value("property.name") String property;</code>).
	 */
	@Bean
	static PropertySourcesPlaceholderConfigurer properties() {
		PropertySourcesPlaceholderConfigurer properies = new PropertySourcesPlaceholderConfigurer();
        Resource[] resourceLocations = new Resource[] {
                new ClassPathResource("datanet.properties")
        };
        properies.setLocations(resourceLocations);
        
        return properies;
	}
	
	@Bean
    public ViewResolver viewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".jsp");
        
        return viewResolver;
    }
	
	/**
	 * Resource configuration
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/ria/**").
				addResourceLocations("/ria/").setCachePeriod(YEAR);
		registry.addResourceHandler("/css/**").
				addResourceLocations("/css/").setCachePeriod(YEAR);
	}
}