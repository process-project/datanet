package pl.cyfronet.datanet.web.server.db;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pl.cyfronet.datanet.web.server.db.beans.Model;

@Repository
public class HibernateModelDao {
	@Autowired private SessionFactory sessionFactory;
	
	@Transactional
	public void saveModel(Model model) {
		sessionFactory.getCurrentSession().save(model);
	}

	@Transactional
	public Model getModel(long id) {
		return (Model) sessionFactory.getCurrentSession().get(Model.class, id);
	}
}