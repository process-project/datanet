package pl.cyfronet.datanet.web.server.services.portallogin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import pl.cyfronet.datanet.web.client.errors.LoginException;
import pl.cyfronet.datanet.web.client.errors.LoginException.Code;
import pl.cyfronet.datanet.web.server.services.portallogin.Response.Status;

@Service
public class PortalLoginHandler {
	private static final Logger log = LoggerFactory.getLogger(PortalLoginHandler.class);
	
	@Value("${portal.login.base.url}") private String loginBaseUrl;
	@Value("${portal.login.challenge.template}") private String challengePath;
	@Value("${portal.login.login.template}") private String loginPath;
	@Value("${portal.login.shared.key}") private String sharedKey;
	
	@Autowired private RestTemplate restClient;
	
	public void login(String login, String password) throws LoginException {
		String challengeUrl = prepareChallangeUrl();
		log.debug("Login challenge attempt with URL {}", challengeUrl);
		
		Response challengeResponse = restClient.getForObject(challengeUrl, Response.class, sharedKey, login);
		log.trace("Challenge response: {}", challengeResponse);
		
		if(challengeResponse.getStatus() == Status.OK) {
			String sshaHash = null;
			
			try {
				sshaHash = sshaHash(password, challengeResponse.getChallenge());
			} catch (NoSuchAlgorithmException | DecoderException | IOException e) {
				log.warn("Could not properly hash the given password", e);
				throw new LoginException(Code.Unknown);
			}
			
			String loginUrl = prepareLoginUrl();
			MultiValueMap<String, String> postParams = new LinkedMultiValueMap<>();
			postParams.add("sshaPassword", sshaHash);
			log.debug("Login attempt with URL {}", loginUrl);
			
			Response loginResponse = restClient.postForObject(loginUrl, postParams, Response.class, sharedKey, login);
			log.trace("Login response: {}", loginResponse);
			
			if(loginResponse.getStatus() == Status.OK) {
				log.debug("Login for user {} successful with token {}", login, loginResponse.getUserData().getToken());
			} else {
				//given password did not match
				throw new LoginException(Code.UserPasswordUnknown);
			}
		} else {
			//given user was not found
			throw new LoginException(Code.UserPasswordUnknown);
		}
	}

	private String sshaHash(String password, String challenge) throws NoSuchAlgorithmException,
			DecoderException, IOException {
		MessageDigest digest = MessageDigest.getInstance("SHA");
		digest.update(password.getBytes());
		
		byte[] salt = Hex.decodeHex(challenge.toCharArray());
		digest.update(salt);
		
		byte[] hash = digest.digest();

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		buffer.write(hash);
		buffer.write(salt);

        return Base64.encodeBase64String(buffer.toByteArray());
	}
	
	private String prepareLoginUrl() {
		StringBuilder builder = new StringBuilder();
		builder.append(loginBaseUrl);
		
		if(loginBaseUrl.endsWith("/")) {
			builder.deleteCharAt(builder.length() - 1);
		}
		
		builder.append(loginPath);
		
		return builder.toString();
	}

	private String prepareChallangeUrl() {
		StringBuilder builder = new StringBuilder();
		builder.append(loginBaseUrl);
		
		if(loginBaseUrl.endsWith("/")) {
			builder.deleteCharAt(builder.length() - 1);
		}
		
		builder.append(challengePath);
		
		return builder.toString();
	}
}