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
<style type="text/css">
.x-panel,.x-panel-body {
	overflow: inherit;
}
.x-grid-empty,.x-grid-tip {
	position: absolute;
	display: none;
}
.x-grid-empty {
	top: 30%;
	left: 10px;
	right: 10px;
	text-align: center;
}
.x-grid-tip {
	left: 70%;
	right: 10px;
	top: -65px;
	height: 60px;	
	z-index: 10;
}
.alert {
	margin: 0 auto;
	padding: 15px;
	color: #8a6d3b;
  	background-color: #fcf8e3;
  	border: 1px solid #faebcc;
  	border-radius: 4px;
  	-webkit-box-shadow: 0 0 7px 0 rgba(119,119,119,0.2);
  	box-shadow: 0 0 7px 0 rgba(119,119,119,0.2);
}
.arrow-border:before,.arrow-border:after {
	content: '';
	position: absolute;
	bottom: 0;
	width: 0;
	height: 0;
	border: 9px solid transparent;
}
.arrow-border.arrow-bottom-right:before {
	margin-bottom: -19px;
	right: 29px;
	border-top-color: #faebcc;
	border-right-color: #faebcc;
}
.arrow-border.arrow-bottom-right:after {
	margin-bottom: -17px;
	right: 30px;
	border-top-color: #fcf8e3;
	border-right-color: #fcf8e3;
}
.x-action-col-icon {
	cursor: pointer;
}
.x-form-field-help {
	height: 21px;
	line-height: 21px;
	color: #777;
}
.x-form-field-help>i {
	height: 16px;
	padding: 0 5px 0 14px;
	margin-top: 2px;
	float: left;
}
.pull-right {
	float: right;
}
.close {
	display: block;
}
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">	
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'scm.purchase.Vendor'
    ],
    launch: function() {
    	Ext.create('erp.view.scm.purchase.Vendor');//创建视图
    }
});
var caller = 'Vendor';
var _config=getUrlParam('_config');
var formCondition = '';
var gridCondition = '';
</script>
</head>
<body >
</body>
</html>