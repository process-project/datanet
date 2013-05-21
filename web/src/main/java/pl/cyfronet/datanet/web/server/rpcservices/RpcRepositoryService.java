package pl.cyfronet.datanet.web.server.rpcservices;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import pl.cyfronet.datanet.deployer.Deployer;
import pl.cyfronet.datanet.deployer.DeployerException;
import pl.cyfronet.datanet.deployer.marshaller.MarshallerException;
import pl.cyfronet.datanet.deployer.marshaller.ModelSchemaGenerator;
import pl.cyfronet.datanet.model.beans.Model;
import pl.cyfronet.datanet.web.client.errors.ModelException;
import pl.cyfronet.datanet.web.client.errors.ModelException.Code;
import pl.cyfronet.datanet.web.client.services.RepositoryService;
import pl.cyfronet.datanet.web.server.db.HibernateModelDao;
import pl.cyfronet.datanet.web.server.db.HibernateRepositoryDao;
import pl.cyfronet.datanet.web.server.db.HibernateUserDao;
import pl.cyfronet.datanet.web.server.db.beans.ModelDbEntity;
import pl.cyfronet.datanet.web.server.db.beans.RepositoryDbEntity;
import pl.cyfronet.datanet.web.server.db.beans.UserDbEntity;

@Service("repositoryService")
public class RpcRepositoryService implements RepositoryService {
	private static final Logger log = LoggerFactory.getLogger(RpcRepositoryService.class);
	
	@Autowired private Deployer deployer;
	@Autowired private ModelSchemaGenerator modelSchemaGenerator;
	@Autowired private HibernateUserDao userDao;
	@Autowired private HibernateModelDao modelDao;
	@Autowired private HibernateRepositoryDao repositoryDao;
	
	@Override
	public void deployModel(Model model) throws ModelException {
		try {
			Map<String, String> models = modelSchemaGenerator.generateSchema(model);
			deployer.deployRepository(Deployer.RepositoryType.Mongo, model.getName(), models);
			
			UserDbEntity user = getUser();
			ModelDbEntity modelDbEntity = modelDao.getModel(model.getId());
			
			RepositoryDbEntity repository = new RepositoryDbEntity();
			repository.setName(model.getName());
			if(repository.getOwners() == null) {
				repository.setOwners(new LinkedList<UserDbEntity>());
			}
			repository.getOwners().add(user);
			repository.setSourceModel(modelDbEntity);
			
			repositoryDao.saveRepository(repository);
		} catch (MarshallerException e) {
			String message = "Could not marshall model";
			log.error(message, e);
			throw new ModelException(Code.ModelDeployError);
		} catch (DeployerException de) {
			log.error("Deployer authorization failure", de);
			throw new ModelException(Code.ModelDeployError);
		}
	}

	@Override
	public List<String> getRepositories() throws ModelException {
		try {
			UserDbEntity userDbEntity = getUser();
			
			List<String> repositoryNames = new LinkedList<>();
			for(RepositoryDbEntity repositoryDbEntity : repositoryDao.getUserRepositories(userDbEntity.getLogin())) {
				String repositoryName = repositoryDbEntity.getName();
				repositoryNames.add(repositoryName);
			}
			return repositoryNames;
		} catch (Exception e) {
			String message = "Could not read available repositories";
			log.error(message, e);
			throw new ModelException(Code.RepositoryRetrievalError);
		}
	}

	@Override
	public void undeployRepository(String repositoryName) throws ModelException {
		try {
			deployer.undeployRepository(repositoryName);
			RepositoryDbEntity repository = repositoryDao.getRepositoryByName(repositoryName);
			repositoryDao.deleteRepository(repository);
		} catch (DeployerException e) {
			log.error("Deployer undeploy repository failure", e);
			throw new ModelException(Code.RepositoryUndeployError);
		}
	}
	
	private UserDbEntity getUser() {
		String login = (String) RequestContextHolder.getRequestAttributes().
				getAttribute("userLogin", RequestAttributes.SCOPE_SESSION);
		UserDbEntity user = userDao.getUser(login);
		return user;
	}
}