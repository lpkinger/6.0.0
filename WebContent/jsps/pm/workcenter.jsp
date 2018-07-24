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
        'pm.WorkCenter'
    ],
    launch: function() {
        Ext.create('erp.view.pm.WorkCenter');
    }
});
	var basestarttime=new Date();
	var caller = getUrlParam('whoami');
	caller = caller.replace(/'/g, "");
	var urlcondition = getUrlParam('urlcondition');
	var em_id ='<%=session.getAttribute("em_id")%>'; 
</script>
</head>
<body>
</body>
</html>