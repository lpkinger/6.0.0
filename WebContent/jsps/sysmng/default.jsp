<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%
	String url = request.getRequestURL().toString();
	String basePath = url.substring(0, url.length()
			- request.getRequestURI().length())
			+ request.getContextPath() + "/";
%>
<!DOCTYPE html>
<html>
<head>
<title>UAS标准后台设置</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="<%=basePath%>jsps/sysmng/css/version.css" type="text/css"></link>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<link rel="stylesheet" href="<%=basePath%>resource/ext/resources/css/ext-all-gray.css" type="text/css"></link>
<link rel="stylesheet" type="text/css" href="<%=basePath %>jsps/sysmng/css/default.css" /> 
<link rel="stylesheet" href="<%=basePath%>jsps/sysmng/css/sysmngFreeze.css" type="text/css"></link> 
<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/ux/css/CheckHeader.css" /> 
<script type="text/javascript" src="<%=basePath%>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/TabScrollerMenu.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/TabCloseMenu.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/CheckColumn.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/grid/GridHeaderFilters.js"></script>
<script type="text/javascript">
	Ext.Loader.setConfig({
		enabled : true
	});//开启动态加载
	Ext.application({
		name : 'erp',//为应用程序起一个名字,相当于命名空间
		appFolder : basePath + 'app',//app文件夹所在路径
		controllers : [//声明所用到的控制层
		'sysmng.Default' ],
		launch : function() {
			Ext.create('erp.view.sysmng.ViewPort');//创建视图
		},
	});
	var condition = '';
	var page =1;
	var total =0;
	var dataCount = 0;//结果总数
	var msg = '';
	var height = window.innerHeight;
	if(Ext.isIE){
		height = screen.height*0.73;
	}
	var pageSize = parseInt(height*0.65/23); 
	var em_type='<%=session.getAttribute("em_type")%>';
	var em_code = '<%=session.getAttribute("em_code")%>';
	var activeItem = null;
</script>
</head>
<body>
</body>
</html>