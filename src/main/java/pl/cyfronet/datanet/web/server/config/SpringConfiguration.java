package pl.cyfronet.datanet.web.server.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.hibernate.SessionFactory;
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
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import pl.cyfronet.datanet.model.util.JaxbEntityListBuilder;
import pl.cyfronet.datanet.model.util.ModelBuilder;

public class SpringConfiguration {
	@Value("${db.driver.class}") private String dbDriverClass;
	@Value("${db.jdbc.url}") private String dbJdbcUrl;
	@Value("${db.user}") private String dbUser;
	@Value("${db.password}") private String dbPassword;
	
	@Value("${cf.target}") private String cfTarget;
	@Value("${cf.username}") private String cfUsername;
	@Value("${cf.password}") private String cfPassword;
	@Value("${cf.unzip.path}") private String cfUnzipPath;
	@Value("${cf.app.postfix}") private String cfAppPostfix;
	
	/**
	 * Properties configuration. The properties can later be accessed from
	 * beans by using the <code>@Value</code> annotation (e.g. <code>@Value("property.name") String property;</code>).
	 */
	@Bean
	public static PropertySourcesPlaceholderConfigurer properties() {
		PropertySourcesPlaceholderConfigurer properies = new PropertySourcesPlaceholderConfigurer();
		Resource[] resourceLocations = new Resource[] {
				new ClassPathResource("datanet.properties"),
				new ClassPathResource("datanet-override.properties"),
                //properties shared with the client
				new ClassPathResource("pl/cyfronet/datanet/web/client/controller/AppProperties.properties")
        };
        properies.setLocations(resourceLocations);
        properies.setIgnoreResourceNotFound(true);
        
        return properies;
	}
	
	@Bean
	public SessionLocaleResolver localeResolver() {
	    SessionLocaleResolver localeResolver = new SessionLocaleResolver();
	    
	    return localeResolver;
	}
	
	/**
	 * REST client used to communicate with the portal to authenticate users.
	 */
	@Bean
	public RestTemplate restClient() {
		RestTemplate restTemplate = new RestTemplate();
		
		return restTemplate;
	}
}