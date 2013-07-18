package pl.cyfronet.datanet.web.server.rpcservices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import pl.cyfronet.datanet.deployer.Deployer;
import pl.cyfronet.datanet.deployer.DeployerException;
import pl.cyfronet.datanet.deployer.marshaller.MarshallerException;
import pl.cyfronet.datanet.deployer.marshaller.ModelSchemaGenerator;
import pl.cyfronet.datanet.model.beans.Entity;
import pl.cyfronet.datanet.model.beans.Repository;
import pl.cyfronet.datanet.model.beans.Version;
import pl.cyfronet.datanet.model.util.JaxbEntityListBuilder;
import pl.cyfronet.datanet.web.client.errors.RepositoryException;
import pl.cyfronet.datanet.web.client.errors.RepositoryException.Code;
import pl.cyfronet.datanet.web.client.services.RepositoryService;
import pl.cyfronet.datanet.web.server.db.HibernateModelDao;
import pl.cyfronet.datanet.web.server.db.HibernateRepositoryDao;
import pl.cyfronet.datanet.web.server.db.HibernateUserDao;
import pl.cyfronet.datanet.web.server.db.HibernateVersionDao;
import pl.cyfronet.datanet.web.server.db.beans.RepositoryDbEntity;
import pl.cyfronet.datanet.web.server.db.beans.UserDbEntity;
import pl.cyfronet.datanet.web.server.db.beans.VersionDbEntity;
import pl.cyfronet.datanet.web.server.util.SpringSecurityHelper;

@Service("repositoryService")
@Secured("ROLE_USER")
public class RpcRepositoryService implements RepositoryService {
	private static final Logger log = LoggerFactory.getLogger(RpcRepositoryService.class);
	
	@Autowired private Deployer deployer;
	@Autowired private ModelSchemaGenerator modelSchemaGenerator;
	@Autowired private HibernateUserDao userDao;
	@Autowired private HibernateModelDao modelDao;
	@Autowired private HibernateVersionDao versionDao;
	@Autowired private HibernateRepositoryDao repositoryDao;
	@Autowired private JaxbEntityListBuilder jaxbEntityListBuilder;

	@Override
	public void deployModelVersion(Version modelVersion, String repositoryName) throws RepositoryException {
		try {
			Map<String, String> models = modelSchemaGenerator.generateSchema(modelVersion);
			deployer.deployRepository(Deployer.RepositoryType.Mongo, repositoryName, models);
			
			UserDbEntity user = userDao.getUser(SpringSecurityHelper.getUserLogin());
			VersionDbEntity versionDbEntity = versionDao.getVersion(modelVersion.getId());
			
			RepositoryDbEntity repository = new RepositoryDbEntity();
			repository.setName(repositoryName);

			if (repository.getOwners() == null) {
				repository.setOwners(new LinkedList<UserDbEntity>());
			}
			
			repository.getOwners().add(user);
			repository.setSourceModelVersion(versionDbEntity);
			repositoryDao.saveRepository(repository);
		} catch (MarshallerException e) {
			String message = "Could not marshall model";
			log.error(message, e);
			throw new RepositoryException(Code.ModelDeployError);
		} catch (DeployerException de) {
			log.error("Deployer authorization failure", de);
			throw new RepositoryException(Code.ModelDeployError);
		}
	}

	
	@Override
	public List<Repository> getRepositories() throws RepositoryException {
		try {
			List<Repository> repositories = new LinkedList<>();
			
			for(RepositoryDbEntity repositoryDbEntity : repositoryDao.getUserRepositories(SpringSecurityHelper.getUserLogin())) {
				Repository repository = new Repository();
				repository.setId(repositoryDbEntity.getId());
				repository.setName(repositoryDbEntity.getName());
				repository.setSourceModelVersion(repository.getSourceModelVersion());
				repositories.add(repository);
			}
			
			return repositories;
		} catch (Exception e) {
			String message = "Could not read available repositories";
			log.error(message, e);
			throw new RepositoryException(Code.RepositoryRetrievalError);
		}
	}

	@Override
	public void undeployRepository(long repositoryId) throws RepositoryException {
		try {
			RepositoryDbEntity repository = repositoryDao.getRepository(repositoryId);
			String repositoryName = repository.getName().substring(4);
			deployer.undeployRepository(repositoryName);
			repositoryDao.deleteRepository(repository);
		} catch (DeployerException e) {
			log.error("Deployer undeploy repository failure", e);
			throw new RepositoryException(Code.RepositoryUndeployError);
		}
	}

	@Override
	public Repository getRepository(long repositoryId) throws RepositoryException {
		try {
			log.debug("Retrieving repository DB bean for id {}", repositoryId);
			
			RepositoryDbEntity repositoryDbEntity = repositoryDao.getRepository(repositoryId);
			Repository repository = createRepository(repositoryDbEntity);
			
			return repository;
		} catch (Exception e) {
			String message = "Could not fetch repository bean from DB for repository id " + repositoryId;
			log.error(message, e);
			throw new RepositoryException(Code.RepositoryRetrievalError);
		}
		
	}

	@Override
	public List<Map<String, String>> getData(long repositoryId, String entityName, int start, int length) {
		//TODO(DH): make a repository call
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		
		for(int i = start; i < start + length; i++) {
			Map<String, String> row = new HashMap<String, String>();
			row.put("field1", "value " + i);
			result.add(row);
		}
		
		return result;
	}


	@Override
	public List<Repository> getRepositories(long versionId) throws RepositoryException {
		List<Repository> result = new ArrayList<>();
		
		try {
			result = versionDao.getVersionRepositories(versionId);
		} catch (Exception e) {
			String message = "Could not fetch repository beans from DB for version id " + versionId;
			log.error(message, e);
			throw new RepositoryException(Code.RepositoryRetrievalError);
		}
		
		return result;
	}
	
	private Repository createRepository(RepositoryDbEntity repositoryDbEntity) throws JAXBException {
		Repository repository = new Repository();
		repository.setId(repositoryDbEntity.getId());
		repository.setName(repositoryDbEntity.getName());
		
		VersionDbEntity versionDbEntity = repositoryDbEntity.getSourceModelVersion();
		List<Entity> entitiesList = jaxbEntityListBuilder.deserialize(versionDbEntity.getModelXml());
		Version version = new Version();
		version.setId(versionDbEntity.getId());
		version.setName(versionDbEntity.getName());
		version.setTimestamp(versionDbEntity.getTimestamp());
		version.setEntities(entitiesList);
		version.setModelId(versionDbEntity.getModel().getId());
		repository.setSourceModelVersion(version);
		
		return repository;
	}
}