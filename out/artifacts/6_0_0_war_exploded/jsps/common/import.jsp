<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@page import="com.uas.erp.model.Employee"%>
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
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<link rel="stylesheet"
	href="<%=basePath%>resource/ext/resources/css/ext-all-gray.css"
	type="text/css"></link>
<link rel="stylesheet" type="text/css"
	href="<%=basePath%>resource/ux/css/CheckHeader.css" />
<style type="text/css">
.export {
	background-image: url('<%=basePath%>resource/images/download.png');
}

.upexcel {
	background-image: url('<%=basePath%>resource/images/excel.png');
}

.history {
	background-image: url('<%=basePath%>resource/images/drink.png');
}

.rule {
	background-image: url('<%=basePath%>resource/images/query.png');
}

.check {
	background-image: url('<%=basePath%>resource/images/hourglass.png');
}

.save {
	background-image: url('<%=basePath%>resource/images/drink.png');
}

.delete {
	background-image: url('<%=basePath%>resource/images/icon/trash.png');
}

.x-button-icon-add {
	background-image: url('<%=basePath%>resource/images/add.png');
}

.x-button-icon-close {
	background-image: url('<%=basePath%>resource/images/icon/trash.png');
}

.x-button-icon-copy {
	background-image: url('<%=basePath%>resource/images/copy.png');
}

.x-button-icon-paste {
	background-image: url('<%=basePath%>resource/images/paste.png');
}

.x-button-icon-up {
	background-image: url('<%=basePath%>resource/images/up.png');
}

.x-button-icon-down {
	background-image: url('<%=basePath%>resource/images/down.png');
}

.x-btn-tb {
	border: none;
}

.x-btn-bar-s {
	border: 1px solid gray;
}

.x-td-warn {
	background: #FF7F24 !important;
}

.x-grid-row-selected .x-grid-cell-special {
	border-right: 1px solid #d4b7b7;
	background: #CECECE
		url('<%=basePath%>resource/images/grid/header-bg.png') repeat center
		center;
}

.x-panel .x-box-item .x-accordion-item .x-panel-default .x-collapsed .x-panel-collapsed .x-panel-default-collapsed
	{
	height: 26px !important;
}

/* .custom .x-grid-header-ct,.custom .x-column-header {
	cursor: pointer;
	border-top: 1px solid #92c9eb;
	background: #65a9d7;
	background: -webkit-gradient(linear, left top, left bottom, from(#ced3d9),
		to(#65a9d7));
	background: -webkit-linear-gradient(top, #ced3d9, #65a9d7);
	background: -moz-linear-gradient(top, #ced3d9, #65a9d7);
	background: -ms-linear-gradient(top, #ced3d9, #65a9d7);
	background: -o-linear-gradient(top, #ced3d9, #65a9d7);
} */

.custom-button {
	cursor: pointer;
	border: 1px solid #bbbbbb;
	background-color: white;
	background: -webkit-linear-gradient(top, #ffffff, #f9f9f9 48%, #e2e2e2 52%, #e7e7e7);
	background: -webkit-linear-gradient(top, #ffffff, #f9f9f9 48%, #e2e2e2 52%, #e7e7e7);
	background: -moz-linear-gradient(top, #ffffff, #f9f9f9 48%, #e2e2e2 52%, #e7e7e7);
	background: -ms-linear-gradient(top, #ffffff, #f9f9f9 48%, #e2e2e2 52%, #e7e7e7);
	background: -o-linear-gradient(top, #ffffff, #f9f9f9 48%, #e2e2e2 52%, #e7e7e7);
	-webkit-border-radius: 4px;
	-moz-border-radius: 4px;
	border-radius: 4px;
	font-size: 14px;
	font-family: Georgia, serif;
	vertical-align: middle;
	padding: 3px 5px;
}

.custom-button:hover {
	background: #F0F0F0;
	color: #ccc;
}

.custom-button:active {
	background: #F0F0F0;
}

.up {
	border: none;
	background: transparent;
	margin-top: -1px;
}

.custom-log tr td {
	padding: 3px 3px 3px 3px;
	text-align: center;
	vertical-align: middle;
}

.custom-log .custom-tr {
	border-top: 1px solid #96d1f8;
	background: #65a9d7;
	background: -webkit-gradient(linear, left top, left bottom, from(#ced3d9),
		to(#65a9d7));
	background: -webkit-linear-gradient(top, #ced3d9, #65a9d7);
	background: -moz-linear-gradient(top, #ced3d9, #65a9d7);
	background: -ms-linear-gradient(top, #ced3d9, #65a9d7);
	background: -o-linear-gradient(top, #ced3d9, #65a9d7);
}
</style>
<script type="text/javascript"
	src="<%=basePath%>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/i18n/i18n.js"></script>
<script type="text/javascript"
	src="<%=basePath%>resource/ux/grid/Export.js"></script>
<script type="text/javascript"
	src="<%=basePath%>resource/ux/RowExpander.js"></script>
<script type="text/javascript"
	src="<%=basePath%>resource/ux/CheckColumn.js"></script>
<script type="text/javascript">
	Ext.Loader.setConfig({
		enabled : true
	});//开启动态加载
	Ext.application({
		name : 'erp',//为应用程序起一个名字,相当于命名空间
		appFolder : basePath + 'app',//app文件夹所在路径
		controllers : ['common.Import' ],//声明所用到的控制层
		launch : function() {
			Ext.create('erp.view.common.init.Import');//创建视图
		}
	});
	var caller = getUrlParam('whoami');
	var title= getUrlParam('title');
	<%
	Object obj = session.getAttribute("employee");
	String em_type = "";
	if(obj != null) {
		Employee employee = (Employee)obj;
		em_type=employee.getEm_type();
	}
	%>
	var em_type = '<%= em_type %>';
</script>
</head>
<body>
</body>
</html>