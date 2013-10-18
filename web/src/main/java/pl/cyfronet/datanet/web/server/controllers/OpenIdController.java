package pl.cyfronet.datanet.web.server.controllers;

import static pl.cyfronet.datanet.web.server.rpcservices.RpcLoginService.OPEN_ID_DISCOVERIES_ATTRIBUTE_NAME;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.openid4java.association.AssociationException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.MessageException;
import org.openid4java.message.ParameterList;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.ax.FetchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.emi.security.authn.x509.proxy.ProxyUtils;

@Controller
public class OpenIdController {
	private static final Logger log = LoggerFactory.getLogger(OpenIdController.class);
	
	@Autowired MainController mainController;
	@Autowired private ConsumerManager openIdManager;
	
	@SuppressWarnings({"unchecked" })
	@RequestMapping(value = "/", params = "openid.ns")
	public String processOpenId(Model model, HttpServletRequest request) throws IOException {
		ParameterList response = new ParameterList(request.getParameterMap());
		DiscoveryInformation discovered = (DiscoveryInformation) request.getSession().getAttribute(OPEN_ID_DISCOVERIES_ATTRIBUTE_NAME);
		StringBuffer receivingURL = request.getRequestURL();
		String queryString = request.getQueryString();
		
		if (queryString != null && queryString.length() > 0) {
            receivingURL.append("?").append(queryString);
		}
		
		VerificationResult verification = null;
		
		try {
			verification = openIdManager.verify(receivingURL.toString(), response, discovered);
			Identifier verified = verification.getVerifiedId();
			
			if (verified != null) {
	            AuthSuccess authSuccess = (AuthSuccess) verification.getAuthResponse();

	            if (authSuccess.hasExtension(AxMessage.OPENID_NS_AX)) {
	                FetchResponse fetchResp = (FetchResponse) authSuccess.getExtension(AxMessage.OPENID_NS_AX);

					List<String> emails = fetchResp.getAttributeValues("email");
	                String email = (String) emails.get(0);
					List<String> fullNames = fetchResp.getAttributeValues("fullname");
					String fullname = (String) fullNames.get(0);
					List<String> proxies = fetchResp.getAttributeValues("proxy");
					String proxy = (String) proxies.get(0);
					log.debug("Retrieved from OpenID: email: {}, full name: {} and proxy: {}", new Object[] {email, fullname, proxy});
					
					//checking the proxy
					try {
						ByteArrayInputStream baos = new ByteArrayInputStream(proxy.replace("<br>", "").getBytes());
						CertificateFactory cf = CertificateFactory.getInstance("X.509");
						X509Certificate crt = (X509Certificate) cf.generateCertificate(baos);
						boolean result = ProxyUtils.isProxy(crt);
						log.debug("Is proxy: {}", result);
					} catch (CertificateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
	        }
		} catch (MessageException | DiscoveryException | AssociationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return mainController.main(model, request);
	}
}