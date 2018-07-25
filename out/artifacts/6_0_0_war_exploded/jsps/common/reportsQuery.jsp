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
<link rel="stylesheet" href="<%=basePath %>resource/css/reports.css" type="text/css"></link>
<script type="text/javascript" src="<%=basePath%>resource/ext/4.2/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">
	var code = getUrlParam('code');
	var title = getUrlParam('title');
</script>
<script type="text/javascript">
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'common.reportsQuery'
    ],
    launch: function() {
    	Ext.create('erp.view.common.reportsQuery.viewport');//创建视图
    }
});
</script>
</head>
<body >
</body>
</html>