<%@ include file="include.jsp" %>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8"/>
		<title><spring:message code="404.page.title"/></title>
		<link href="img/favicon.ico" rel="icon" type="image/x-icon" />
		
		<style type="text/css">
			body {
		      color: #666;
		      text-align: center;
		      font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
		      sans-serif;
		      margin:0;
		      width: 800px;
		      margin: auto;
		      font-size: 14px;
		    }
		    h1 {
		      font-size: 56px;
		      line-height: 100px;
		      font-weight: normal;
		      color: #456;
		    }
		
		    h3 {
		      color: #456;
		      font-size: 20px;
		      font-weight: normal;
		      line-height: 28px;
		    }
		
		    hr {
		      margin: 18px 0;
		      border: 0;
		      border-top: 1px solid #EEE;
		      border-bottom: 1px solid white;
		    }
		</style>		
	</head>
	<body>
    	<h1>404</h1>
    	<img src="img/datanet-logo-143x40.png" />
		<h3><spring:message code="404.page.message"/></h3>
		<hr/>
		<p><spring:message code="404.page.description"/></p>
	</body>
</html>