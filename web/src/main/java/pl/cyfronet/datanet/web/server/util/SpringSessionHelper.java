package pl.cyfronet.datanet.web.server.util;

import javax.servlet.http.HttpSession;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class SpringSessionHelper {
	public static HttpSession getHttpSession() {
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		
		return attr.getRequest().getSession();
	}
	
	public static void setSecurityContext(SecurityContext securityContext) {
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		attr.getRequest().getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
	}
	
	public static SecurityContext getSecurityContext() {
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		
		return (SecurityContext) attr.getRequest().getSession().getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
	}
	
	public static void clearSecurityContext() {
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		attr.getRequest().getSession().removeAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
	}
	
	public static String getUserLogin() {
		SecurityContext securityContext = getSecurityContext();
		
		if(securityContext != null && securityContext.getAuthentication() != null) {
			return securityContext.getAuthentication().getName();
		}
		
		return null;
	}
}