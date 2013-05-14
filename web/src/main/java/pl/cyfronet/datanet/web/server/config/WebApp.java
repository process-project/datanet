package pl.cyfronet.datanet.web.server.config;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.servlet.DispatcherServlet;

import pl.cyfronet.datanet.web.server.util.SpringGwtRemoteServiceServlet;

public class WebApp implements WebApplicationInitializer {
	private static final Logger log = LoggerFactory.getLogger(WebApp.class);
	
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		AnnotationConfigWebApplicationContext root = new AnnotationConfigWebApplicationContext();
		root.setServletContext(servletContext);
		root.scan("pl.cyfronet.datanet.web.server.config");
		root.refresh();
		
		FilterRegistration.Dynamic characterEncodingFilter = servletContext.addFilter("characterEncodingFilter", new CharacterEncodingFilter());
		characterEncodingFilter.setInitParameter("encoding", "UTF-8");
		characterEncodingFilter.setInitParameter("forceEncoding", "true");
		characterEncodingFilter.addMappingForUrlPatterns(null, true, "/*");
		
		FilterRegistration.Dynamic requestFilter = servletContext.addFilter("requestContextFilter", new RequestContextFilter());
		requestFilter.addMappingForUrlPatterns(null, true, "/rpcservices/*");
		
		ServletRegistration.Dynamic gwtServlet = servletContext.addServlet("gwtServices", new SpringGwtRemoteServiceServlet(root));
		gwtServlet.addMapping("/rpcservices/*");

		ServletRegistration.Dynamic springServlet = servletContext.addServlet("spring", new DispatcherServlet(root));
		springServlet.setLoadOnStartup(1);
		springServlet.addMapping("/");
		log.info("Datanet web application successfully initialized");
	}
}