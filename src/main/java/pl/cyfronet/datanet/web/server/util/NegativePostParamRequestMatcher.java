package pl.cyfronet.datanet.web.server.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.util.matcher.RequestMatcher;

public class NegativePostParamRequestMatcher implements RequestMatcher {
	private String paramName;

	public NegativePostParamRequestMatcher(String paramName) {
		this.paramName = paramName;
		
	}

	@Override
	public boolean matches(HttpServletRequest request) {
		return request.getMethod().equalsIgnoreCase("post") &&
				request.getParameter(paramName) == null;
	}
}