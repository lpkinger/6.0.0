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
<style type="text/css">

.x-window-body-default {
    border-color: #bcb1b0;
    border-width: 0px!important;
    background: #e0e0e0;
    color: black;
}

.x-window-body {
    position: relative;
    border-style: none!important; 
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
        'fs.credit.ItemsValueSet'
    ],
    launch: function() {
        Ext.create('erp.view.fs.credit.ItemsValueSet');
    }
});
var caller=getUrlParam('whoami');
var condition = getUrlParam('gridCondition');
var gridCondition = '';

var ctid='';

if(condition.indexOf('IS')>0){
	ctid = condition.substring(condition.indexOf('IS')+2,condition.length); 
}else{
	ctid = condition.substring(condition.indexOf('=')+2,condition.length-1); 
}
</script>
</head>
<body >
</body>
</html>