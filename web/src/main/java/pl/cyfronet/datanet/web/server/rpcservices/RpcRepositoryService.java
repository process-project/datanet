package pl.cyfronet.datanet.web.server.rpcservices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import pl.cyfronet.datanet.model.beans.Field;
import pl.cyfronet.datanet.model.beans.Field.Type;
import pl.cyfronet.datanet.model.beans.Model;
import pl.cyfronet.datanet.model.beans.Repository;
import pl.cyfronet.datanet.model.beans.Version;
import pl.cyfronet.datanet.web.client.errors.ModelException;
import pl.cyfronet.datanet.web.client.errors.ModelException.Code;
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

	@Override
	public void deployModelVersion(Version modelVersion, String repositoryName) throws ModelException {
		try {
			Map<String, String> models = modelSchemaGenerator.generateSchema(modelVersion);
			deployer.deployRepository(Deployer.RepositoryType.Mongo, repositoryName, models);
			
			UserDbEntity user = userDao.getUser(SpringSecurityHelper.getUserLogin());
			VersionDbEntity versionDbEntity = versionDao.getVersion(modelVersion.getId());
			
			RepositoryDbEntity repository = new RepositoryDbEntity();
			repository.setName(repositoryName);

			if(repository.getOwners() == null) {
				repository.setOwners(new LinkedList<UserDbEntity>());
			}
			
			repository.getOwners().add(user);
			repository.setSourceModelVersion(versionDbEntity);
			
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
	public List<Repository> getRepositories() throws ModelException {
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
			throw new ModelException(Code.RepositoryRetrievalError);
		}
	}

	@Override
	public void undeployRepository(long repositoryId) throws ModelException {
		try {
			RepositoryDbEntity repository = repositoryDao.getRepository(repositoryId);
			String repositoryName = repository.getName().substring(4);
			deployer.undeployRepository(repositoryName);
			repositoryDao.deleteRepository(repository);
		} catch (DeployerException e) {
			log.error("Deployer undeploy repository failure", e);
			throw new ModelException(Code.RepositoryUndeployError);
		}
	}

	@Override
	public Repository getRepository(long repositoryId) {
		Repository repository = new Repository();
		repository.setName("helloworld");
		repository.setId(1);
		
		Version version = new Version();
		version.setName("BasicModel");
		version.setEntities(new ArrayList<Entity>());
		
		Field field = new Field();
		field.setName("field1");
		field.setType(Type.String);
		field.setRequired(true);
		
		Entity e1 = new Entity();
		e1.setName("Entity1");
		e1.setFields(new ArrayList<Field>());
		e1.getFields().add(field);
		version.getEntities().add(e1);
		
		field = new Field();
		field.setName("field2");
		field.setType(Type.String);
		field.setRequired(true);
		
		Entity e2 = new Entity();
		e2.setName("Entity2");
		e2.setFields(new ArrayList<Field>());
		e2.getFields().add(field);
		version.getEntities().add(e2);
		
		field = new Field();
		field.setName("field3");
		field.setType(Type.String);
		field.setRequired(true);
		
		Entity e3 = new Entity();
		e3.setName("Entity3");
		e3.setFields(new ArrayList<Field>());
		e3.getFields().add(field);
		version.getEntities().add(e3);
		
		repository.setSourceModelVersion(version);
		
		return repository;
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
}
