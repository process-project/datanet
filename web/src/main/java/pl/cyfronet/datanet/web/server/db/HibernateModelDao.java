package pl.cyfronet.datanet.web.server.db;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pl.cyfronet.datanet.web.server.db.beans.ModelDbEntity;
import pl.cyfronet.datanet.web.server.db.beans.UserDbEntity;

@Repository
public class HibernateModelDao {
	@Autowired private SessionFactory sessionFactory;
	
	@Transactional
	public void saveModel(ModelDbEntity model) {
		sessionFactory.getCurrentSession().saveOrUpdate(model);
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
	
	@Transactional(readOnly = true)
	public List<ModelDbEntity> getUserModels(String userLogin) {
		UserDbEntity user = (UserDbEntity) sessionFactory.getCurrentSession().createCriteria(UserDbEntity.class).add(Restrictions.eq("login", userLogin)).uniqueResult();
		
		//let's make sure lazy loading does not surprise us
		if(user.getModels() != null) {
			user.getModels().size();
		}
		
		if(user.getModels() == null) {
			return new ArrayList<>();
		}
		
		return user.getModels();
	}
	
	@Transactional(readOnly = true)
	public ModelDbEntity getModel(String userLogin, long id) {
		Criteria c = sessionFactory.getCurrentSession().createCriteria(ModelDbEntity.class, "model");
		c.createAlias("model.owners", "owner");
		c.add(Restrictions.eq("model.id", id));
		c.add(Restrictions.eq("owner.login", userLogin));
		
		return (ModelDbEntity)c.uniqueResult();
	}

	@Transactional
	public void deleteModel(String login, long modelId) {
		//XXX check if model have versions. If yes than throw exception
		ModelDbEntity model = getModel(login, modelId);
		sessionFactory.getCurrentSession().delete(model);
	}

}