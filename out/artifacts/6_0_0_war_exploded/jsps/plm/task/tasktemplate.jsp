<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
request.setCharacterEncoding("utf-8");
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="<%=basePath %>resource/gnt/resources/css/ext-all-gray.css" rel="stylesheet" type="text/css" />
<!--[if IE]>
	<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-ie-scoped.css" type="text/css"></link>
<![endif]-->
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<script src="<%=basePath %>resource/gnt/ext-all.js" type="text/javascript"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">
var caller = "TaskTemplate";
var condition="";
var ptdata="";
var formCondition;
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.Ajax.request({//拿到treegrid数据
	url : basePath + 'common/singleGridPanel.action',
	params: {
		caller: caller, 
		condition: ''
	},
	callback : function(options,success,response){
		var res = new Ext.decode(response.responseText);
		if(res.columns){
        	fields = res.fields;
        	columns = res.columns;
			Ext.application({
			    name: 'erp',//为应用程序起一个名字,相当于命名空间
			    appFolder: basePath+'app',//app文件夹所在路径
			    controllers: [//声明所用到的控制层
			        'plm.task.TaskTemplate'
			    ],
			    launch: function() {
			    	Ext.create('erp.view.plm.task.TaskTemplate');//创建视图
			    }
			});
		} else if(res.exceptionInfo){
			showError(res.exceptionInfo);
		}
	}
});
</script>
</head>
<body >
</body>
</html>