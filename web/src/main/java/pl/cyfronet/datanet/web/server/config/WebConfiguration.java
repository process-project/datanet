package pl.cyfronet.datanet.web.server.config;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.InMemoryConsumerAssociationStore;
import org.openid4java.consumer.InMemoryNonceVerifier;
import org.openid4java.discovery.Discovery;
import org.openid4java.discovery.html.HtmlResolver;
import org.openid4java.discovery.yadis.YadisResolver;
import org.openid4java.server.RealmVerifierFactory;
import org.openid4java.util.HttpFetcherFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

@Configuration
@EnableWebMvc
@ComponentScan({"pl.cyfronet.datanet.web.server.controllers",
				"pl.cyfronet.datanet.web.server.rpcservices"})
public class WebConfiguration extends WebMvcConfigurerAdapter {
	private static final int YEAR = 31556926;
	
	/**
	 * Web resources configuration
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/ria/**").addResourceLocations("/ria/").setCachePeriod(YEAR);
		registry.addResourceHandler("/css/**").addResourceLocations("/css/").setCachePeriod(YEAR);
		registry.addResourceHandler("/img/**").addResourceLocations("/img/").setCachePeriod(YEAR);
		registry.addResourceHandler("/highlight/**").addResourceLocations("/highlight/").setCachePeriod(YEAR);
		registry.addResourceHandler("/docs/manual/resources/**").addResourceLocations("/manual/resources/").setCachePeriod(YEAR);
		registry.addResourceHandler("/docs/resources/**").addResourceLocations("/manual/resources/").setCachePeriod(YEAR);
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
	}
	
	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
		localeChangeInterceptor.setParamName("locale");
		
		return localeChangeInterceptor;
	}
	
	@Bean @Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
	public ConsumerManager openIdManager() throws NoSuchAlgorithmException, KeyManagementException {
		TrustManager[] tma = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs,
					String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs,
					String authType) {
			}
		} };

		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, tma, new java.security.SecureRandom());

		HttpFetcherFactory hff = new HttpFetcherFactory(sc);
		YadisResolver yr = new YadisResolver(hff);
		RealmVerifierFactory rvf = new RealmVerifierFactory(yr);
		Discovery d = new Discovery(new HtmlResolver(hff), yr,
				Discovery.getXriResolver());
		ConsumerManager consumerManager = new ConsumerManager(rvf, d, hff);
		consumerManager.setAssociations(new InMemoryConsumerAssociationStore());
		consumerManager.setNonceVerifier(new InMemoryNonceVerifier(5000));
		
		return consumerManager;
	}
}