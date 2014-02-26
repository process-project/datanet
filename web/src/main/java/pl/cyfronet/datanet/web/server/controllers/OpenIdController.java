package pl.cyfronet.datanet.web.server.controllers;

import static pl.cyfronet.datanet.web.server.rpcservices.RpcLoginService.OPEN_ID_DISCOVERIES_ATTRIBUTE_NAME;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.ParameterList;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.ax.FetchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.RequestContextUtils;

import pl.cyfronet.datanet.web.server.db.HibernateUserDao;
import pl.cyfronet.datanet.web.server.db.beans.UserDbEntity;
import pl.cyfronet.datanet.web.server.rpcservices.RpcLoginService;
import eu.emi.security.authn.x509.proxy.ProxyUtils;

@Controller
public class OpenIdController {
	private static final Logger log = LoggerFactory.getLogger(OpenIdController.class);
	
	@Autowired private MainController mainController;
	@Autowired private ConsumerManager openIdManager;
	@Autowired private MessageSource messages;
	@Autowired private HibernateUserDao userDao;
	
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
					List<String> userCerts = fetchResp.getAttributeValues("userCert");
					String userCert = (String) userCerts.get(0);
					List<String> proxyPrivKeys = fetchResp.getAttributeValues("proxyPrivKey");
					String proxyPrivKey = (String) proxyPrivKeys.get(0);
					boolean canLogin = true;
					String completeProxy = null;
					
					try {
						completeProxy = getProxyContent(proxy, userCert, proxyPrivKey);
						CertificateFactory cf = CertificateFactory.getInstance("X.509");
						X509Certificate crt = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(replaceBrWithNewLines(proxy).getBytes()));
						
						if (!ProxyUtils.isProxy(crt)) {
							model.addAttribute("processingError", messages.getMessage("open.id.user.proxy.unrecongnized", null, RequestContextUtils.getLocale(request)));
							log.info("OpenID login for {} impossible because of invalid proxy certificate", authSuccess.getIdentity());
							canLogin = false;
						}
						
						Date current = Calendar.getInstance().getTime();
						
						if (current.after(crt.getNotAfter()) || current.before(crt.getNotBefore())) {
							model.addAttribute("processingError", messages.getMessage("open.id.user.proxy.expired", null, RequestContextUtils.getLocale(request)));
							log.info("OpenID login for {} impossible because of expired proxy certificate", authSuccess.getIdentity());
							canLogin = false;
						}
					} catch (CertificateException e) {
						log.error("Could not retrieve a valid user proxy");
						log.error("Error was:", e);
						model.addAttribute("processingError", messages.getMessage("open.id.user.proxy.error", null, RequestContextUtils.getLocale(request)));
						canLogin = false;
					}
					
					if (canLogin) {
						UserDbEntity user = userDao.getUser(authSuccess.getIdentity());
						
						if(user == null) {
							user = new UserDbEntity();
							user.setLogin(authSuccess.getIdentity());
						}
						
						user.setLastLogin(Calendar.getInstance().getTime());
						userDao.saveUser(user);
						
						List<GrantedAuthority> authorities = new ArrayList<>();
						authorities.add(new SimpleGrantedAuthority(RpcLoginService.USER_ROLE));
						
						Authentication authentication = new UsernamePasswordAuthenticationToken(authSuccess.getIdentity(), completeProxy, authorities);
						SecurityContextHolder.getContext().setAuthentication(authentication);
						
						return "redirect:/";
					}
	            }
	        } else {
	        	model.addAttribute("processingError", messages.getMessage("open.id.verification.failed", null, RequestContextUtils.getLocale(request)));
	        }
		} catch (Exception e) {
			log.error("Could not properly finish the OpenID login procedure");
			log.error("Error was:", e);
			model.addAttribute("processingError", messages.getMessage("open.id.sequence.error", null, RequestContextUtils.getLocale(request)));
		}

		return mainController.main(model, request);
	}

	private String getProxyContent(String proxy, String userCert, String proxyPrivKey) {
		return replaceBrWithNewLines(proxy) + replaceBrWithNewLines(proxyPrivKey) + replaceBrWithNewLines(userCert);
	}

	private String replaceBrWithNewLines(String contents) {
		return contents.replaceAll("<br>", "\n");
	}
}