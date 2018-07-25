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
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">
//问题反馈编号：2016120061
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'ma.ComboSet'
    ],
    launch: function() {
    	Ext.create('erp.view.ma.comboset.ComboSet');//创建视图
    }
});
	var gridCondition =getUrlParam('gridCondition');
	gridCondition = (gridCondition == null || gridCondition == "null") ? "" : gridCondition;
	gridCondition = gridCondition.replace(/IS/g, "=");
	var caller = gridCondition.substring(gridCondition.indexOf('=')+1,gridCondition.lastIndexOf(' AND'));
	var field = gridCondition.substring(gridCondition.lastIndexOf('=')+1);
</script>
</head>
<body >
</body>
</html>