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
<style type="text/css">	
	.x-grid-view .x-grid-row .x-grid-cell {
		background-color: #e0e0ff;
		border-color: #ededed;
		border-style: solid;
		border-width: 1px 0;
		border-top-color: #fafafa;
	}
	
	.x-grid-view tbody>.x-grid-wrap-row td.x-grid-rowwrap .x-grid-cell {
		background-color: #f1f2f5;
	}
	
	.x-grid-view tbody>.x-grid-wrap-row:nth-child(odd) td.x-grid-rowwrap .x-grid-cell {
		background-color: #e0e0ff;
	}
	
	.x-grid-view .x-grid-row-alt .x-grid-cell,.x-grid-row-alt .x-grid-rowwrap-div {
		background-color: #f1f2f5;
	}
	
	.x-grid-view .x-grid-row-over .x-grid-cell,.x-grid-row-over .x-grid-rowwrap-div {
		border-color: #eaeaea;
		background-color: #bcd2ee;
		color: green
	}
	
	.x-grid-view .x-grid-row-focused .x-grid-cell,.x-grid-row-focused .x-grid-rowwrap-div
		{
		border-color: #157fcc;
		background-color: #dce9f9
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
    controllers : [ 'hr.employee.EmployeeTrack' ],
    launch : function() {
			Ext.create('erp.view.hr.employee.EmployeeTrack');
    }
});
var Height=window.innerHeight-4;
</script>
</head>
<body>
</body>
</html>