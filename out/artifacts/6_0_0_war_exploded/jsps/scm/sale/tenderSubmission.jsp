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
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
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
        'scm.sale.TenderSubmission'
    ],
    launch: function() {
    	Ext.create('erp.view.scm.sale.TenderSubmission');//创建视图
    }
});
var caller = 'TenderSubmission';
var formCondition = getUrlParam('formCondition');
var gridCondition = getUrlParam('gridCondition');
var id = formCondition.substr(formCondition.indexOf('idEQ')+4).replace(/[' "]/g,"");
if(formCondition.indexOf('idIS')>-1){
	id = formCondition.substr(formCondition.indexOf('idIS')+4).replace(/[' "]/g,"");
}
if(formCondition.indexOf('readOnly')>-1){
	var readOnly =  true;
	
}
</script>
</head>
<body>
</body>
</html>