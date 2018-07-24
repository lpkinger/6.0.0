<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
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
<title>UAS管理系统-初始化数据导入</title>
<link rel="icon" href="<%=basePath%>resource/images/icon_title.png" type="image/x-icon"/>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="<%=basePath%>resource/ext/resources/css/ext-all.css" type="text/css"></link>
<link rel="stylesheet" type="text/css" href="<%=basePath%>resource/ux/css/CheckHeader.css" />
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
	background: #CECECE url('<%=basePath%>resource/images/grid/header-bg.png') repeat center center;
}

.x-panel .x-box-item .x-accordion-item .x-panel-default .x-collapsed .x-panel-collapsed .x-panel-default-collapsed {
	height: 26px !important;
}

.custom .x-grid-header-ct,.custom .x-column-header {
	cursor: pointer;
	border-top: 1px solid #92c9eb;
	background: #65a9d7;
	background: -webkit-gradient(linear, left top, left bottom, from(#ced3d9), to(#65a9d7) );
	background: -webkit-linear-gradient(top, #ced3d9, #65a9d7);
	background: -moz-linear-gradient(top, #ced3d9, #65a9d7);
	background: -ms-linear-gradient(top, #ced3d9, #65a9d7);
	background: -o-linear-gradient(top, #ced3d9, #65a9d7);
}
.custom-button {
	cursor: pointer;
	border-top: 1px solid #96d1f8;
	background: #8eb6d1;
	background: -webkit-gradient(linear, left top, left bottom, from(#dfebf2), to(#8eb6d1) );
	background: -webkit-linear-gradient(top, #dfebf2, #8eb6d1);
	background: -moz-linear-gradient(top, #dfebf2, #8eb6d1);
	background: -ms-linear-gradient(top, #dfebf2, #8eb6d1);
	background: -o-linear-gradient(top, #dfebf2, #8eb6d1);
	-webkit-border-radius: 4px;
	-moz-border-radius: 4px;
	border-radius: 4px;
	font-size: 14px;
	font-family: Georgia, serif;
	vertical-align: middle;
	padding: 3px 5px;
}
.custom-button:hover {
	background: #5aa4d6;
	color: #ccc;
}
.custom-button:active {
	background: #96d1f8;
}
.up {
	border: none;
    background: -webkit-linear-gradient(top, #dfebf2, #8eb6d1) !important;
    background: -webkit-gradient(linear, left top, left bottom, from(#dfebf2), to(#8eb6d1) )!important;
    background: -webkit-linear-gradient(top, #dfebf2, #8eb6d1)!important;
    background: -moz-linear-gradient(top, #dfebf2, #8eb6d1)!important;
    background: -ms-linear-gradient(top, #dfebf2, #8eb6d1)!important;
    background: -o-linear-gradient(top, #dfebf2, #8eb6d1)!important;
	margin-top: -2px;
}
.up:hover {
    background: #5aa4d6 !important;
}
#upexcel-body{
	background: -webkit-linear-gradient(top, #dfebf2, #8eb6d1) !important;
    background: -webkit-gradient(linear, left top, left bottom, from(#dfebf2), to(#8eb6d1) )!important;
    background: -webkit-linear-gradient(top, #dfebf2, #8eb6d1)!important;
    background: -moz-linear-gradient(top, #dfebf2, #8eb6d1)!important;
    background: -ms-linear-gradient(top, #dfebf2, #8eb6d1)!important;
    background: -o-linear-gradient(top, #dfebf2, #8eb6d1)!important;
}
#upexcel-body:hover{
	background: #5aa4d6 !important;
}
.custom-log {
	margin-left: 20px
}
.custom-log tr {
	line-height: 26px
}
.custom-log td {
	padding: 5px 10px;
	border: 1px solid #dfdfdf
}
.custom-log .custom-tr {
	background-color: #dfdfdf;
  	font-weight: bold
}
</style>
<script type="text/javascript" src="<%=basePath%>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/ux/RowExpander.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/ux/CheckColumn.js"></script>
<script type="text/javascript">
	removeCss(basePath + 'resource/css/upgrade/gray/main.css');
	function removeCss(filename) {
		var targetelement = "link";
	    var targetattr = "href";
	    var allsuspects = document.getElementsByTagName(targetelement);
	    for (var i = allsuspects.length; i >= 0; i--) {
	        if (allsuspects[i] && allsuspects[i].getAttribute(targetattr) != null && allsuspects[i].getAttribute(
	                targetattr).indexOf(filename) != -1)
	            allsuspects[i].parentNode.removeChild(allsuspects[i])
	    }
	}
</script>
<script type="text/javascript">
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath + 'app',//app文件夹所在路径
		controllers : [//声明所用到的控制层
		'common.Template' ],
		launch : function() {
			Ext.create('erp.view.common.init.Template');//创建视图
		}
	});
	var caller = 'InitDetail';
	
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