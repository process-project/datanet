package pl.cyfronet.datanet.web.server.db;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pl.cyfronet.datanet.web.server.db.beans.ModelDbEntity;

@Repository
public class HibernateModelDao {
	@Autowired private SessionFactory sessionFactory;
	
	@Transactional
	public void saveModel(ModelDbEntity model) {
		sessionFactory.getCurrentSession().save(model);
	}

	@Transactional
	public ModelDbEntity getModel(long id) {
		return (ModelDbEntity) sessionFactory.getCurrentSession().get(ModelDbEntity.class, id);
	}
	
	@Transactional
	@SuppressWarnings("unchecked")
	public List<ModelDbEntity> getModels() {
		return sessionFactory.getCurrentSession().createCriteria(ModelDbEntity.class).list();
	}
}