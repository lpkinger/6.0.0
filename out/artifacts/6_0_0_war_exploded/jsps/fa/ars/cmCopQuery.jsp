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
<style>
	.custom-blank .x-grid-cell{
		background-color: #F9F7F8;
	}
	.custom .x-grid-cell{
		background-color: #F5F2CD;
	}
	.custom-alt .x-grid-cell{
		background-color: #F3F2F0;
	}
	.custom-first .x-grid-cell{
		border-top-color: #999; 
		border-top-style: dashed;
		background-color: #EEE8CD;
	}
	.custom-alt-first .x-grid-cell{
		border-top-color: #999; 
		border-top-style: dashed;
		background-color: #EAEAEA;
	}
	.custom-grid .x-grid-row-over .x-grid-cell { 
	    background-color: #BCD2EE; 
	    border-bottom-color: #999; 
	    border-top-color: #999; 
	} 
	 
	.custom-grid .x-grid-row-selected .x-grid-cell { 
	    background-color: #BCD2EE !important; 
	}
</style>
<script type="text/javascript" src="<%=basePath %>resource/ux/grid/GridHeaderFilters.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/data/PagingMemoryProxy.js"></script>
<script type="text/javascript">

Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'fa.ars.CmCopQuery'
    ],
    launch: function() {
        Ext.create('erp.view.fa.ars.cmCopQuery.Viewport');
    }
});
	var caller = getUrlParam('whoami');
	var condition = '';
	var page = 1;
	var value = 0;
	var total = 0;
	var dataCount = 0;//结果总数
	var msg = '';
	var height = window.innerHeight;
	var pageSize = parseInt(height*0.7/20);
	var keyField = "";
	var pfField = "";
	var url = "";
</script>
</head>
<body >
</body>
</html>