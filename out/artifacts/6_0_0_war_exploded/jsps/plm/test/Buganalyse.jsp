<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css" type="text/css"></link>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<style type="text/css">
#sidebar a:link { 
color:#1C86EE; 
text-decoration:none; 
} 
#sidebar a:visited { 
color:#1C86EE; 
text-decoration:none; 
} 
#sidebar a:hover { 
color:#CD2626; 
text-decoration:none; 
} 
#sidebar a:active { 
color:#1C86EE; 
text-decoration:none; 
} 
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
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
        'plm.test.Buganalyse'
    ],
    launch: function() {
    	Ext.create('erp.view.plm.test.Buganalyse');//创建视图
    }
});
var dataCount = 0;
var condition="";
var page = 1;
var caller='';
var msg = '';
var height = window.innerHeight*0.83;
var pageSize = parseInt(height*0.7/20);
var startdate='';
var enddate='';
var menu='';
</script>
</head>
<body >
</body>
</html>