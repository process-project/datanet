package pl.cyfronet.datanet.web.server.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
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
		
		Dynamic gwtServlet = servletContext.addServlet("gwtServices", new SpringGwtRemoteServiceServlet(root));
		gwtServlet.addMapping("/rpcservices/*");

		Dynamic springServlet = servletContext.addServlet("spring", new DispatcherServlet(root));
		springServlet.setLoadOnStartup(1);
		springServlet.addMapping("/");
		log.info("Datanet web application successfully initialized");
	}
}