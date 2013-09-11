<%@ include file="include.jsp" %>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8"/>
		<meta name="gwt:property" content="locale=${locale}">
		<title><spring:message code="main.page.title"/></title>
		<link href="img/favicon.ico" rel="icon" type="image/x-icon" />
		<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->
		<!--[if IE 7]>
 		<link rel="stylesheet" href="ria/css/font-awesome-ie7.css">
		<![endif]-->
		
		<!-- highlight style (can be replaced with default.css), javascript and initialization -->
		<link rel="stylesheet" href="highlight/styles/github.css">
		<script type="text/javascript" src="highlight/highlight.pack.js"></script>
		<script type="text/javascript">hljs.initHighlightingOnLoad();</script>
		
		<!-- GWT module -->
		<script type="text/javascript" src="/datanet/ria/ria.nocache.js"></script>
	</head>
	<body>
    	<iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>
	</body>
</html>
