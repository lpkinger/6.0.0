<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%-- <link rel="stylesheet" href="<%=basePath %>resource/css/main-sysnavigation.css" type="text/css"></link> --%>
<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css" type="text/css"></link>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>

<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/ux/css/CheckHeader.css" />
<script type="text/javascript" src="<%=basePath %>resource/ux/CheckColumn.js"></script>

<style type="text/css">
.flow_mainpanel > .x-panel-body-default{
	border-top:none !important;
}
.flow_mainpanel > .x-panel-header-default-top {
	height:40px;
	background-image:none;
	background-color:#f2f2f2;
}
.flow_mainpanel > .x-panel-header-default-top > .x-panel-header-body{
	height:40px;
}
.flow_mainpanel > .x-panel-header-default-top > .x-panel-header-body > .x-box-inner > .x-panel-header-text-container{
    padding-top: 6px;
    padding-left: 6px;
    height:40px !important;
}
.flow_mainpanel > .x-panel-header-default-top > .x-panel-header-body > .x-box-inner > .x-panel-header-text-container
> .x-panel-header-text{
	font-family: "microsoft yahei", sans-serif;
    font-size: 17px;
    color: black;
    font-weight: bold;
}
.x-flow-tbar .x-btn .x-btn-center .x-btn-inner{
	color:#367fae
}
.x-flow-tbar{
	padding:5px 0 5px 0;
	background: unset;
}
.x-btn-gray{
	height:22px;
	border-color: #a1a1a1;
	background-image:none;
	background-color:#f2f2f2;
	background: -webkit-linear-gradient(#fff, #e3e3e3); /* Safari 5.1 - 6.0 */
  	background: -o-linear-gradient(#fff, #e3e3e3); /* Opera 11.1 - 12.0 */
  	background: -moz-linear-gradient(#fff, #e3e3e3); /* Firefox 3.6 - 15 */
  	background: linear-gradient(#fff, #e3e3e3);
}
.x-btn-gray:hover{
	border-color: #656565;
}
#flowbutton .tree-nav-add{
	width: 16px;
    height: auto;
    top: 19px;
    left: 0;
    bottom: 0;
    right: auto;
}
.x-tree-icon-parent {
    height: 16px !important;
    width: 16px !important;
    background: url(../../../resource/images/tree/book.png) no-repeat 0px -0px !important;
}
.x-tree-cls-close .x-tree-icon-leaf{
	margin-right:4px !important;
	margin-top:4px !important;
    width: 16px;
    background-image: url(css/close.png);
}
.x-tree-cls-enable .x-tree-icon-leaf{
	margin-right:4px !important;
	margin-top:4px !important;
    width: 16px;
	background-image: url(css/enable.png);
}
.x-tree-panel .x-grid-row .x-grid-cell-inner{
	height:26px;
    line-height: 24px;
}
.x-tree-panel .x-grid-row .x-grid-cell-inner img {
    margin-top: 3px;
}
.u-toolbar {
    height: 30px;
    margin-top: 0px;
    background-color: #f0f0f0;
    padding: 1px 0 5px 2px;
}
.u-toolbar-group .x-btn-gray {
	top:4px !important;
    border-radius: 0;
    height: 20px;
    border-radius: 0;
    padding: 1px;
}
</style>
<script type="text/javascript">
Ext.Loader.setConfig({
	enabled: true,
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
    	'oa.flow.FlowDefine'
    ],
    launch: function() {
         Ext.create('erp.view.oa.flow.flowDefine.viewport');
    }
});
var flowCaller = getUrlParam('flowcaller');
var caller = 'Form';
var em_type="<%=session.getAttribute("em_type")%>";
</script>
</head>
<body>

</body>
</html>