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
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/CheckColumn.js"></script>
<script type="text/javascript">
Ext.Loader.setConfig({
	enabled: true
});
Ext.application({
    name: 'erp',
    appFolder: basePath+'app',
    controllers: [
        'ma.group.AutoSync'
    ],
    launch: function() {
    	Ext.create('erp.view.ma.group.AutoSync');
    }
});
var caller = "AutoSync";
</script>
</head>
<body >
</body>
</html>