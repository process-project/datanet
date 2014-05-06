package pl.cyfronet.datanet.web.client.util;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.MetaElement;
import com.google.gwt.dom.client.NodeList;

public class CsrfUtil {
	public static String getCsrfHeaderName() {
		MetaElement csrfHeaderNameElement = getMetaElement("_csrf_header");
		
		if(csrfHeaderNameElement != null) {
			return csrfHeaderNameElement.getContent();
		}
		
		return null;
	}
	
	public static String getCsrfParameterName() {
		MetaElement csrfParameterNameElement = getMetaElement("_csrf_parameter_name");
		
		if(csrfParameterNameElement != null) {
			return csrfParameterNameElement.getContent();
		}
		
		return null;
	}

	public static String getCsrfValue() {
		MetaElement csrfElement = getMetaElement("_csrf");
		
		if(csrfElement != null) {
			return csrfElement.getContent();
		}
		
		return null;
	}
	
	private static MetaElement getMetaElement(String name) {
		NodeList<Element> metaElements = Document.get().getElementsByTagName("meta");
		
		for(int i = 0; i < metaElements.getLength(); i++) {
			Element metaElement = metaElements.getItem(i);
			
			if(metaElement.getAttribute("name") != null && metaElement.getAttribute("name").equals(name)) {
				return (MetaElement) metaElement;
			}
		}
		
		return null;
	}
}