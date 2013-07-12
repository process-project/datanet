package pl.cyfronet.datanet.web.server.rpcservices;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.cyfronet.datanet.model.beans.Entity;
import pl.cyfronet.datanet.model.beans.Model;
import pl.cyfronet.datanet.model.util.JaxbEntityListBuilder;
import pl.cyfronet.datanet.model.util.ModelBuilder;
import pl.cyfronet.datanet.web.client.errors.ModelException;
import pl.cyfronet.datanet.web.client.errors.ModelException.Code;
import pl.cyfronet.datanet.web.client.services.ModelService;
import pl.cyfronet.datanet.web.server.db.HibernateModelDao;
import pl.cyfronet.datanet.web.server.db.HibernateUserDao;
import pl.cyfronet.datanet.web.server.db.beans.ModelDbEntity;
import pl.cyfronet.datanet.web.server.db.beans.UserDbEntity;
import pl.cyfronet.datanet.web.server.util.SpringSessionHelper;

@Service("modelService")
public class RpcModelService implements ModelService {
	private static final Logger log = LoggerFactory
			.getLogger(RpcModelService.class);

	@Autowired
	private HibernateModelDao modelDao;
	@Autowired
	private HibernateUserDao userDao;
	@Autowired
	private ModelBuilder modelBuilder;
	@Autowired
	private JaxbEntityListBuilder jaxbEntityListBuilder;

	@Override
	public Model saveModel(Model model) throws ModelException {
		log.info("Processing model save request for model {}", model);

		try {
			// TODO: Create optimized DAO method for this case
			List<ModelDbEntity> availableModels = modelDao.getModels();
			for (ModelDbEntity dbModel : availableModels) {
				if (model.getName().equals(dbModel.getName())
						&& model.getId() != dbModel.getId()) {
					throw new ModelException(Code.ModelNameNotUnique);
				}
			}

			UserDbEntity user = userDao.getUser(SpringSessionHelper.getUserLogin());
			ModelDbEntity modelDbEntity = new ModelDbEntity();
			modelDbEntity.setId(model.getId());
			modelDbEntity.setName(model.getName());
			modelDbEntity.setVersion(model.getVersion());
			modelDbEntity.setExperimentBody(jaxbEntityListBuilder
					.serialize(model.getEntities()));
			if (modelDbEntity.getOwners() == null) {
				modelDbEntity.setOwners(new ArrayList<UserDbEntity>());
			}
			modelDbEntity.getOwners().add(user);
			modelDao.saveModel(modelDbEntity);

			// return model id, updated by hibernate with new unique id
			model.setId(modelDbEntity.getId());

			return model;
		} catch (ModelException me) {
			throw me;
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

			for (ModelDbEntity modelDbEntity : modelDao.getUserModels(SpringSessionHelper.getUserLogin())) {
				result.add(getModel(modelDbEntity));
			}

			return result;
		} catch (Exception e) {
			String message = "Could not retrieve models";
			log.error(message, e);
			throw new ModelException(Code.ModelRetrievalError);
		}
	}
	
	@Override
	public Model getModel(long modelId) throws ModelException {
		ModelDbEntity modelDbEntity = modelDao.getModel(SpringSessionHelper.getUserLogin(), modelId);

		if (modelDbEntity == null) {
			throw new ModelException(ModelException.Code.ModelRetrievalError);
		}

		return getModel(modelDbEntity);
	}

	@Override
	public void deleteModel(long modelId) throws ModelException {
		modelDao.deleteModel(SpringSessionHelper.getUserLogin(), modelId);		
	}

	private Model getModel(ModelDbEntity modelDbEntity) throws ModelException {
		try {
			Model model = new Model();
			List<Entity> entitiesList = jaxbEntityListBuilder
					.deserialize(modelDbEntity.getExperimentBody());
			model.setId(modelDbEntity.getId());
			model.setName(modelDbEntity.getName());
			model.setVersion(modelDbEntity.getVersion());
			model.setEntities(entitiesList);

			return model;
		} catch (Exception e) {
			String message = "Could not retrieve models";
			log.error(message, e);
			throw new ModelException(Code.ModelRetrievalError);
		}
	}
}