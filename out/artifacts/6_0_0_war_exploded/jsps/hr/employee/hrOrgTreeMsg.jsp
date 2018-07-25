<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css" type="text/css"></link>
<!--[if IE]>
	<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-ie-scoped.css" type="text/css"></link>
<![endif]-->
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<style type="text/css">
	.x-html-editor-wrap textarea {
		background-color: #EEE8CD;
	}
	.x-hrorgTreeFirstLeaf {
	    font-weight:600 !important;
	    background-color: #f1f1f1 !important;
	} 
	.x-hrorgTree {
		color: null;
	    font: normal 11px tahoma, arial, verdana, sans-serif;
	    background-color: #f1f1f1 !important;
	    border-color: #ededed;
	    border-style: solid;
	    border-width: 1px 0;
	    border-top-color: #fafafa;
	    height: 26px;
	    line-height: 26px;
	} 
	.x-hrorgTree:hover {
	 	background-color:#FAFAFA !important;
	    font-size: 14px;
	    font-weight: bold;
	    color: green;
	}
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">
Ext.Loader.setConfig({
	enabled: true,
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
    	'hr.employee.HrOrgTreeMsg'
    ],
    launch: function() {
         Ext.create('erp.view.hr.employee.HrOrgTreeMsg');
    }
});
var caller = "HrOrgTreeMsg";
var formCondition = '';
var gridCondition = '';
</script>
</head>
<body>

</body>
</html>