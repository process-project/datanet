package pl.cyfronet.datanet.web.server.util;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.RPCRequest;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Based on http://code.google.com/p/spring4gwt
 * 
 */
@SuppressWarnings("serial")
public class SpringGwtRemoteServiceServlet extends RemoteServiceServlet {
	private static final Logger log = LoggerFactory.getLogger(SpringGwtRemoteServiceServlet.class);

	private ApplicationContext applicationContext;

	public SpringGwtRemoteServiceServlet(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	public String processCall(String payload) throws SerializationException {
		try {
			Object handler = getBean(getThreadLocalRequest());
			RPCRequest rpcRequest = RPC.decodeRequest(payload, handler.getClass(), this);
			onAfterRequestDeserialized(rpcRequest);
			log.trace("Invoking {}.{}", handler.getClass().getName(), rpcRequest.getMethod().getName());
			
			return RPC.invokeAndEncodeResponse(handler, rpcRequest.getMethod(), rpcRequest.getParameters(), rpcRequest.getSerializationPolicy());
		} catch(IncompatibleRemoteServiceException e) {
			log.error("An IncompatibleRemoteServiceException was thrown while processing this call.", e);
			
			return RPC.encodeResponseForFailure(null, e);
		} catch(Exception e) {
			log.error("Could not properly handle an RPC call", e);
			
			return RPC.encodeResponseForFailure(null, e);
		}
	}

	/**
	 * Determine Spring bean to handle request based on request URL, e.g. a
	 * request ending in /myService will be handled by bean with name
	 * "myService".
	 * 
	 * @param request
	 * @return handler bean
	 */
	protected Object getBean(HttpServletRequest request) {
		String service = getService(request);
		Object bean = getBean(service);
		
		if (!(bean instanceof RemoteService)) {
			throw new IllegalArgumentException(
					"Spring bean is not a GWT RemoteService: " + service + " ("	+ bean + ")");
		}

		log.trace("Bean for service {} is {}", service, bean);
		
		return bean;
	}

	/**
	 * Parse the service name from the request URL.
	 * 
	 * @param request
	 * @return bean name
	 */
	protected String getService(HttpServletRequest request) {
		String url = request.getRequestURI();
		String service = url.substring(url.lastIndexOf("/") + 1);
		log.trace("Service for URL {} is {}", url, service);
		
		return service;
	}

	/**
	 * Look up a spring bean with the specified name in the current web
	 * application context.
	 * 
	 * @param name
	 *            bean name
	 * @return the bean
	 */
	protected Object getBean(String name) {
		if (applicationContext == null) {
			throw new IllegalStateException(
					"No Spring web application context found");
		}
		
		if (!applicationContext.containsBean(name)) {
				throw new IllegalArgumentException("Spring bean not found: " + name);
		}
		
		return applicationContext.getBean(name);
	}
}