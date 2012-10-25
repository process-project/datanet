package pl.cyfronet.datanet.test.mongo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import pl.cyfronet.datanet.test.mongo.model.Entity;

@Controller
public class CrudController {
	@Autowired private MongoOperations mongo;
	
	@RequestMapping("/create")
	public String create(Model model) {
		Entity e = new Entity();
		e.setName(String.valueOf(System.currentTimeMillis()));
		mongo.save(e, "entities");
		model.addAttribute("id", e.getName());
		
		return "ok";
	}
	
	@RequestMapping("/list")
	public String list(Model model) {
		List<Entity> entities = mongo.find(
				null, Entity.class);
		model.addAttribute("entityCount", entities.size());
		
		return "list";
	}
}