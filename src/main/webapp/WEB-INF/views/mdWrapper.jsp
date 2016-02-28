<%@ include file="include.jsp" %>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8"/>
		<c:set var="titleKey">${resourceKey}.page.title</c:set>
		<title><spring:message code="${titleKey}"/></title>
		<link href="<c:url value="/img/favicon.ico"/>" rel="icon" type="image/x-icon" />
		<link href="<c:url value="/css/bootstrap-combined.no-icons.min.css"/>" rel="stylesheet"/>
		<link href="<c:url value="/css/font-awesome.min.css"/>" rel="stylesheet"/>
	</head>
	<body>
		<div class="container">
    		${mdContents}
    	</div>
	</body>
</html>
