<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
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
<link rel="stylesheet"
	href="<%=basePath%>resource/ext/resources/css/ext-all-gray.css"
	type="text/css"></link>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<link rel="stylesheet" type="text/css"
	href="<%=basePath%>resource/ux/css/CheckHeader.css" />
<style type="text/css">

.x-form-checkbox{
	width:40px;
	height:20px;
	background-image: url('<%=basePath%>resource/images/unchecked.png');
 	background-position:center;
 	margin-left:5px;
 
}
.x-form-cb-checked .x-form-checkbox{
	background-image: url('<%=basePath%>resource/images/checked.png');
	background-position:center;
}
.la-checkfield{
font-family:黑体;font-size:20px;color:#838B8B;
margin:0px 0px 10px 10px;
}
 .la-unactive{
 font-size: 15px ;font-family:"Microsoft YaHei UI",'宋体';color:#838B8B;
 font-weight:550;
/* font-family:黑体;font-size:16px;font-weight:bold; */
} 
.la-active{
 font-size: 15px ;font-family:"Microsoft YaHei UI",'宋体';color:#0ce04f;
  font-weight:550;
/* font-family:黑体;font-size:16px;font-weight:bold; */
}
.background-image{
	background: white url('<%=basePath%>resource/images/salary.png') ; 
}
.x-button-send {
	background-image: url('<%=basePath%>resource/images/send4.png') !important;
}
.x-images-set-enabled{ 
	background-image: url('<%=basePath%>resource/images/set/enabled.png') ;
}
.x-button-icon-check{
	background-image: url('<%=basePath%>resource/images/hourglass.png') ;
}

.delete {
	background-image: url('<%=basePath%>resource/images/icon/trash.png');
}
.x-data-config{
 background-image: url('<%=basePath%>resource/images/config.png');
}
.x-data-download{
 background-image: url('<%=basePath%>resource/images/download2.png');
}
.x-data-loadall{
 background-image: url('<%=basePath%>resource/images/download2.png');
}
.x-data-check{
 background-image: url('<%=basePath%>resource/images/check.png');
}
.x-data-delete{
 background-image: url('<%=basePath%>resource/images/delete.png');
}
.x-data-save{
 background-image: url('<%=basePath%>resource/images/save.png');
}
.x-data-toformal{
 background-image: url('<%=basePath%>resource/images/toformal.png');
}
.x-data-import{
 background-image: url('<%=basePath%>resource/images/import.png');
}
.x-data-sendnow{
 background-image: url('<%=basePath%>resource/images/sendnow.png');
}
.x-data-sendnow{
 background-image: url('<%=basePath%>resource/images/sendnow.png');
}
.x-data-sendontime{
 background-image: url('<%=basePath%>resource/images/sendontime.png');
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
.x-aaa{
	margin-top:-2px;
}
</style>
<script type="text/javascript"
	src="<%=basePath%>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/grid/GridHeaderFilters.js"></script>
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
		controllers : ['salary.Salary' ],//声明所用到的控制层
		launch : function() {
			Ext.create('erp.view.salary.Viewport');//创建视图
		}
	});
	var caller = 'Salary';
	var title= getUrlParam('title');
	var mobile = '<%=session.getAttribute("em_mobile")%>';
	var login= '<%=session.getAttribute("salary")%>';
</script>
</head>
<body>
</body>
</html>