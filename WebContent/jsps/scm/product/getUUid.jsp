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
<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css" type="text/css"></link>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<style>
.breadcrumb{
    font: 11px Arial, Helvetica, sans-serif;
    background-image:url('<%=basePath %>resource/images/screens/bc_bg.png'); 
    background-repeat:repeat-x;
    height:100%;
    line-height:30px;
    color:#9b9b9b;
    border:solid 1px #cacaca;
    width:100%;
    overflow:hidden;
    margin:0px;
    padding:0px;
}
.breadcrumb li {
    list-style-type:none;
    float:left;
    padding-left:10px;
}
.breadcrumb a{
    height:30px;
    display:block;
    background-image:url('<%=basePath %>resource/images/screens/bc_separator.png'); 
    background-repeat:no-repeat; 
    background-position:right;
    padding-right: 15px;
    text-decoration: none;
    color:#454545;
}
.home{
    border:none;
    margin: 6px 0px;
}
.breadcrumb a:hover{
	color:#35acc5;
}
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<style>
.x-custom .x-panel-default,.x-custom .x-panel-body-default {
    border-color: rgb(240, 240, 241);
}
.x-custom .x-panel-header {
	padding: 3px 10px;
	border-top: 1px solid #d0d0d0;
}
.x-custom .x-panel-header-default {
	background-color: #c8c8c8;
    background-image: -webkit-gradient(linear, 50% 0%, 50% 100%, color-stop(0%, #f0f0f0),
 color-stop(100%, #c8c8c8));
    background-image: -webkit-linear-gradient(top, #f0f0f0, #c8c8c8);
    background-image: -moz-linear-gradient(top, #f0f0f0, #c8c8c8);
    background-image: -o-linear-gradient(top, #f0f0f0, #c8c8c8);
    background-image: -ms-linear-gradient(top, #f0f0f0, #c8c8c8);
    background-image: linear-gradient(top, #f0f0f0, #c8c8c8);
}
.x-custom .x-panel-header-text {
	color: #000
}
.x-custom .x-form-item-medium .x-form-text{
	height: 32px;
	line-height: 32px;
	padding: 1px 5px;
}

.x-custom .x-grid-row .x-grid-cell, .x-custom .x-grid-cell-inner {
/* 	height: 64px;
	line-height: 64px */
}
#uuIdGrid  .x-grid-row .x-grid-cell,#uuIdGrid .x-grid-cell-inner{
	height: 64px;
	line-height: 64px 
}
.x-custom .x-text-link {
	color: #3498db;
	text-decoration: underline;
}
.x-custom .x-text-muted {
	color: #999
}
</style>
<script type="text/javascript">	
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'scm.product.GetUUid'
    ],
    launch: function() {
    	Ext.create('erp.view.scm.product.GetUUid.Viewport');//创建视图
    }
});
var caller = 'GetUUid';
var type = getUrlParam('type');
var status=getUrlParam('status');
var formCondition = '';
var gridCondition = '';
var msg = '';
var page = 1;
var value = 0;
var total = 0;
var dataCount = 0;//结果总数
var height = window.innerHeight;
if(Ext.isIE){//ie不支持window.innerHeight;document.documentElement.clientHeight == 0
	height = screen.height*0.73;
}
//var pageSize = parseInt(height*0.7/23);
var pageSize=5;
</script>
</head>
<body >
</body>
</html>