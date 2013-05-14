package pl.cyfronet.datanet.web.server.db;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pl.cyfronet.datanet.web.server.db.beans.UserDbEntity;

@Repository
public class HibernateUserDao {
	@Autowired private SessionFactory sessionFactory;
	
	@Transactional
	public void saveUser(UserDbEntity user) {
		sessionFactory.getCurrentSession().saveOrUpdate(user);
	}

	@Transactional
	public UserDbEntity getUser(long id) {
		return (UserDbEntity) sessionFactory.getCurrentSession().get(UserDbEntity.class, id);
	}
	
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<UserDbEntity> getUsers() {
		return sessionFactory.getCurrentSession().createCriteria(UserDbEntity.class).list();
	}
	
	@Transactional(readOnly = true)
	public UserDbEntity getUser(String login) {
		return (UserDbEntity) sessionFactory.getCurrentSession().createCriteria(UserDbEntity.class).add(Restrictions.eq("login", login)).uniqueResult();
	}
}