package pl.cyfronet.datanet.web.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {
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
				//the following matches all POST requests which do not contain 'openid.ns' in the path
				//which basically protects all POST requests that are not originated by the OpenID server
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