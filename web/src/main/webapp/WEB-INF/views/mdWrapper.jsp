<%@ include file="include.jsp" %>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8"/>
		<c:set var="titleKey">${resourceKey}.page.title</c:set>
		<title><spring:message code="${titleKey}"/></title>
		<link href="../../img/favicon.ico" rel="icon" type="image/x-icon" />
		<link href="//netdna.bootstrapcdn.com/twitter-bootstrap/2.3.2/css/bootstrap-combined.no-icons.min.css" rel="stylesheet"/>
		<link href="//netdna.bootstrapcdn.com/font-awesome/3.2.1/css/font-awesome.css" rel="stylesheet"/>
	</head>
	<body>
		<div class="container">
    		${mdContents}
    	</div>
	</body>
</html>
