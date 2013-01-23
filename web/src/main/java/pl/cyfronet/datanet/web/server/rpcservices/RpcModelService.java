package pl.cyfronet.datanet.web.server.rpcservices;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.cyfronet.datanet.model.beans.Model;
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
	
	@Override
	public void saveModel(Model model) throws ModelException {
		log.info("Processing model save request for model {}", model);

		try {
			ModelDbEntity modelDbEntity = new ModelDbEntity();
			modelDbEntity.setName(model.getName());
			modelDbEntity.setVersion(model.getVersion());
			modelDbEntity.setExperimentBody(modelBuilder.serialize(model));
			modelDao.saveModel(modelDbEntity);
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
				result.add(modelBuilder.deserialize(modelDbEntity.getExperimentBody()));
			}
			
			return result;
		} catch (Exception e) {
			String message = "Could not retrieve models";
			log.error(message, e);
			throw new ModelException(Code.ModelRetrievalError);
		}
	}
}