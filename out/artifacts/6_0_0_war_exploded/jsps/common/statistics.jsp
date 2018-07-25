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
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript"
	src="<%=basePath%>resource/jquery/jquery-1.8.0.min.js"></script>
<script type="text/javascript"
	src="<%=basePath%>resource/jquery/ichartjs.base.js"></script>
<script type="text/javascript"
	src="<%=basePath%>resource/jquery/ichartjs.pie.js"></script>
<script type="text/javascript"
	src="<%=basePath%>resource/jquery/ichart.coordinate.js"></script>
<script type="text/javascript"
	src="<%=basePath%>resource/jquery/ichartjs.column.js"></script>
<script type="text/javascript">
var basePath='<%=basePath %>';
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'common.statistics'
    ],
    launch: function() {
        Ext.create('erp.view.common.statistics');
    }
});
caller='Statistics';
</script>
<style type="text/css">
body{
  background-color: #E8E8E8;  
}
</style>
</head>
<body>
<div id='div1'></div>
<div align="center">
		<div id='canvasDiv1'></div>
		<div id='canvasDiv2'></div>
	</div>
</body>
</html>