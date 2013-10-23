package pl.cyfronet.datanet.test.repositoryclient;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

import pl.cyfronet.datanet.model.beans.AccessConfig;
import pl.cyfronet.datanet.model.beans.AccessConfig.Access;
import pl.cyfronet.datanet.web.server.config.SpringConfiguration;
import pl.cyfronet.datanet.web.server.services.repositoryclient.RepositoryClient;
import pl.cyfronet.datanet.web.server.services.repositoryclient.RepositoryClientFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class,
		classes = SpringConfiguration.class)
public class RepositoryClientTest {
	@Autowired private RepositoryClient repositoryClient;
	@Autowired private RepositoryClientFactory repositoryClientFactory;
	
	@Mock private MultipartFile file;
	
	@Before
	public void prepare() {
		MockitoAnnotations.initMocks(this);
	}
	
	//TODO(DH): create a test repository beforehand
//	@Test
	public void retrieveData() throws RestClientException, URISyntaxException {
		repositoryClient.retrieveRepositoryData("http://testmodel.datanet.cyfronet.pl", "testentity", null, 1, -1, null);
	}
	
//	@Test
	public void insertData() throws RestClientException, URISyntaxException, IOException {
		Map<String, String> data = new HashMap<>();
		data.put("testfield", "It is " + System.currentTimeMillis() + " millis from the epoch");
		repositoryClient.updateEntityRow("http://testmodel.datanet.cyfronet.pl", "testentity", null, data, null);
	}
	
//	@Test
	public void insertFileData() throws RestClientException, URISyntaxException, IOException, KeyManagementException, NoSuchAlgorithmException {
		String time = String.valueOf(System.currentTimeMillis());
		Map<String, String> data = new HashMap<>();
		data.put("meta", "testing file insertion at " + time);
		
		when(file.getOriginalFilename()).thenReturn("file.txt");
		when(file.getBytes()).thenReturn("file contents".getBytes());
		
		Map<String, MultipartFile> files = new HashMap<>();
		files.put("file", file);
		
		RepositoryClient repositoryClient = repositoryClientFactory.create("secret", "secret");
		repositoryClient.updateEntityRow("http://filemodel.datanet.cyfronet.pl", "fileentity", null, data, files);
	}
	
	@Test
	public void dummyTest() {
	}
}