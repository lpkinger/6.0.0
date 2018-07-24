<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css"/>
	<!--[if IE]>
		<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-ie-scoped.css" type="text/css"></link>
	<![endif]-->
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/jquery/jquery-1.4.min.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/other/highcharts.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/other/funnel.js"></script>
<script type="text/javascript">
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
 Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'crm.chance.FunnelCRM'
    ],
    launch: function() {
        Ext.create('erp.view.crm.chance.FunnelCRM');
    }
}); 
var caller = getUrlParam('whoami');

</script>
</head>
<body >
<div id='funnel'></div>
<div id='funnel1'></div>
<div id="demo"></div>
</body>
</html>