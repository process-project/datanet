package pl.cyfronet.datanet.web.server.config;

import java.util.Properties;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

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
    public ViewResolver viewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix("/WEB-INF/views/");
        viewResolver.setSuffix(".jsp");
        
        return viewResolver;
    }
	
	/**
	 * REST client used to communicate with the  portal to authenticate users.
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
}