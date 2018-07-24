<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
	request.setCharacterEncoding("utf-8");
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="<%=basePath%>resource/ext/resources/css/ext-all-gray.css" type="text/css"></link>
<link rel="stylesheet" href="<%=basePath%>resource/css/main.css" type="text/css"></link>
<style>
.checked {
	padding-left: 12px;
	background-image: url("<%=basePath%>resource/images/accept.png") !important;
	background-repeat: no-repeat !important;
}

.error {
	padding-left: 12px;
	background-image: url("<%=basePath%>resource/images/renderer/important.png") !important;
	background-repeat: no-repeat !important;
}

.custom-alt .x-grid-cell {
	border-top-color: #999;
	border-top-style: dashed;
	background-color: #EAEAEA;
}
</style>
<script type="text/javascript" src="<%=basePath%>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/ux/RowExpander.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/i18n/i18n.js"></script>
<script type="text/javascript">
/**
 * 配置同步
 */
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath + 'app',//app文件夹所在路径
		controllers : [//声明所用到的控制层
		'ma.Conf' ],
		launch : function() {
			Ext.create('erp.view.ma.Conf');//创建视图
		}
	});
</script>
</head>
<body>
</body>
</html>