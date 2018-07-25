<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<%
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ request.getContextPath() + "/";
%>
<meta name="viewport"
	content="width=device-width, initial-scale=1, maximum-scale=1, minimum-scale=1, user-scalable=no, minimal-ui">
<link rel="apple-touch-icon" href="images/apple-touch-icon.png" />
<link rel="apple-touch-startup-image"
	href="images/apple-touch-startup-image-320x460.png" />

<title>新闻公告</title>
<base href="<%=basePath%>jsps/mobile/" />
<link
	href='http://fonts.useso.com/css?family=Source+Sans+Pro:400,300,700,900'
	rel='stylesheet' type='text/css' />
<style type="text/css">
#content {
	font-size: 14px;
	width: 100%;
    bottom: 50px;
	height: 80% !important;
}

html {
	overflow-x: hidden;
	overflow-y: auto;
}

img {
	max-width: 100%;
}

.bottombar {
	background-color: #0972b9;
	position: fixed;
	bottom: 0px;
	width: 100%;
	height: 50px;
}
</style>
</head>
<script type="text/javascript"
	src="<%=basePath%>resource/jquery/jquery-1.8.0.min.js"></script>
<script type="text/javascript"
	src="<%=basePath%>resource/bootstrap/bootstrap.min.js"></script>

<script type="text/javascript">
var basePath = (function() {
	var fullPath = window.document.location.href;
	var path = window.document.location.pathname;
	var subpos = fullPath.indexOf('//');
	var subpath = subpos > -1 ? fullPath.substring(0, subpos + 2) : '';
	if (subpos > -1)
		fullPath = fullPath.substring(subpos + 2);
	var pos = fullPath.indexOf(path), sname = path.substring(0, path.substr(1).indexOf('/') + 1);
	sname = (['/jsps','/workfloweditor','/resource','/system','/process','/demo','/exam','/oa','/opensys','/mobile'].indexOf(sname) > -1 ? '/' : sname);
	return subpath + fullPath.substring(0, pos) + sname + (sname == '/' ? '' : '/');
})();
</script>
<script type="text/javascript"
	src="<%=basePath%>resource/jquery/jquery-1.8.0.min.js"></script>
<body>
	<div id="content">${content}</div>
	<!-- 按钮触发模态框 -->
    <div style="height: 50px;"></div>
</body>
</html>