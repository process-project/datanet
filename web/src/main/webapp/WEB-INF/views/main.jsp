<%@ include file="include.jsp" %>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8"/>
		<meta name="gwt:property" content="locale=${locale}"/>
		<meta name="_csrf" content="${_csrf.token}"/>
		<meta name="_csrf_header" content="${_csrf.headerName}"/>
		<meta name="_csrf_parameter_name" content="${_csrf.parameterName}"/>
		<title><spring:message code="main.page.title"/></title>
		<link href="<c:url value="/img/favicon.ico"/>" rel="icon" type="image/x-icon" />
		<!--[if lt IE 9]>
		<script src="<c:url value="/js/html5.js"/>"></script>
		<![endif]-->
		<!--[if IE 7]>
 		<link rel="stylesheet" href="<c:url value="/ria/css/font-awesome-ie7.css"/>">
		<![endif]-->
		
		<!-- highlight style (can be replaced with default.css), javascript and initialization -->
		<link rel="stylesheet" href="<c:url value="/highlight/styles/github.css"/>" />
		<script type="text/javascript" src="<c:url value="/highlight/highlight.pack.js"/>"></script>
		<script type="text/javascript">hljs.initHighlightingOnLoad();</script>
		
		<!-- GWT module -->
		<script type="text/javascript" src="<c:url value="/ria/ria.nocache.js"/>"></script>
	</head>
	<body>
    	<iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>
    	<c:if test="${processingError != null}">
    		<div id="processingError" style="display: none;">
    			${processingError}
    		</div>
    	</c:if>
	</body>
</html>