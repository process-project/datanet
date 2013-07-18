package pl.cyfronet.datanet.web.server.rpcservices;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import pl.cyfronet.datanet.model.beans.Entity;
import pl.cyfronet.datanet.model.beans.Model;
import pl.cyfronet.datanet.model.beans.Version;
import pl.cyfronet.datanet.model.util.JaxbEntityListBuilder;
import pl.cyfronet.datanet.model.util.ModelBuilder;
import pl.cyfronet.datanet.web.client.errors.ModelException;
import pl.cyfronet.datanet.web.client.errors.ModelException.Code;
import pl.cyfronet.datanet.web.client.services.ModelService;
import pl.cyfronet.datanet.web.server.db.HibernateModelDao;
import pl.cyfronet.datanet.web.server.db.HibernateUserDao;
import pl.cyfronet.datanet.web.server.db.HibernateVersionDao;
import pl.cyfronet.datanet.web.server.db.beans.ModelDbEntity;
import pl.cyfronet.datanet.web.server.db.beans.UserDbEntity;
import pl.cyfronet.datanet.web.server.db.beans.VersionDbEntity;
import pl.cyfronet.datanet.web.server.util.SpringSecurityHelper;

@Service("modelService")
@Secured("ROLE_USER")
public class RpcModelService implements ModelService {
	private static final Logger log = LoggerFactory.getLogger(RpcModelService.class);

	@Autowired private HibernateModelDao modelDao;
	@Autowired private HibernateVersionDao versionDao;
	@Autowired private HibernateUserDao userDao;
	@Autowired private ModelBuilder modelBuilder;
	@Autowired private JaxbEntityListBuilder jaxbEntityListBuilder;

	
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

			UserDbEntity user = userDao.getUser(SpringSecurityHelper.getUserLogin());
			ModelDbEntity modelDbEntity = new ModelDbEntity();
			modelDbEntity.setId(model.getId());
			modelDbEntity.setName(model.getName());
			modelDbEntity.setTimestamp(model.getTimestamp());
			modelDbEntity.setModelXml(jaxbEntityListBuilder
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

			for (ModelDbEntity modelDbEntity : modelDao.getUserModels(SpringSecurityHelper.getUserLogin())) {
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
		ModelDbEntity modelDbEntity = modelDao.getModel(SpringSecurityHelper.getUserLogin(), modelId);

		if (modelDbEntity == null) {
			throw new ModelException(ModelException.Code.ModelRetrievalError);
		}

		return getModel(modelDbEntity);
	}

	@Override
	public void deleteModel(long modelId) throws ModelException {
		modelDao.deleteModel(SpringSecurityHelper.getUserLogin(), modelId);		
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
			throw new ModelException(Code.ModelRetrievalError);
		}
	}
	
	private Version getVersion(VersionDbEntity versionDbEnt) throws JAXBException  {
		List<Entity> entitiesList = jaxbEntityListBuilder
				.deserialize(versionDbEnt.getModelXml());
		Version version = new Version();
		version.setId(versionDbEnt.getId());
		version.setName(versionDbEnt.getName());
		version.setTimestamp(versionDbEnt.getTimestamp());
		version.setEntities(entitiesList);
		version.setModelId(versionDbEnt.getModel().getId());
		
		return version;
	}
	
	@Override
	public List<Version> getVersions(long modelId) throws ModelException {
		try {	
			List<VersionDbEntity> versionDbEnts = versionDao.getVersions(modelId);
			List<Version> versions = new ArrayList<>();
			if (versionDbEnts != null)
				for (VersionDbEntity versionDbEnt : versionDbEnts)
					versions.add(getVersion(versionDbEnt));
			return versions;
		} catch (Exception e) {
			String message = "Could not retrieve versions for model " + modelId;
			log.error(message, e);
			throw new ModelException(Code.VersionRetrievalError);
		}
	}
	
	@Override
	public Version getVersion(long versionId) throws ModelException {
		VersionDbEntity versionDbEntity = versionDao.getVersion(versionId);
		Version version;
		
		try {
			version = getVersion(versionDbEntity);
			
			return version;
		} catch (Exception e) {
			String message = "Could not retrieve version" + versionId;
			log.error(message, e);
			throw new ModelException(Code.VersionRetrievalError);
		}
	}

	@Override
	public Version addVersion(long modelId, Version version)
			throws ModelException {
		try {	
			VersionDbEntity versionDbEntity = new VersionDbEntity();		
			versionDbEntity.setName(version.getName());
			versionDbEntity.setModelXml(jaxbEntityListBuilder.serialize(version.getEntities()));
			versionDbEntity.setTimestamp(version.getTimestamp());
			versionDao.saveVersion(modelId, versionDbEntity);
			version.setId(versionDbEntity.getId());
			
			return version;
		} catch (Exception e) {
			String message = "Could not add version for model " + modelId;
			log.error(message, e);
			throw new ModelException(Code.ModelSaveError);
		}
	}
}