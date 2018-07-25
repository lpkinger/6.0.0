<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link type="text/css" rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css"/>
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
        'plm.project.ProjectMessage'
    ],
    launch: function() {
    	Ext.create('erp.view.plm.project.ProjectMessage');//创建视图
    }
});
var caller = 'ProjectMessage';
var formCondition = getUrlParam('formCondition');
var gridCondition = '';
var prjCode = getUrlParam('prjCode');
var prjid;
if(formCondition.indexOf('IS')>0){
	prjid = formCondition.substring(formCondition.indexOf('IS')+2,formCondition.length); 
}else{
	prjid = formCondition.substring(formCondition.indexOf('=')+2,formCondition.length-1); 
}

</script>
</head>
<body>
</body>
</html>