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
<%-- <link rel="stylesheet"
	href="<%=basePath%>resource/ext/4.2/resources/ext-theme-neptune/ext-theme-neptune-all.css"
	type="text/css"></link>	 --%>
<link rel="stylesheet"
	href="<%=basePath%>resource/ext/4.2/resources/ext-theme-gray/ext-theme-gray-all.css"
	type="text/css"></link>	
<link rel="stylesheet" href="<%=basePath%>resource/css/opensys.css" type="text/css"></link>	 
<script type="text/javascript"
	src="<%=basePath%>resource/ext/4.2/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
 <script type="text/javascript" src="<%=basePath %>resource/ux/grid/GridHeaderFilters.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/data/PagingMemoryProxy.js"></script> 
<script type="text/javascript">
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',
    appFolder: basePath+'app',
    controllers: [
        'opensys.Complaint'
    ],
    launch: function() {
        Ext.create('erp.view.opensys.complaint.ViewPort');
    }
});
var caller ='CurComplaint';
</script>
</head>
<body>

</body>
</html>