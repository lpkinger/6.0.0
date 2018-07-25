<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<%-- <link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css" type="text/css"></link> --%>
<%-- <link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>  --%>
<link rel="stylesheet" href="<%=basePath %>resource/ext/4.2/simple.css" type="text/css"></link>  
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<style type="text/css">
.x-grid-cell-inner{
 padding: 0px 0px!important;
}
.x-grid-cell-process{
 background: white !important;
 border-right: 0px !important;
}
</style>
<%-- <script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script> --%>
<script type="text/javascript"
	src="<%=basePath%>resource/ext/4.2/ext-all.js"></script>
<link rel="stylesheet"
	href="<%=basePath%>resource/ext/4.2/resources/ext-theme-gray/ext-theme-gray-all.css"
	type="text/css"></link>
<link rel="stylesheet"
	href="<%=basePath%>resource/ext/4.2/resources/ext-theme-neptune/tree-neptune.css"
	type="text/css"></link>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<style type="text/css">
.x-btn-default-toolbar-small-icon-text-left .x-btn-inner {
    line-height: 20px;
}
</style>
<script type="text/javascript">	
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',
    appFolder: basePath+'app',
    controllers: ['crm.chance.ChanceProcess'],
    launch: function() {
    	Ext.create('erp.view.crm.chance.ChanceProcess');
    }
});
</script>
</head>
<body >
</body>
</html>