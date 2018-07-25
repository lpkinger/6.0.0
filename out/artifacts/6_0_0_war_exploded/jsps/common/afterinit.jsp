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
<link rel="stylesheet" href="<%=basePath%>resource/ext/resources/css/ext-all.css" type="text/css"></link>
<style type="text/css">
.custom-button {
	cursor: pointer;
	border-top: 1px solid #96d1f8;
	background-color: #dce9f9;
	background-image: -webkit-gradient(linear, left top, left bottom, from(#ebf3fc),to(#dce9f9) );
	background-image: -webkit-linear-gradient(top, #ebf3fc, #dce9f9);
	background-image: -moz-linear-gradient(top, #ebf3fc, #dce9f9);
	background-image: -ms-linear-gradient(top, #ebf3fc, #dce9f9);
	background-image: -o-linear-gradient(top, #ebf3fc, #dce9f9);
	background-image: linear-gradient(top, #ebf3fc, #dce9f9);
	text-shadow: 0 1px 0 rgba(255, 255, 255, .5);
	-webkit-border-radius: 4px;
	-moz-border-radius: 4px;
	border-radius: 4px;
	font-size: 14px;
	font-family: Georgia, serif;
	vertical-align: middle;
	padding: 3px 5px;
}
.custom-button:hover {
	background: #96d1f8;
	color: #ccc;
}
.custom-button:active {
	background: #96d1f8;
}
</style>
<script type="text/javascript" src="<%=basePath%>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/i18n/i18n.js"></script>
<script type="text/javascript">
Ext.Loader.setConfig({
	enabled: true
});
Ext.application({
	name: 'erp',
    appFolder: basePath + 'app',
		controllers : [ 'common.AfterInit' ],
		launch : function() {
			Ext.create('erp.view.common.init.AfterInit');
		}
});
var caller = 'AfterInit';
</script>
</head>
<body>
</body>
</html>