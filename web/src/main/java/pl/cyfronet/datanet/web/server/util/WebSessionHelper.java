package pl.cyfronet.datanet.web.server.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class WebSessionHelper {
	public static HttpSession getCurrentSession() {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		
	    return requestAttributes.getRequest().getSession(false);
	}

	/**
	 * Returns current application URL down to the context path.
	 */
	public static String getCurrentUrl() {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = requestAttributes.getRequest();
		String result = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
		
	    return result;
	}
}