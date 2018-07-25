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
<link rel="stylesheet" href="<%=basePath %>resource/ext/4.2/resources/ext-theme-neptune/tree-neptune.css" type="text/css"></link>
<style>
.resultList-grid .x-column-header{display:none; }
::-webkit-scrollbar{
	width:1px; /* 设置滚动条宽度 */
}
</style>
<script type="text/javascript" src="<%=basePath%>resource/ext/4.2/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="http://api.map.baidu.com/api?&v=1.3"></script>
<script type="text/javascript" src="http://api.map.baidu.com/library/CurveLine/1.5/src/CurveLine.min.js"></script>
<script type="text/javascript">
Ext.Loader.setConfig({
	enabled: true
});
Ext.application({
	name: 'erp',
    appFolder: basePath + 'app',
    controllers : [ 'hr.attendance.CustomerDbfind' ],
    launch : function() {
			Ext.create('erp.view.hr.attendance.CustomerDbfind');
    }
});
</script>
</head>
<body>
</body>
</html>