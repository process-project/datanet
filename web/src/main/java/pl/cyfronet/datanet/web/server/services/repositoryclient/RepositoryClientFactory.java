package pl.cyfronet.datanet.web.server.services.repositoryclient;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RepositoryClientFactory {
	public RepositoryClient create(String login, String password) {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		BasicCredentialsProvider credentialsProvider =  new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(login, password));
		httpClient.setCredentialsProvider(credentialsProvider);
		HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory =
				new HttpComponentsClientHttpRequestFactory(httpClient);
		
		return new RepositoryClient(new RestTemplate(httpComponentsClientHttpRequestFactory));
	}
}