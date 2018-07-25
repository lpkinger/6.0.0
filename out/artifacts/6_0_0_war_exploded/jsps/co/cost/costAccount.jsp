<!-- 自动摊提折旧作业页面 -->
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet"
	href="<%=basePath%>resource/ext/resources/css/ext-all-gray.css"
	type="text/css"></link>
<link rel="stylesheet" href="<%=basePath%>resource/css/main.css"
	type="text/css"></link>
<style>
#desc li {
	padding: 10px;
	line-height: 1;
	vertical-align: middle;
}
#desc li img {
	vertical-align: -2px;
	margin-right: 3px;
}
</style>
<script type="text/javascript"
	src="<%=basePath%>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/i18n/i18n.js"></script>
<script type="text/javascript">
/* 自动摊提折旧作业 */
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath + 'app',//app文件夹所在路径
		controllers : [//声明所用到的控制层
		'co.cost.CostAccount' ],
		launch : function() {
			Ext.create('erp.view.co.cost.CostAccount');//创建视图
		}
	});
	var caller = 'StepCost';
</script>
</head>
<body>
	<ul id="desc">
		<li><img src="<%=basePath%>resource/images/renderer/remind.png"/>成本计算需要进行大量数据计算，请不要频繁点击!</li>
		<li><img src="<%=basePath%>resource/images/renderer/remind.png"/>单次计算耗时在5~30分钟，可以在操作日志里面查看计算进度.</li>
	</ul>
</body>
</html>