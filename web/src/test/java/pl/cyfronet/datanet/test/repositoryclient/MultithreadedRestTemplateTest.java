package pl.cyfronet.datanet.test.repositoryclient;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.client.RestTemplate;

import pl.cyfronet.datanet.web.server.config.SpringConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class,
		classes = SpringConfiguration.class)
public class MultithreadedRestTemplateTest {
	@Autowired private RestTemplate restTemplate; 
	
	@Test
	public void shouldMakeConcurrentRequests() throws InterruptedException {
		final int numberOfThreads = 10;
		List<Thread> threads = new ArrayList<>();
		final List<HttpStatus> results = new ArrayList<>();
		
		for (int i = 0; i < numberOfThreads; i++) {
			Thread thread = new Thread() {
				@Override
				public void run() {
					ResponseEntity<String> response = restTemplate.getForEntity("http://cyfronet.pl", String.class);
					
					synchronized (results) {
						results.add(response.getStatusCode());
					}
				}
			};
			threads.add(thread);
			thread.start();
		}
		
		for (Thread thread : threads) {
			thread.join();
		}
		
		Assert.assertEquals(numberOfThreads, results.size());
		
		for (HttpStatus httpStatus : results) {
			Assert.assertEquals(HttpStatus.OK, httpStatus);
		}
	}
}