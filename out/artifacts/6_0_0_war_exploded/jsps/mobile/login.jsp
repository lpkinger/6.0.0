<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8" />
<meta name="viewport"
	content="initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=0, width=device-width">
<meta http-equiv="Cache-Control" content="no-siteapp" />
<title>优软管理系统,usoftchina.com</title>
<meta name="description"
	content="USOFTCHINA.COM,ERP,SCM,CRM,MRP,企业管理,优软" />
<meta name="keywords" content="USOFTCHINA.COM,ERP,SCM,CRM,MRP,企业管理,优软" />
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<base href="<%=basePath%>jsps/mobile/" />
<script>
window.basePath = (function() {
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
<link rel="stylesheet" href="login.css" type="text/css" />
</head>
<body>
	<div id="header-container">
		<div class="center">
			<h2>优软管理系统</h2>
		</div>
	</div>
	<div id="app-container">
		<form id="form" action="#">
			<input type="text" name="username" id="username" placeholder="账号"
				autocomplete="off" tabindex="1" class="txtinput" required="required">
			<input type="password" name="password" " id="password"
				placeholder="密码" autocomplete="off" tabindex="2" class="txtinput"
				required="required">
			<form:select path="masters" hidefocus="true" id="master"
				class="selmenu">
				<form:options items="${masters}" itemValue="ma_name"
					itemLabel="ma_name" />
			</form:select>
			<input type="button" name="submit" id="submitbtn" class="submitbtn"
				onclick="mobileLogin();" tabindex="7" value="登录&raquo;">
		</form>
	</div>
	<div id="footer-container">
		<p class="link">
			<a href="#" id="download_client">客户端下载</a> | <a
				href="<%=basePath%>?mobile=0">电脑版</a>
		</p>
	</div>
</body>
<script type="text/javascript"
	src="<%=basePath%>resource/jquery/jquery-1.4.min.js"></script>
<script type="text/javascript"
	src="<%=basePath%>resource/jquery/jquery.json-2.2.min.js"></script>
<script type="text/javascript" src="login.js"></script>
</html>