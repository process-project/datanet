package pl.cyfronet.datanet.test.repositoryclient;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.client.RestClientException;

import pl.cyfronet.datanet.web.server.config.SpringConfiguration;
import pl.cyfronet.datanet.web.server.services.repositoryclient.RepositoryClient;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class,
		classes = SpringConfiguration.class)
public class RepositoryClientTest {
	@Autowired private RepositoryClient repositoryClient;
	
	//TODO(DH): create a test repository beforehand
//	@Test
	public void retrieveData() throws RestClientException, URISyntaxException {
		repositoryClient.retrieveRepositoryData("http://testmodel.datanet.cyfronet.pl", "testentity", 1, -1);
	}
	
	@Test
	public void insertData() throws RestClientException, URISyntaxException {
		Map<String, String> data = new HashMap<>();
		data.put("testfield", "It is " + System.currentTimeMillis() + " millis from the epoch");
		repositoryClient.updateEntityRow("http://testmodel.datanet.cyfronet.pl", "testentity", null, data);
	}
}