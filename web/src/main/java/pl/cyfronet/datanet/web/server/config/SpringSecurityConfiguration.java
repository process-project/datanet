package pl.cyfronet.datanet.web.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/*
 * When spring-security 3.2.x (it will contain javaconfig integration) release is out the XML may be removed
 */
@Configuration
@ImportResource("classpath:spring-security-context.xml")
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {
}