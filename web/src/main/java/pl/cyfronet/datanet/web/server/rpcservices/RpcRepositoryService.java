package pl.cyfronet.datanet.web.server.rpcservices;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import pl.cyfronet.datanet.deployer.Deployer;
import pl.cyfronet.datanet.deployer.DeployerException;
import pl.cyfronet.datanet.deployer.marshaller.MarshallerException;
import pl.cyfronet.datanet.deployer.marshaller.ModelSchemaGenerator;
import pl.cyfronet.datanet.model.beans.AccessConfig;
import pl.cyfronet.datanet.model.beans.Entity;
import pl.cyfronet.datanet.model.beans.Field;
import pl.cyfronet.datanet.model.beans.Model;
import pl.cyfronet.datanet.model.beans.Repository;
import pl.cyfronet.datanet.model.beans.Type;
import pl.cyfronet.datanet.model.beans.Version;
import pl.cyfronet.datanet.model.beans.validator.RepositoryValidator;
import pl.cyfronet.datanet.model.util.JaxbEntityListBuilder;
import pl.cyfronet.datanet.web.client.controller.beans.EntityData;
import pl.cyfronet.datanet.web.client.errors.ModelException;
import pl.cyfronet.datanet.web.client.errors.RepositoryException;
import pl.cyfronet.datanet.web.client.errors.RepositoryException.Code;
import pl.cyfronet.datanet.web.client.services.RepositoryService;
import pl.cyfronet.datanet.web.server.db.HibernateModelDao;
import pl.cyfronet.datanet.web.server.db.HibernateRepositoryDao;
import pl.cyfronet.datanet.web.server.db.HibernateUserDao;
import pl.cyfronet.datanet.web.server.db.HibernateVersionDao;
import pl.cyfronet.datanet.web.server.db.beans.ModelDbEntity;
import pl.cyfronet.datanet.web.server.db.beans.RepositoryDbEntity;
import pl.cyfronet.datanet.web.server.db.beans.UserDbEntity;
import pl.cyfronet.datanet.web.server.db.beans.VersionDbEntity;
import pl.cyfronet.datanet.web.server.services.repositoryclient.RepositoryClient;
import pl.cyfronet.datanet.web.server.services.repositoryclient.RepositoryClientFactory;
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
	@Autowired private RepositoryClientFactory repositoryClientFactory;

	@Value("${max.number.of.repositories.per.user}") private int maxRepoCount;
	
	@Override
	public List<Repository> getRepositories() throws RepositoryException {
		try {
			List<Repository> repositories = new LinkedList<>();
			
			for(RepositoryDbEntity repositoryDbEntity : repositoryDao.getUserRepositories(SpringSecurityHelper.getUserLogin())) {
				Repository repository = new Repository();
				repository.setId(repositoryDbEntity.getId());
				repository.setName(repositoryDbEntity.getName());
				repository.setSourceModelVersion(repository.getSourceModelVersion());
				repository.setUrl(repositoryDbEntity.getUrl());
				repositories.add(repository);
				
				RepositoryClient repositoryClient = repositoryClientFactory.create(
						(String) SecurityContextHolder.getContext().getAuthentication().getCredentials());
				
				AccessConfig accessConfig = repositoryClient.getAccessConfig(
						repositoryDbEntity.getUrl(), repositoryDbEntity.getToken());
				repository.setAccessConfig(accessConfig);
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
			if (repositoryDao.isRepositoryOwner(repositoryId, SpringSecurityHelper.getUserLogin())) {
				RepositoryDbEntity repository = repositoryDao.getRepository(repositoryId);
				String repositoryName = repository.getName().substring(4);
				deployer.undeployRepository(repositoryName);
				repositoryDao.deleteRepository(repository);
			} else {
				throw new RepositoryException(Code.RepositoryAuthorizationError);
			}
		} catch (DeployerException e) {
			log.error("Deployer undeploy repository failure", e);
			throw new RepositoryException(Code.RepositoryUndeployError);
		}
	}

	@Override
	public Repository getRepository(long repositoryId) throws RepositoryException {
		try {
			if (repositoryDao.isRepositoryOwner(repositoryId, SpringSecurityHelper.getUserLogin())) {
				log.debug("Retrieving repository DB bean for id {}", repositoryId);
				
				RepositoryClient repositoryClient = repositoryClientFactory.create(
						(String) SecurityContextHolder.getContext().getAuthentication().getCredentials());
				
				RepositoryDbEntity repositoryDbEntity = repositoryDao.getRepository(repositoryId);
				AccessConfig accessConfig = repositoryClient.getAccessConfig(
						repositoryDbEntity.getUrl(), repositoryDbEntity.getToken());
				Repository repository = createRepository(repositoryDbEntity, accessConfig);
				
				return repository;
			} else {
				throw new RepositoryException(Code.RepositoryAuthorizationError);
			}
		} catch (Exception e) {
			String message = "Could not fetch repository bean from DB for repository id " + repositoryId;
			log.error(message, e);
			throw new RepositoryException(Code.RepositoryRetrievalError);
		}
		
	}

	@Override
	public EntityData getData(long repositoryId, String entityName, int start, int length, Map<String, String> query) throws RepositoryException {
		log.debug("Fetching repository data for repository with id {} and entity name {} starting with {} and length {} and query {}",
				new Object[] {repositoryId, entityName, start, length, query});
		
		try {
			RepositoryDbEntity repositoryDbEntity = repositoryDao.getRepository(repositoryId);
			log.debug("Retrieving repository data for url {} entity {} start index {}, length {} and query {}",
					new Object[] {repositoryDbEntity.getUrl(), entityName, start, length, query});
			
			ModelDbEntity modelDbEntity = repositoryDao.getModelForRepository(repositoryDbEntity.getId());
			Model model = getModel(modelDbEntity);
			Entity entity = null;
			List<String> fileFields = new ArrayList<>();
			
			for (Entity e : model.getEntities()) {
				if (e.getName().equals(entityName)) {
					entity = e;
					break;
				}
			}
			
			if (entity != null) {
				for (Field field : entity.getFields()) {
					if (field.getType() == Type.File) {
						fileFields.add(field.getName());
					}
				}
			}
			
			RepositoryClient client = repositoryClientFactory.create(
					(String) SecurityContextHolder.getContext().getAuthentication().getCredentials());
			
			return client.retrieveRepositoryData(repositoryDbEntity.getUrl(), entityName, fileFields, start, length, query);
		} catch (Exception e) {
			String message = "Repository data retrieval error occurred for repository with id " + repositoryId;
			log.error(message, e);
			throw new RepositoryException(Code.RepositoryDataRetrievalError, message, e);
		}
	}

	@Override
	public List<Repository> getRepositories(long versionId) throws RepositoryException {
		List<Repository> result = new ArrayList<>();
		
		try {
			result = versionDao.getVersionRepositories(versionId);
			
			for (Repository repository : result) {
				if (repositoryDao.isRepositoryOwner(repository.getId(), SpringSecurityHelper.getUserLogin())) {
					RepositoryClient repositoryClient = repositoryClientFactory.create(
							(String) SecurityContextHolder.getContext().getAuthentication().getCredentials());
					RepositoryDbEntity repositoryDbEntity = repositoryDao.getRepository(repository.getId());
					AccessConfig accessConfig = repositoryClient.getAccessConfig(
							repository.getUrl(), repositoryDbEntity.getToken());
					repository.setAccessConfig(accessConfig);
				}
			}
		} catch (Exception e) {
			String message = "Could not fetch repository beans from DB for version id " + versionId;
			log.error(message, e);
			throw new RepositoryException(Code.RepositoryRetrievalError);
		}
		
		return result;
	}
	
	@Override
	public Repository deployModelVersion(long versionId, String repositoryName) throws RepositoryException {
		validateRepository(repositoryName);
		
		try {
			if (getRepositoryCount() >= maxRepoCount) {
				throw new RepositoryException(Code.RepositoryAuthorizationError, "You can create maximum 5 repositories");
			}
			
			Version version = versionDao.getVersion(versionId);
			Map<String, String> models = modelSchemaGenerator.generateSchema(version);
			String token = UUID.randomUUID().toString();
			String repositoryUrl = deployer.deployRepository(Deployer.RepositoryType.Mongo, repositoryName, models, token);
			repositoryUrl = changeToHttps(repositoryUrl);
			UserDbEntity user = userDao.getUser(SpringSecurityHelper.getUserLogin());
			RepositoryDbEntity repository = new RepositoryDbEntity();

			if (repository.getOwners() == null) {
				repository.setOwners(new LinkedList<UserDbEntity>());
			}
			
			VersionDbEntity versionDbEntity = versionDao.getVersionDbEntity(versionId);
			repository.getOwners().add(user);
			repository.setSourceModelVersion(versionDbEntity);
			repository.setUrl(repositoryUrl);
			repository.setName(repositoryName);
			repository.setToken(token);
			repositoryDao.saveRepository(repository);
			versionDao.addVersionRepository(versionDbEntity, repository);
			
			RepositoryClient repositoryClient = repositoryClientFactory.create(
					(String) SecurityContextHolder.getContext().getAuthentication().getCredentials());
			repositoryClient.waitUntilRepositoryAvailable(repositoryUrl);
			
			AccessConfig accessConfig = repositoryClient.getAccessConfig(repositoryUrl, token);
			
			return createRepository(repository, accessConfig);
		} catch (MarshallerException e) {
			String message = "Could not marshall model";
			log.error(message, e);
			throw new RepositoryException(Code.ModelDeployError, e.getMessage());
		} catch (DeployerException e) {
			log.error("Deployer authorization failure", e);
			throw new RepositoryException(Code.ModelDeployError, e.getMessage());
		} catch (Exception e) {
			log.error("Repository deployment failure", e);
			throw new RepositoryException(Code.ModelDeployError, e.getMessage());
		}
	}

	/**
	 * @deprecated instead use the following action: pl.cyfronet.datanet.web.server.controllers.FormController.handleForm(EntityUpload)
	 */
	@Override
	@Deprecated
	public void saveData(long repositoryId, String entityName, Map<String, String> data) throws RepositoryException {
		try {
			RepositoryDbEntity repositoryDbEntity = repositoryDao.getRepository(repositoryId);
			
			RepositoryClient repositoryClient = repositoryClientFactory.create(
					(String) SecurityContextHolder.getContext().getAuthentication().getCredentials());
			repositoryClient.updateEntityRow(repositoryDbEntity.getUrl(), entityName, null, data, null);
		} catch (Exception e) {
			log.error("Repository entity row data could not be saved", e);
			throw new RepositoryException(Code.RepositoryDataSavingError, e.getMessage());
		}
	}
	
	@Override
	public void removeRepository(long repositoryId) throws RepositoryException {
		try {
			if (repositoryDao.isRepositoryOwner(repositoryId, SpringSecurityHelper.getUserLogin())) {
				RepositoryDbEntity repositoryDbEntity = repositoryDao.getRepository(repositoryId);
				log.info("Removing repository with id {} and name {}", repositoryId, repositoryDbEntity.getName());
				deployer.undeployRepository(repositoryDbEntity.getName());
				repositoryDao.deleteRepository(repositoryDbEntity);
			} else {
				throw new RepositoryException(Code.RepositoryAuthorizationError);
			}
		} catch (Exception e) {
			log.error("Repository with id {} could not be removed", repositoryId);
			log.error("Repository removal error", e);
			throw new RepositoryException(Code.RepositoryRemovalError, e.getMessage());
		}
	}
	
	@Override
	public int getRepositoryCount() throws RepositoryException {
		int result = -1;
		
		try {
			List<RepositoryDbEntity> userRepositories = repositoryDao.getUserRepositories(SpringSecurityHelper.getUserLogin());
			result = userRepositories.size();
		} catch (Exception e) {
			log.error("Could not retrieve user repository count for user {}", SpringSecurityHelper.getUserLogin());
			log.error("Repository count error", e);
			throw new RepositoryException(Code.RepositoryDataRetrievalError);
		}
		
		return result;
	}
	
	@Override
	public void updateAccessConfig(long repositoryId, AccessConfig accessConfig) throws RepositoryException {
		try {
			if (repositoryDao.isRepositoryOwner(repositoryId, SpringSecurityHelper.getUserLogin())) {
				RepositoryDbEntity repositoryDbEntity = repositoryDao.getRepository(repositoryId);
				log.info("Updating repository access configuration for repository with id {}", repositoryId);
				RepositoryClient repositoryClient = repositoryClientFactory.create(
						(String) SecurityContextHolder.getContext().getAuthentication().getCredentials());
				repositoryClient.updateAccessConfiguration(repositoryDbEntity.getUrl(), repositoryDbEntity.getToken(), accessConfig);
			} else {
				throw new RepositoryException(Code.RepositoryAuthorizationError);
			}
		} catch (Exception e) {
			log.error("Access configuration for repository with id {} could not be updated", repositoryId);
			log.error("Repository access configuration update error", e);
			throw new RepositoryException(Code.RepositoryAccessConfigurationUpdateError, e.getMessage());
		}
	}
	
	@Override
	public void removeEntityRow(long repositoryId, String rowId, String entityName) throws RepositoryException {
		try {
			if (repositoryDao.isRepositoryOwner(repositoryId, SpringSecurityHelper.getUserLogin())) {
				RepositoryDbEntity repositoryDbEntity = repositoryDao.getRepository(repositoryId);
				log.info("Removing repository entity row for repository id {} and row id {}", repositoryId, rowId);
				RepositoryClient repositoryClient = repositoryClientFactory.create(
						(String) SecurityContextHolder.getContext().getAuthentication().getCredentials());
				repositoryClient.removeEntityRow(repositoryDbEntity.getUrl(), rowId, entityName);
			} else {
				throw new RepositoryException(Code.RepositoryAuthorizationError);
			}
		} catch (Exception e) {
			log.error("Repository entity row cloud not be deleted", repositoryId);
			log.error("Repository entity row removal error", e);
			throw new RepositoryException(Code.RepositoryEntityRowRemovalError, e.getMessage());
		}
	}
	
	private Repository createRepository(RepositoryDbEntity repositoryDbEntity, AccessConfig accessConfig) throws JAXBException {		
		Repository repository = new Repository();
		repository.setId(repositoryDbEntity.getId());
		repository.setName(repositoryDbEntity.getName());
		repository.setUrl(repositoryDbEntity.getUrl());
		repository.setAccessConfig(accessConfig);
		
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
	
	private Model getModel(ModelDbEntity modelDbEntity) throws ModelException {
		try {
			Model model = new Model();
			List<Entity> entitiesList = jaxbEntityListBuilder
					.deserialize(modelDbEntity.getModelXml());
			model.setId(modelDbEntity.getId());
			model.setName(modelDbEntity.getName());
			model.setEntities(entitiesList);
			model.setTimestamp(modelDbEntity.getTimestamp());
			return model;
		} catch (Exception e) {
			String message = "Could not retrieve models";
			log.error(message, e);
			throw new ModelException(pl.cyfronet.datanet.web.client.errors.ModelException.Code.ModelRetrievalError);
		}
	}
	
	private String changeToHttps(String repositoryUrl) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(repositoryUrl);
		builder.scheme("https");
		
		return builder.build().toUriString();
	}
	
	private void validateRepository(String repositoryName) throws RepositoryException {
		RepositoryValidator validator = new RepositoryValidator();		
		
		if(!validator.isValidName(repositoryName)) {
			throw new RepositoryException(Code.RespositoryValidationError, "Repository validation error");
		}
	}
}