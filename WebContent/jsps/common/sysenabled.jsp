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
 <link rel="icon" href="<%=basePath %>resource/images/icon_title.png" type="image/x-icon"/>
<link rel="stylesheet" href="<%=basePath%>resource/ext/4.2/resources/ext-theme-gray/ext-theme-gray-all.css" type="text/css"></link>
<style>
.loading {
	background: url("<%=basePath %>resource/images/loading.gif") no-repeat center!important; 
}
.checked {
	background: url("<%=basePath %>resource/images/renderer/finishrecord.png") no-repeat center!important; 
}
.error {
	background: url("<%=basePath %>resource/images/renderer/important.png") no-repeat center!important; 
}
.refresh{
    background: url('<%=basePath %>resource/images/refresh.gif')  no-repeat;
}
</style>
<script type="text/javascript" src="<%=basePath%>resource/ext/4.2/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/i18n/i18n.js"></script>
<script type="text/javascript">
Ext.Loader.setConfig({
	enabled: true
});
Ext.application({
	name: 'erp',
    appFolder: basePath + 'app',
    controllers : [ 'common.SysEnabled' ],
    launch : function() {
			Ext.create('erp.view.common.sysinit.SysEnabled');
    }
});
var Height=window.innerHeight-4;
var caller=getUrlParam('whoami'),detailEl;
</script>
</head>
<body>
</body>
</html>