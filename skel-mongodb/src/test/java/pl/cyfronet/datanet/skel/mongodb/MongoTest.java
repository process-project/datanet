package pl.cyfronet.datanet.skel.mongodb;

import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.Mongo;

public class MongoTest {

	private static final Logger log = LoggerFactory.getLogger(MongoTest.class);

	public static void main(String[] args) {
		try {
			MongoOperations mongoOps = new MongoTemplate(new Mongo(), "mydb");
			
			Person p = new Person("Joe", 34);
			Address a = new Address();
			a.setNumber("3a");
			a.setStreet("Kasztanowa");
			p.setAddress(a);
			
			mongoOps.insert(p);
			
			System.out.println(mongoOps.collectionExists(Address.class));
			log.info(mongoOps.findOne(
					new Query(Criteria.where("name").is("Joe")), Person.class) + "");
			
			mongoOps.dropCollection("person");
		} catch (UnknownHostException ex) {
			log.error(ex.getMessage());
		}
	}
}
