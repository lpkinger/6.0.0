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
<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/css/TabScrollerMenu.css"/>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/TabScrollerMenu.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/TabCloseMenu.js"></script>	
<style>
	 .x-panel-body-default {
		background: #f1f2f5;
	}
		
	#benchlist .x-grid-row .x-grid-cell {
		background-color: #f1f2f5 !important; 
		padding:0 0 0 20px;
	}
	
	.x-tab-bar-top .x-tab-bar-strip-default-plain {
	    top: 0px;
	    height: 0px !important;;
	    border-width: 0px 0px 0px;
	}
	
	.x-panel-header {
	    padding: 1px 4px 2px 5px;
	}
	
</style>

<script type="text/javascript">
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'ma.bench.SingleBenchSet'
    ],
    launch: function() {
        Ext.create('erp.view.ma.bench.SingleBenchSet');
    }
});
var caller = '';
var benchcode = getUrlParam('benchcode');

<%
Object is_saas = session.getAttribute("isSaas");
boolean isSaas = is_saas != null && Boolean.valueOf(is_saas.toString());
%>
var isSaas = <%=isSaas%>;
</script>
</head>
<body>
</body>
</html>