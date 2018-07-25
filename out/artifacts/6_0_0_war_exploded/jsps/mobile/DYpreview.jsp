<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="user-scalable=no, width=device-width, initial-scale=1.0" />
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/jquery/jquery-1.11.3.min.js"></script>  	
<script type="text/javascript" src="<%=basePath%>resource/jquery/jquery.json-2.2.min.js"></script>
<link href="<%=basePath%>resource/bootstrap/bootstrap.min.css" rel="stylesheet">
<link href="<%=basePath%>resource/bootstrap/bootstrap-datetimepicker.min.css" rel="stylesheet">
<base href="<%=basePath%>jsps/mobile/" />
<script type="text/javascript" src="DYpreview.js"></script>
<link rel="stylesheet" href="DYpreview.css" type="text/css" />
<title>订阅号预览</title>
</head>
<body class="remove">
	<div id = "title" class="page-header"></div>
	<div id = "intro"></div>
	<div id = "main"></div>
	<div id = "image"></div>
</body>
<script>
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
	var title = '<%=request.getAttribute("title")%>';
	var id = '<%=request.getAttribute("id")%>';
	showMain(id,title);
</script>
</html>