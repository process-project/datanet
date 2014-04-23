<%@ include file="include.jsp" %>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8"/>
		<title><spring:message code="500.page.title"/></title>
		<link href="<c:url value="/img/favicon.ico" />" rel="icon" type="image/x-icon" />
		<link rel="stylesheet" href="<c:url value="/css/error.css"/>">		
	</head>
	<body>
    	<h1>500</h1>
    	<img src="<c:url value="/img/datanet-logo-143x40.png"/>" />
		<h3><spring:message code="500.page.message" /></h3>
		<hr/>
		<p><spring:message code="500.page.description" /></p>
	</body>
</html>