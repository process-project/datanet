package pl.cyfronet.datanet.web.server.services.repositoryclient;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RepositoryClientFactory {
	public RepositoryClient create(final String proxyCertificate)
			throws NoSuchAlgorithmException, KeyManagementException {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		httpClient.addRequestInterceptor(new HttpRequestInterceptor() {
			@Override
			public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
				request.addHeader(new BasicHeader("Grid-Proxy", Base64.encodeBase64String(proxyCertificate.getBytes())));
			}
		});
		HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
				httpClient);

		return new RepositoryClient(new RestTemplate(httpComponentsClientHttpRequestFactory));
	}
}