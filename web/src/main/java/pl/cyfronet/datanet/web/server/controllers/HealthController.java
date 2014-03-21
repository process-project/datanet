package pl.cyfronet.datanet.web.server.controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import pl.cyfronet.datanet.deployer.Deployer;
import pl.cyfronet.datanet.web.server.controllers.beans.HealthStatus;
import pl.cyfronet.datanet.web.server.controllers.beans.HealthStatus.Status;
import pl.cyfronet.datanet.web.server.controllers.beans.ModuleStatus;
import pl.cyfronet.datanet.web.server.db.HibernateUserDao;

@Controller
public class HealthController {
	private static final Logger log = LoggerFactory.getLogger(HealthController.class);
	
	private HealthStatus healthStatus;
	
	@Autowired private HibernateUserDao userDao;
	@Autowired private Deployer deployer;

	@RequestMapping(value = "/healthcheck", produces = "application/xml")
	@ResponseBody
	public HealthStatus healthCheck() {
		return healthStatus;
	}
	
	@Scheduled(fixedDelay = 18000000) //every 5 hours
	private void performHealthCheck() {
		log.info("Performing a health check...");
		
		List<String> failures = new ArrayList<>();
		ModuleStatus databaseCheck = checkDatabase();
		
		if (databaseCheck.getStatus() != Status.ok) {
			failures.add("database failed");
		}
		
		ModuleStatus cloudFoundryCheck = checkCloudFoundry();
		
		if (cloudFoundryCheck.getStatus() != Status.ok) {
			failures.add("Cloud Foundry failed");
		}

		HealthStatus result = new HealthStatus();
		result.setDate(Calendar.getInstance().getTime());
		result.setCloudFoundry(cloudFoundryCheck);
		result.setDatabase(databaseCheck);
		
		if (failures.size() > 0) {
			result.setStatus(Status.failed);
			result.setMessage(StringUtils.collectionToCommaDelimitedString(failures));
		} else {
			result.setStatus(Status.ok);
			result.setMessage("All OK");
		}

		log.info("Health check result is {}", result);
		healthStatus = result;
	}

	private ModuleStatus checkCloudFoundry() {
		ModuleStatus moduleStatus = new ModuleStatus();
		
		try {
			//lets try to list repositories
			deployer.listRepostories();
			moduleStatus.setStatus(Status.ok);
		} catch (Throwable e) {
			log.error("Cloud Foundry health check failed", e);
			moduleStatus.setStatus(Status.failed);
			moduleStatus.setMessage(e.getMessage());
		}
		
		return moduleStatus;
	}

	private ModuleStatus checkDatabase() {
		ModuleStatus moduleStatus = new ModuleStatus();
		
		try {
			//lets try to list all the users
			userDao.getUsers();
			moduleStatus.setStatus(Status.ok);
		} catch (Throwable e) {
			log.error("Database health check failed", e);
			moduleStatus.setStatus(Status.failed);
			moduleStatus.setMessage(e.getMessage());
		}
		
		return moduleStatus;
	}
}