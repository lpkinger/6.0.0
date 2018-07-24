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
<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css"/>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<style type="text/css">
	.mylabel{
		font-size: 16px;
        font-weight: 600;
        /* color: red; */
        margin-top: 10px;
        float: left;
        margin-left: 10px;
	}
	.myradio{
		margin-top: 35px;
		margin-left: 10px;
		color: black;
	}
	.labeltext{
		color: gray;
		margin-left: 45px;
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
		controllers : [ 'ma.encryptConfig' ],
		launch : function() {
			Ext.create('erp.view.ma.encryptConfig');
		}
});
</script>
</head>
<body>
</body>
</html>