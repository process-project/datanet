<%@ include file="include.jsp" %>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8"/>
		<title><spring:message code="error.page.title"/></title>
		<link href="img/favicon.ico" rel="icon" type="image/x-icon" />
		<link rel="stylesheet" href="css/error.css">		
	</head>
	<body>
    	<h1><spring:message code="error.page.message"/></h1>
    	<img src="img/datanet-logo-143x40.png" />
		<h3>${exception.getLocalizedMessage()}</h3>
		<hr/>
		<p><spring:message code="error.page.description"/></p>
	</body>
</html>