<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
request.setCharacterEncoding("utf-8");
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css" type="text/css"></link>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/ux/css/CheckHeader.css" />
<style type="text/css">
.x-form-field-cir-focus{
	-moz-box-shadow:1px 1px 2px rgba(0,0,0,.2); 
	-webkit-box-shadow:1px 1px 2px rgba(0,0,0,.2); 
	box-shadow:1px 1px 2px rgba(0,0,0,.2); 
	-moz-border-radius:5px; 
	-webkit-border-radius:5px; 
	border-radius:5px;
	background: #ffffff !important;
	border: 1px solid #CD950C !important;
}
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/CheckColumn.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/RowExpander.js"></script>
<script type="text/javascript">
Ext.Loader.setConfig({
	enabled: true
});
Ext.application({
    name: 'erp',
    appFolder: basePath+'app',
    controllers: [
        'ma.SysCheckTreeScan'
    ],
    launch: function() {
    	Ext.create('erp.view.ma.SysCheckTreeScan');
    }
});
	var caller = "SysCheckTreeScan";
	var formCondition = '';
	var gridCondition = '';
</script>
</head>
<body >
</body>
</html>