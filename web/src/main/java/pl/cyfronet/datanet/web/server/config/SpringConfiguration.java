package pl.cyfronet.datanet.web.server.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.hibernate.SessionFactory;
import org.pegdown.PegDownProcessor;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import pl.cyfronet.datanet.deployer.ApplicationConfig;
import pl.cyfronet.datanet.deployer.Deployer;
import pl.cyfronet.datanet.deployer.MapperBuilder;
import pl.cyfronet.datanet.deployer.ZipByteArrayMapperBuilder;
import pl.cyfronet.datanet.deployer.marshaller.ModelSchemaGenerator;
import pl.cyfronet.datanet.model.util.JaxbEntityListBuilder;
import pl.cyfronet.datanet.model.util.ModelBuilder;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Configuration
@EnableTransactionManagement
@ComponentScan({"pl.cyfronet.datanet.web.server.services",
				"pl.cyfronet.datanet.web.server.db"})
public class SpringConfiguration {
	@Value("${db.driver.class}") private String dbDriverClass;
	@Value("${db.jdbc.url}") private String dbJdbcUrl;
	@Value("${db.user}") private String dbUser;
	@Value("${db.password}") private String dbPassword;
	
	@Value("${cf.target}") private String cfTarget;
	@Value("${cf.username}") private String cfUsername;
	@Value("${cf.password}") private String cfPassword;
	@Value("${cf.unzip.path}") private String cfUnzipPath;
	
	/**
	 * Properties configuration. The properties can later be accessed from
	 * beans by using the <code>@Value</code> annotation (e.g. <code>@Value("property.name") String property;</code>).
	 */
	@Bean
	public static PropertySourcesPlaceholderConfigurer properties() {
		PropertySourcesPlaceholderConfigurer properies = new PropertySourcesPlaceholderConfigurer();
        Resource[] resourceLocations = new Resource[] {
                new ClassPathResource("datanet.properties"),
                new ClassPathResource("datanet-override.properties")
        };
        properies.setLocations(resourceLocations);
        properies.setIgnoreResourceNotFound(true);
        
        return properies;
	}
	
	@Bean
	public ResourceBundleMessageSource messageSource() {
		ResourceBundleMessageSource messages = new ResourceBundleMessageSource();
		messages.setBasenames("datanet-messages");
		
		return messages;
	}
	
	@Bean
	public SessionLocaleResolver localeResolver(){
	    SessionLocaleResolver localeResolver = new SessionLocaleResolver();
	    localeResolver.setDefaultLocale(new Locale("en"));
	    
	    return localeResolver;
	}

	@Bean
	public ModelBuilder modelBuilder() {
		return new ModelBuilder();
	}

	@Bean
	public JaxbEntityListBuilder jaxbEntityListBuilder() {
		return new JaxbEntityListBuilder();
	}
	
	/**
	 * Standard view resolver used to find jsp views.
	 */
	@Bean
    public ViewResolver viewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".jsp");
        
        return viewResolver;
    }
	
	/**
	 * REST client used to communicate with the portal to authenticate users.
	 */
	@Bean
	public RestTemplate restClient() {
		RestTemplate restTemplate = new RestTemplate();
		
		return restTemplate;
	}
	
	/**
	 * Database configuration
	 * @throws Exception 
	 */
	@Bean
	public FactoryBean<SessionFactory> createSessionFactory() throws Exception {
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		dataSource.setDriverClass(dbDriverClass);
		dataSource.setJdbcUrl(dbJdbcUrl);
		dataSource.setUser(dbUser);
		dataSource.setPassword(dbPassword);
		
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(dataSource);
		sessionFactory.setPackagesToScan(new String[] {"pl.cyfronet.datanet.web.server.db.beans"});
		sessionFactory.setHibernateProperties(hibernateProperties().getObject());
		
		return sessionFactory;
	}
	@Bean
	public HibernateTransactionManager transactionManager() throws Exception{
		HibernateTransactionManager txManager = new HibernateTransactionManager();
		txManager.setSessionFactory(createSessionFactory().getObject());
		
		return txManager;
	}
	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation(){
		return new PersistenceExceptionTranslationPostProcessor();
	}
	@Bean HibernateExceptionTranslator hibernateExceptionTranslator() {
		return new HibernateExceptionTranslator();
	}
	@Bean
	public FactoryBean<Properties> hibernateProperties() {
		PropertiesFactoryBean pfb = new PropertiesFactoryBean();
		pfb.setLocation(new ClassPathResource("hibernate.properties"));
		
		return pfb;
	}
	
	@Bean
	public Deployer deployer() throws URISyntaxException, IOException {
		final String ZIP_NAME = "datanet-skel-mongodb.zip";
		final String APP_FOLDER_NAME = "datanet-skel-mongodb";

		InputStream zipStream = this.getClass().getClassLoader()
				.getResourceAsStream(ZIP_NAME);
		Map<Deployer.RepositoryType, MapperBuilder> builderMap = new HashMap<Deployer.RepositoryType, MapperBuilder>();
		builderMap.put(Deployer.RepositoryType.Mongo, new ZipByteArrayMapperBuilder(IOUtils.toByteArray(zipStream),
				new File(cfUnzipPath), APP_FOLDER_NAME));
		Deployer deployer = new Deployer(cfUsername, cfPassword, cfTarget,
				new ApplicationConfig(), builderMap);
		return deployer;
	}
	
	@Bean
	public ModelSchemaGenerator modelMarshaller() {
		return new ModelSchemaGenerator();
	}
	
	@Bean
	public RestTemplate restTemplate() {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		BasicCredentialsProvider credentialsProvider =  new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("", ""));
		httpClient.setCredentialsProvider(credentialsProvider);
		HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory =
				new HttpComponentsClientHttpRequestFactory(httpClient);
		
		return new RestTemplate(httpComponentsClientHttpRequestFactory);
	}
	
	@Bean
	public StandardServletMultipartResolver multipartResolver() {
		return new StandardServletMultipartResolver();
	}
	
	@Bean
	public PegDownProcessor markdownProcessor() {
		return new PegDownProcessor();
	}
}