<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<html>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<head>
</head>
<body>
<div id="maindiv">
	<iframe id="iframe_page" style="width:100%;height:98%;" src="<%=basePath %>mobile/mobileRealTimeCharts.action?numId=${param.numId}" align="left" scrolling="auto" frameborder="0"></iframe>
</div>
</body>	
</html>
