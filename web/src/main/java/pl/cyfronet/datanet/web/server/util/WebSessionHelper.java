package pl.cyfronet.datanet.web.server.util;

import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class WebSessionHelper {
	public static HttpSession getCurrentSession() {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		
	    return requestAttributes.getRequest().getSession(false);
	}
}