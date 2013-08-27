package pl.cyfronet.datanet.web.server.db;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pl.cyfronet.datanet.web.server.db.beans.ModelDbEntity;
import pl.cyfronet.datanet.web.server.db.beans.RepositoryDbEntity;
import pl.cyfronet.datanet.web.server.db.beans.UserDbEntity;

@Repository
public class HibernateRepositoryDao {
	@Autowired private SessionFactory sessionFactory;

	@Transactional
	public void saveRepository(RepositoryDbEntity repository) {
		sessionFactory.getCurrentSession().saveOrUpdate(repository);
	}

	@Transactional(readOnly = true)
	public RepositoryDbEntity getRepository(long id) {
		return (RepositoryDbEntity) sessionFactory.getCurrentSession().get(RepositoryDbEntity.class, id);
	}

	@Transactional(readOnly = true)
	public RepositoryDbEntity getRepositoryByName(String name) {
		return (RepositoryDbEntity) sessionFactory.getCurrentSession().createCriteria(RepositoryDbEntity.class).add(Restrictions.eq("name", name)).uniqueResult();
	}

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<RepositoryDbEntity> getRepositories() {
		return sessionFactory.getCurrentSession().createCriteria(RepositoryDbEntity.class).list();
	}

	@Transactional(readOnly = true)
	public List<RepositoryDbEntity> getUserRepositories(String userLogin) {
		UserDbEntity user = (UserDbEntity) sessionFactory.getCurrentSession().createCriteria(UserDbEntity.class).add(Restrictions.eq("login", userLogin)).uniqueResult();
		
		//let's make sure lazy loading does not surprise us
		if(user.getRepositories() != null) {
			user.getRepositories().size();
		}
		
		return user.getRepositories();
	}

	@Transactional
	public void deleteRepository(RepositoryDbEntity repository) {
		sessionFactory.getCurrentSession().delete(repository);
	}

	@Transactional(readOnly = true)
	public ModelDbEntity getModelForRepository(long repositoryId) {
		return getRepository(repositoryId).getSourceModelVersion().getModel();
	}	
}