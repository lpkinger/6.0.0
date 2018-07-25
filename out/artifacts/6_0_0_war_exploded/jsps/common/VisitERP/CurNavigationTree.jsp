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

<style type="text/css">
.x-tree-panel .x-grid-row .x-grid-cell {
    border: 1px solid #cacaca;
    background-color: #f5f5f5;
    border-top: none;
    border-right: none;
}
.x-btn-gray {
	background: #d5d5d5;
	background: -moz-linear-gradient(top, #fff 0, #efefef 38%, #d5d5d5 88%);
	background: -webkit-gradient(linear, left top, left bottom, color-stop(0%, #fff),
		color-stop(38%, #efefef), color-stop(88%, #d5d5d5));
	filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#fff',
		endColorstr='#d5d5d5', GradientType=0);
	border-color: #bfbfbf;
	border-radius: 2px;
	vertical-align: bottom;
	text-align: center;
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
.x-action-col-icon {
    height: 16px;
    margin-left:5px !important;
}
</style>
<script type="text/javascript">
function showInformation(msg, fn){
	Ext.MessageBox.show({
     	title: $I18N.common.msg.title_prompt,
     	msg: msg,
     	buttons: Ext.Msg.OK,
     	icon: Ext.Msg.INFO,
     	fn: fn
	});
}
Ext.Loader.setConfig({
	enabled: true,
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
    	'common.VisitERP.CurNavigationTree'
    ],
    launch: function() {
         Ext.create('erp.view.common.VisitERP.CurNavigationTree');
    }
});
var caller = "CurNavigationTree";
var formCondition = '';
var gridCondition = '';
</script>
</head>
<body>

</body>
</html>