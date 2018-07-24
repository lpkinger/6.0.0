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
<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css" type="text/css"></link>
<!--[if IE]>
	<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-ie.css" type="text/css"></link>
<![endif]-->
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/grid/GridHeaderFilters.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">	
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'fa.arp.TurnEstimate'
    ],
    launch: function() {
    	Ext.create('erp.view.fa.arp.TurnEstimate');//创建视图
    }
});
var caller = getUrlParam('whoami');
var urlcondition = getUrlParam('urlcondition');
var formCondition = '';
var gridCondition = '';
var config = getUrlParam('gridCondition');
var caller = '';
var condition = '';
var page = 1;
var value = 0;
var total = 0;
var dataCount = 0;
var msg = '';
var height = window.innerHeight;
if(Ext.isIE){
	height = screen.height*0.73;
}
var pageSize = parseInt(height*0.7/23);
var keyField = "";
var pfField = "";
var url = "";
var relative = null;
</script>
</head>
<body >
</body>
</html>