package pl.cyfronet.datanet.web.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
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
	 * Resource configuration
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/ria/**").addResourceLocations("/ria/").setCachePeriod(YEAR);
		registry.addResourceHandler("/css/**").addResourceLocations("/css/").setCachePeriod(YEAR);
		registry.addResourceHandler("/img/**").addResourceLocations("/img/").setCachePeriod(YEAR);
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
}