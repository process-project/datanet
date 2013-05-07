package pl.cyfronet.datanet.web.server.rpcservices;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hsqldb.lib.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.cyfronet.datanet.deployer.Deployer;
import pl.cyfronet.datanet.deployer.DeployerException;
import pl.cyfronet.datanet.deployer.marshaller.MarshallerException;
import pl.cyfronet.datanet.deployer.marshaller.ModelSchemaGenerator;
import pl.cyfronet.datanet.model.beans.Entity;
import pl.cyfronet.datanet.model.beans.Model;
import pl.cyfronet.datanet.model.util.JaxbEntityListBuilder;
import pl.cyfronet.datanet.model.util.ModelBuilder;
import pl.cyfronet.datanet.web.client.errors.ModelException;
import pl.cyfronet.datanet.web.client.errors.ModelException.Code;
import pl.cyfronet.datanet.web.client.services.ModelService;
import pl.cyfronet.datanet.web.server.db.HibernateModelDao;
import pl.cyfronet.datanet.web.server.db.beans.ModelDbEntity;

@Service("modelService")
public class RpcModelService  implements ModelService {
	private static final Logger log = LoggerFactory.getLogger(RpcModelService.class);
	
	@Autowired private HibernateModelDao modelDao;
	@Autowired private ModelBuilder modelBuilder;
	@Autowired private JaxbEntityListBuilder jaxbEntityListBuilder;
	@Autowired private Deployer deployer;
	@Autowired private ModelSchemaGenerator modelMarshaller;
	
	@Override
	public Model saveModel(Model model) throws ModelException {
		log.info("Processing model save request for model {}", model);

		try {
			ModelDbEntity modelDbEntity = new ModelDbEntity();
			modelDbEntity.setId(model.getId());
			modelDbEntity.setName(model.getName());
			modelDbEntity.setVersion(model.getVersion());
			modelDbEntity.setExperimentBody(jaxbEntityListBuilder.serialize(model.getEntities()));
			modelDao.saveModel(modelDbEntity);
			
			//return model id, updated by hibernate with new unique id
			model.setId(modelDbEntity.getId());
			
			return model;
		} catch (Exception e) {
			String message = "Could not save model " + model;
			log.error(message, e);
			throw new ModelException(Code.ModelSaveError);
		}
	}

	@Override
	public List<Model> getModels() throws ModelException {
		try {
			List<Model> result = new ArrayList<>();
			
			for(ModelDbEntity modelDbEntity : modelDao.getModels()) {
				Model model = new Model();
				List<Entity> entitiesList = jaxbEntityListBuilder.deserialize(modelDbEntity.getExperimentBody());
				model.setId(modelDbEntity.getId());
				model.setName(modelDbEntity.getName());
				model.setVersion(modelDbEntity.getVersion());
				model.setEntities(entitiesList);
				result.add(model);
			}
			
			return result;
		} catch (Exception e) {
			String message = "Could not retrieve models";
			log.error(message, e);
			throw new ModelException(Code.ModelRetrievalError);
		}
	}

	@Override
	public void deployModel(Model model) throws ModelException {
		try {
			Map<String, String> models = modelMarshaller.generateSchema(model);
			deployer.deployRepository(Deployer.RepositoryType.Mongo, model.getName(), models);
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
			List<String> repositoryNames = deployer.listRepostories();
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
		} catch (DeployerException e) {
			log.error("Deployer undeploy repository failure", e);
			throw new ModelException(Code.RepositoryUndeployError);
		}
	}
}