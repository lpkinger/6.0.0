<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css"/>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/grid/GridHeaderFilters.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/data/PagingMemoryProxy.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/grid/Export.js"></script>
<script type="text/javascript">
Ext.Loader.setConfig({
	enabled: true
});
Ext.application({
    name: 'erp',
    appFolder: basePath+'app',
    controllers: [
        'pm.mps.DeskProduct'
    ],
    launch: function() {
        Ext.create('erp.view.pm.mps.DeskProduct');
    }
});
var id='';
var dataCount = 0;
var mrpcondition=getUrlParam('urlcondition');
var BaseQueryCondition="";
var page = 1;
var caller='DeskProduct';
var msg = '';
var height = window.innerHeight*0.74;
var pageSize =parseInt(height/36);
</script>
</head>
<body>
</body>
</html>