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
var _i = getUrlParam('id'), _c = getUrlParam('class');
if(_i != null) {
	if(_c != 'billtask') {
		window.location.href = basePath + 'jsps/plm/task/task.jsp?_noc=1&formCondition=idIS' + _i + '&gridCondition=ra_taskidIS' + _i;
	} else {
		window.location.href = basePath + 'jsps/plm/task/billtask.jsp?_noc=1&formCondition=idIS' + _i;
	}
} else {
	var caller ='ProjectTask!Bill';
	Ext.Loader.setConfig({
		enabled: true
	});//开启动态加载
	Ext.application({
	    name: 'erp',//为应用程序起一个名字,相当于命名空间
	    appFolder: basePath+'app',//app文件夹所在路径
	    controllers: [//声明所用到的控制层
	        'plm.task.BillTask'
	    ],
	    launch: function() {
	    	Ext.create('erp.view.plm.task.BillTask');//创建视图
	    }
	});	
}
</script>
</head>
<body >
</body>
</html>