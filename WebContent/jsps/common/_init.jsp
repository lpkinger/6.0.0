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
<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all.css" type="text/css"></link>
<style type="text/css">
body{
	background-image: url("<%=basePath%>resource/images/screens/bg_blue.jpg");
}
.prev{
	background-image: url('<%=basePath %>resource/images/prev.png');
}
.next{
	background-image: url('<%=basePath %>resource/images/next.png');
}
.save{
	background-image: url('<%=basePath %>resource/images/drink.png');
}
.stepon{
	background: #CECECE url('<%=basePath %>resource/images/grid/header-bg.png') repeat center center !important;
	font-size: 13.5px;
	-moz-box-shadow:1px 1px 2px rgba(0,0,0,.2); 
	-webkit-box-shadow:1px 1px 2px rgba(0,0,0,.2); 
	box-shadow:1px 1px 2px rgba(0,0,0,.2); 
	-moz-border-radius:5px; 
	-webkit-border-radius:5px; 
	border-radius:5px;
	width: 150px !important;
	height: 22;
	text-align: center;
}
.stepon .x-btn-center .x-btn-inner:hover{
	color: white;
	text-decoration: underline;
	cursor: pointer;
}
.stepoff{
	font-size: 13px;
	-moz-box-shadow:1px 1px 2px rgba(0,0,0,.2); 
	-webkit-box-shadow:1px 1px 2px rgba(0,0,0,.2); 
	box-shadow:1px 1px 2px rgba(0,0,0,.2); 
	-moz-border-radius:5px; 
	-webkit-border-radius:5px; 
	border-radius:5px;
	width: 150px !important;
	height: 22;
	text-align: center;
	background: #CECECE url('<%=basePath %>resource/ext/resources/themes/images/default/grid/grid-blue-hd.gif') repeat center center!important;
}
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/grid/Export.js"></script>
<script type="text/javascript">
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'common.Init'
    ],
    launch: function() {
    	Ext.create('erp.view.common.init.Init');//创建视图
    }
});
</script>
</head>
<body >
</body>
</html>