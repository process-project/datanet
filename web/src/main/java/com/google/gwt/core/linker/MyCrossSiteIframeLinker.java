package com.google.gwt.core.linker;

import com.google.gwt.core.ext.LinkerContext;

public class MyCrossSiteIframeLinker extends CrossSiteIframeLinker {
	@Override
	protected String getJsDevModeRedirectHookPermitted(LinkerContext context) {
		return "$wnd.location.protocol == \"http:\" || $wnd.location.protocol == \"file:\" || $wnd.location.protocol == \"https:\"";
	}
	
	@Override
	protected String getJsDevModeUrlValidation(LinkerContext context) {
		String regexp = getStringConfigurationProperty(context, "devModeUrlWhitelistRegexp",
	        "https://(localhost|127\\.0\\.0\\.1)(:\\d+)?/.*");
	    if (!regexp.isEmpty()) {
	      return ""
	          + "if (!/^" + regexp.replace("/", "\\/") + "$/.test(devModeUrl)) {\n"
	          + "  if (devModeUrl && window.console && console.log) {\n"
	          + "    console.log('Ignoring non-whitelisted Dev Mode URL: ' + devModeUrl);\n"
	          + "  }\n"
	          + "  devModeUrl = '';"
	          + "}";
	    }
	    return "";
	}
}