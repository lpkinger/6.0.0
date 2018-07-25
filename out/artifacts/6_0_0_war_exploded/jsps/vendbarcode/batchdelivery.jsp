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
<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css"/>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/ux/css/CheckHeader.css" />
<style type="text/css">

 .custom-turned .x-grid-cell {
	background: #ffdab9 !important;
	font-style: italic !important;
	border-color: #ededed;
	border-style: solid;
	border-width: 1px 0;
	border-top-color: #fafafa;
	height: 26px;
	line-height: 26px
}
.myCls{
    color: red;
}
.custom-turned .x-grid-cell-inner {
	background: #ffdab9 !important;
	font-style: italic !important;
}
 
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/CheckColumn.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/zebraBrowserPrint/BrowserPrint-1.0.4.min.js"></script> 
<script type="text/javascript" src="<%=basePath %>resource/zebraBrowserPrint/zebraPrint.js"></script> 
<script type="text/javascript">
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'vendbarcode.BatchDelivery'
    ],
    launch: function() {
        Ext.create('erp.view.vendbarcode.batchDelivery.Viewport');
    }
});
	var caller = getUrlParam('whoami');
	var dataCount = 0;//结果总数
	caller = caller.replace(/'/g, "");
	var urlcondition = getUrlParam('urlcondition');
	var height = window.innerHeight;
	if(Ext.isIE){//ie不支持window.innerHeight;document.documentElement.clientHeight == 0
		height = screen.height*0.73;
	}
	var pageSize = parseInt(height*0.7/23);
	var page = 1;
	var value = 0;
	var total = 0;
	var page = 1;
	var pageSize = 50;
</script>
</head>
<body>
</body>
</html>