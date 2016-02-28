package pl.cyfronet.datanet.web;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.persistence.EntityManagerFactory;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.orm.jpa.vendor.HibernateJpaSessionFactoryBean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.client.RestTemplate;

import pl.cyfronet.datanet.model.util.JaxbEntityListBuilder;
import pl.cyfronet.datanet.model.util.ModelBuilder;
import pl.cyfronet.datanet.web.server.util.NegativePostParamRequestMatcher;
import pl.cyfronet.datanet.web.server.util.SpringGwtRemoteServiceServlet;

@SpringBootApplication
@PropertySource("classpath:pl/cyfronet/datanet/web/client/controller/AppProperties.properties")
public class DataNetApplication {
	private static final Logger log = LoggerFactory.getLogger(DataNetApplication.class);
	
	public static void main(String[] args) {
		new SpringApplicationBuilder(DataNetApplication.class).run(args);
		log.info("DataNet web application successfully started");
	}
	
	@Configuration
	@EnableWebSecurity
	@EnableGlobalMethodSecurity(securedEnabled = true)
	@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
	protected static class ApplicationSecurity extends WebSecurityConfigurerAdapter {
		@Autowired
		protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
			auth.inMemoryAuthentication();
		}
		
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
				.authorizeRequests()
					.antMatchers("/**")
					.permitAll()
					.and()
				.csrf()
					//the following matches all POST requests which do not contain 'openid.ns'
					//in the path which basically protects all POST requests that are not originated
					//by the OpenID server
					.requireCsrfProtectionMatcher(new NegativePostParamRequestMatcher("openid.ns"))
					.and()
				.formLogin()
					.loginPage("/login")
					.and()
				.anonymous()
					.and()
				.headers()
					.disable();
		}
	}
	
	@Bean
	public ServletRegistrationBean rpcServices(ApplicationContext applicationContext) {
		return new ServletRegistrationBean(
				new SpringGwtRemoteServiceServlet(applicationContext), "/rpcservices/*");
	}
	
	@Bean
	public EmbeddedServletContainerCustomizer containerCustomizer() {
		return container -> {
			container.addErrorPages(
					new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/500.html"),
					new ErrorPage(HttpStatus.NOT_FOUND, "/404.html")
			);
		};
	}
	
	@Bean
    public HibernateJpaSessionFactoryBean sessionFactory(EntityManagerFactory emf) {
         HibernateJpaSessionFactoryBean factory = new HibernateJpaSessionFactoryBean();
         factory.setEntityManagerFactory(emf);
         
         return factory;
    }
	
	@Bean
	public ModelBuilder modelBuilder() {
		return new ModelBuilder();
	}
	
	@Bean
	public JaxbEntityListBuilder jaxbEntityListBuilder() {
		return new JaxbEntityListBuilder();
	}
	
	@Bean
	public RestTemplate restTemplate() throws NoSuchAlgorithmException, KeyManagementException {
		ThreadSafeClientConnManager connectionManager = new ThreadSafeClientConnManager();
		SSLContext sslContext = SSLContext.getInstance("SSL");
		sslContext.init(null, new TrustManager[] { new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}
		}}, new SecureRandom());
		DefaultHttpClient httpClient = new DefaultHttpClient(connectionManager);
		SSLSocketFactory sslSocketFactory = new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, sslSocketFactory));
		BasicCredentialsProvider credentialsProvider =  new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("", ""));
		httpClient.setCredentialsProvider(credentialsProvider);
		HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory =
				new HttpComponentsClientHttpRequestFactory(httpClient);
		
		return new RestTemplate(httpComponentsClientHttpRequestFactory);
	}
}