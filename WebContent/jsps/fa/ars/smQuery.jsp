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
<script type="text/javascript">
Ext.Loader.setConfig({
	enabled: true
});
Ext.application({
    name: 'erp',
    appFolder: basePath + 'app',
    controllers: [ 'fa.ars.SmQuery' ],
    launch: function() {
        Ext.create('erp.view.fa.ars.SmQuery');
    }
});
var caller = 'SellerMonth!Query';
var gridCondition = '';
</script>
</head>
<body >
</body>
</html>