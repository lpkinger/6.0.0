<%@page import="com.uas.erp.model.Master"%>
<%@page import="com.uas.erp.model.Employee"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>优软ERP主页</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no">
<meta name="format-detection" content="telephone=no">
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="default">
<meta name="msapplication-tap-highlight" content="no" />
<link rel="stylesheet" href="<%=basePath%>resource/ext/resources/css/ext-all-gray.css" type="text/css" />
<link rel="stylesheet" href="<%=basePath%>jsps/mobile/main.css" type="text/css" />
<script type="text/javascript">
	var basePath = (function() {
		var fullPath = window.document.location.href;
		var path = window.document.location.pathname;
		var subpos = fullPath.indexOf('//');
		var subpath = subpos > -1 ? fullPath.substring(0, subpos + 2) : '';
		if (subpos > -1)
			fullPath = fullPath.substring(subpos + 2);
		var pos = fullPath.indexOf(path), sname = path.substring(0, path.substr(1).indexOf('/') + 1);
		return subpath + fullPath.substring(0, pos) + (sname == '/ERP' ? '/ERP/' : '/');
	})();
</script>
</head>
<body>
	<div id="loading">加载中...</div>
</body>
<script type="text/javascript" src="<%=basePath%>resource/ext/ext-all.js"></script>
<script type="text/javascript">
var em_name = '<%=session.getAttribute("em_name")%>';
var em_uu = '<%=session.getAttribute("em_uu")%>';
var en_uu = '<%=session.getAttribute("en_uu")%>';
var em_code = '<%=session.getAttribute("em_code")%>';
Ext.Loader.setConfig({
	enabled: true
});
Ext.application({
    name: 'erp',
    appFolder: basePath + 'app',
		controllers : [ 'mobile.Main' ],
		launch : function() {
			Ext.create('erp.view.mobile.Main');
		}
	});
</script>
  <script>
<%
	Object obj = session.getAttribute("employee");
	String sob = "";
	String sobText = "";
	if(obj != null) {
		Employee employee = (Employee)obj;
		sob = employee.getEm_master();
		Master master = employee.getCurrentMaster();
		if(master != null)
			sobText = master.getMa_function();
	}
%>
var sob = '<%=sob%>';
var sobText = '<%=sobText%>';
  </script>
</html>