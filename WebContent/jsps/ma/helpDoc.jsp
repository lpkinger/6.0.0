<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%
	String url = request.getRequestURL().toString();
	String basePath = url.substring(0, url.length()
			- request.getRequestURI().length())
			+ request.getContextPath() + "/";
	String caller = request.getParameter("caller");
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<style type="text/css">
.search-item {
	font: normal 11px tahoma, arial, helvetica, sans-serif;
	padding: 3px 10px 3px 10px;
	border: 1px solid #fff;
	border-bottom: 1px solid #eeeeee;
	white-space: normal;
	color: #555;
}

.search-item h3 {
	display: block;
	font: inherit;
	font-weight: bold;
	color: #222;
}

.search-item h3 span {
	float: right;
	font-weight: normal;
	margin: 0 0 5px 5px;
	width: 150px;
	display: block;
	clear: none;
}

.msg .x-box-mc {
	font-size: 14px;
}

#msg-div {
	position: absolute;
	left: 50%;
	top: 10px;
	width: 400px;
	margin-left: -200px;
	z-index: 20000;
}

#msg-div .msg {
	border-radius: 8px;
	-moz-border-radius: 8px;
	background: #F6F6F6;
	border: 2px solid #ccc;
	margin-top: 2px;
	padding: 10px 15px;
	color: #555;
}

#msg-div .msg h3 {
	margin: 0 0 8px;
	font-weight: bold;
	font-size: 15px;
}

#msg-div .msg p {
	margin: 0;
}
</style>
<link rel="icon" href="<%=basePath%>resource/images/icon_title.png"
	type="image/x-icon" />
<link rel="stylesheet"
	href="<%=basePath%>resource/ext/4.2/resources/ext-theme-gray/ext-theme-gray-all.css"
	type="text/css"></link>
<link rel="stylesheet"
	href="<%=basePath%>resource/ext/4.2/resources/ext-theme-neptune/tree-neptune.css"
	type="text/css"></link>
<link rel="stylesheet" type="text/css"
	href="<%=basePath%>resource/ux/css/CheckHeader.css" />
<link rel="stylesheet" type="text/css"
	href="<%=basePath%>jsps/sys/css/GroupTabPanel.css" />
<link rel="stylesheet" type="text/css"
	href="<%=basePath%>jsps/sys/css/init.css" />
<style type="text/css">
.x-toolbar-sencha {
	background: #e0e0e0;
	color: #304c33;
	border: none !important;
}

.x-toolbar-sencha .x-logo {
	padding: 10px 10px 10px 31px;
	/*  background: url(../images/logo.png) no-repeat 10px 11px; */
	color: #fff;
	text-align: center;
	font-size: 22px;
	font-weight: bold;
	text-shadow: 0 1px 0 #4e691f;
}
</style>
<script type="text/javascript"
	src="<%=basePath%>resource/ext/4.2/ext-all.js"></script>
<%-- <script type="text/javascript"
	src="<%=basePath%>resource/ext/4.2/ext-all-debug.js"></script> --%>
<script type="text/javascript" src="<%=basePath%>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath%>jsps/sys/sysinit.js"></script>
<script type="text/javascript">
	Ext.Loader.setConfig({
		enabled : true
	});
	Ext.application({
		name : 'erp',
		appFolder : basePath + 'app',
		controllers : ['ma.HelpDoc' ],
		launch : function() {
			Ext.create('erp.view.ma.help.HelpDoc');
		}
	});
	var caller="";
	var username = '<%=session.getAttribute("username")%>';
	var em_name = '<%=session.getAttribute("em_name")%>';
	var em_uu = '<%=session.getAttribute("em_uu")%>';
	var en_uu = '<%=session.getAttribute("en_uu")%>';
	var em_code = '<%=session.getAttribute("em_code")%>';
	var en_email ='<%=session.getAttribute("en_email")%>';
	var em_type = '<%=session.getAttribute("em_type")%>';
	var em_id ='<%=session.getAttribute("em_id")%>';
	var en_admin = '<%=session.getAttribute("en_admin")%>';
	var em_defaulthsid = '<%=session.getAttribute("em_defaulthsid")%>';
</script>
</head>
<body>
</body>
</html>