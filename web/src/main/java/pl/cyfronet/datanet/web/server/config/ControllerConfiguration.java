package pl.cyfronet.datanet.web.server.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan("pl.cyfronet.datanet.web.server.controllers")
public class ControllerConfiguration {}