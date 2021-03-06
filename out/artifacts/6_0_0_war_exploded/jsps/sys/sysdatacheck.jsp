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
<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css" type="text/css"></link>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<style>
.x-btn-default-small{
	border-radius:0px;
}
.x-btn-default-small-disabled{
	border-color:#9d9d9d;
	background-image: -webkit-linear-gradient(top, #9d9d9d, #9d9d9d);
}
.loading {
	background: url("<%=basePath %>resource/images/loading.gif") no-repeat center!important; 
}
.checked {
	background: url("<%=basePath %>resource/images/renderer/finishrecord.png") no-repeat center!important; 
}
.error {
	background: url("<%=basePath %>resource/images/renderer/important.png") no-repeat center!important; 
}
.x-btn-default-small-disabled .x-btn-inner {
	color:#fbfbfb !important
}
#_error_stack{
	display:block !important;
}

</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/RowExpander.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">
/**
 * 优化
 */
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'sys.init.SysDataCheck'
    ],
    launch: function() {
    	Ext.create('erp.view.sys.init.SysDataCheck');//创建视图
    }
});
</script>
</head>
<body >
</body>
</html>