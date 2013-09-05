package pl.cyfronet.datanet.web.client.controller;

import com.google.gwt.i18n.client.Constants;

public interface AppProperties extends Constants {
	@Key("current.version") String version();
	@Key("max.number.of.repositories.per.user") int maxNumberOfRepositoriesPerUser(); 
}