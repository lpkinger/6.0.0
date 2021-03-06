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
<style>
	.custom-total .x-grid-cell{
		background-color: #CDB38B;
	}
	.x-form-display-field {
		font-size: 14px;
		color: blue
	}
	.x-form-item-label {
		font-size: 11px
	}

</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">	
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'fa.arp.ShowVmCopDetail'
    ],
    launch: function() {
    	Ext.create('erp.view.fa.arp.vmCopQuery.ShowVmDetail');//创建视图
    }
});
/* var caller = '${param.whoami}'; */
var showtype = getUrlParam('showtype');
var vmid = getUrlParam('vmid');
var yearmonth = getUrlParam('yearmonth');
var currency = getUrlParam('currency');
var vendcode = getUrlParam('vendcode');
var vendname = getUrlParam('vendname');
var chkumio = getUrlParam('chkumio');
var cop = getUrlParam('cop');
</script>
</head>
<body >
</body>
</html>