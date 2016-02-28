package pl.cyfronet.datanet.web.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

public class WebConfiguration extends WebMvcConfigurerAdapter {
	/**
	 * Web resources configuration
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/ria/**").addResourceLocations("/ria/");
		registry.addResourceHandler("/css/**").addResourceLocations("/css/");
		registry.addResourceHandler("/fonts/**").addResourceLocations("/fonts/");
		registry.addResourceHandler("/js/**").addResourceLocations("/js/");
		registry.addResourceHandler("/img/**").addResourceLocations("/img/");
		registry.addResourceHandler("/highlight/**").addResourceLocations("/highlight/");
		
		registry.addResourceHandler("/documentation/manual/resources/**").addResourceLocations("/manual/resources/");
		registry.addResourceHandler("/documentation/tutorial/resources/**").addResourceLocations("/tutorial/resources/");
		registry.addResourceHandler("/documentation/resources/**").addResourceLocations("/manual/resources/", "/tutorial/resources/");
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