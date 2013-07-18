package pl.cyfronet.datanet.web.server.db;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import pl.cyfronet.datanet.model.beans.Entity;
import pl.cyfronet.datanet.model.beans.Repository;
import pl.cyfronet.datanet.model.beans.Version;
import pl.cyfronet.datanet.model.util.JaxbEntityListBuilder;
import pl.cyfronet.datanet.web.server.db.beans.ModelDbEntity;
import pl.cyfronet.datanet.web.server.db.beans.RepositoryDbEntity;
import pl.cyfronet.datanet.web.server.db.beans.VersionDbEntity;

@org.springframework.stereotype.Repository
public class HibernateVersionDao {
	@Autowired private SessionFactory sessionFactory;
	@Autowired private JaxbEntityListBuilder jaxbEntityListBuilder;

	@Transactional
	public void saveVersion(long modelId, VersionDbEntity versionDbEntity) {
		ModelDbEntity modelDbEntity = getModel(modelId);
		versionDbEntity.setModel(modelDbEntity);
		modelDbEntity.getVersions().add(versionDbEntity);
		sessionFactory.getCurrentSession().saveOrUpdate(versionDbEntity);
		sessionFactory.getCurrentSession().saveOrUpdate(modelDbEntity);
	}
	
	@Transactional
	public VersionDbEntity getVersion(long versionId) {
		return (VersionDbEntity) sessionFactory.getCurrentSession().get(VersionDbEntity.class, versionId);
	}

	@Transactional
	public List<VersionDbEntity> getVersions(long modelId) {
		ModelDbEntity modelDbEntity = getModel(modelId);
		List<VersionDbEntity> versions = modelDbEntity.getVersions();
		Hibernate.initialize(versions);
		
		return versions;
	}
	
	@Transactional
	public List<Repository> getVersionRepositories(long versionId) throws JAXBException {
		List<Repository> result = new ArrayList<>();
		VersionDbEntity versionDbEntity = getVersion(versionId);
		
		for (RepositoryDbEntity repositoryDbEntity : versionDbEntity.getRepositories()) {
			Repository repository = new Repository();
			repository.setId(repositoryDbEntity.getId());
			repository.setName(repositoryDbEntity.getName());

			List<Entity> entitiesList = jaxbEntityListBuilder.deserialize(versionDbEntity.getModelXml());
			Version version = new Version();
			version.setId(versionDbEntity.getId());
			version.setName(versionDbEntity.getName());
			version.setTimestamp(versionDbEntity.getTimestamp());
			version.setEntities(entitiesList);
			version.setModelId(versionDbEntity.getModel().getId());
			repository.setSourceModelVersion(version);
			result.add(repository);
		}
		
		return result;
	}
	
	private ModelDbEntity getModel(long id) {
		return (ModelDbEntity) sessionFactory.getCurrentSession().load(ModelDbEntity.class, id);
	}
}
