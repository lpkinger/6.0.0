<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%
request.setCharacterEncoding("utf-8");
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet"
	href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css"
	type="text/css"></link>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css"
	type="text/css"></link>
<style type="text/css">
.custom-grid-autoheight .x-grid-row,.custom-grid-autoheight .x-grid-row .x-grid-cell,.custom-grid-autoheight .x-grid-cell-inner
	{
	height: auto !important;
	white-space: normal;
}

.info-detail {
	float: right;
	color: blue;
	text-decoration: underline;
	cursor: pointer;
}

.x-form-display-field {
	color: #2a6496;
	font-family: "\5fae\8f6f\96c5\9ed1", sans-serif;
}

.dl-horizontal dt {
	float: left;
	width: 40px;
	overflow: hidden;
	clear: left;
	text-align: left;
	text-overflow: ellipsis;
	white-space: nowrap;
}
dt {
	font-weight: 700;
}
dt, dd {
	line-height: 1.42857143;
}
.dl-horizontal dd {
	margin-left: 50px;
}
</style>
<script type="text/javascript"
	src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript"
	src="<%=basePath %>resource/ux/grid/GridHeaderFilters.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">
// object copy
Ext.Object.copy = function(source) {
	if (source) {
		var n = {};
		for ( var key in source) {
	        n[key] = source[key];
	   	}
		return n;
	}
	return null;
};
/**
 * svn日志
 */
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath + 'app',//app文件夹所在路径
		controllers : [//声明所用到的控制层
		'ma.SvnLog' ],
		launch : function() {
			Ext.create('erp.view.ma.SvnLog');//创建视图
		}
	});
</script>
</head>
<body>
</body>
</html>