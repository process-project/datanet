package pl.cyfronet.datanet.web.server.db;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pl.cyfronet.datanet.web.server.db.beans.ModelDbEntity;
import pl.cyfronet.datanet.web.server.db.beans.VersionDbEntity;

@Repository
public class HibernateVersionDao {
	@Autowired private SessionFactory sessionFactory;

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
	
	private ModelDbEntity getModel(long id) {
		return (ModelDbEntity) sessionFactory.getCurrentSession().load(ModelDbEntity.class, id);
	}
}