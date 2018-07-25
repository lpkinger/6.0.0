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
<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css" type="text/css"></link>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/ux/css/CheckHeader.css" />
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/CheckColumn.js"></script>
<style type="text/css">
.upexcel {
	background-image: url('<%=basePath%>resource/images/excel.png');
}

.history {
	background-image: url('<%=basePath%>resource/images/drink.png');
}

.check {
	background-image: url('<%=basePath%>resource/images/hourglass.png');
}

.save {
	background-image: url('<%=basePath%>resource/images/drink.png');
}

.x-button-icon-close {
	background-image: url('<%=basePath%>resource/images/icon/trash.png');
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
        'ma.update.DataUpdate'
    ],
    launch: function() {
    	Ext.create('erp.view.ma.update.DataUpdate');//创建视图
    }
});
	var caller = "DataUpdate";
	var schemeDetails = new Array();
	var em_code='<%=session.getAttribute("em_code")%>';
	var formCondition = '';
	var gridCondition = '';	
	var schemeStore= new Ext.data.Store({
		proxy : {
			type : 'ajax',
			url : basePath +'ma/update/getUpdateScheme.action',
			extraParams:{
				em_code:em_code
			},
			reader: {
		          type: 'json',
		          root: 'schemes'
				}
		},
		autoLoad : true,
		fields : [{name : 'id_'},{name : 'title_'}]
	});
</script>
</head>
<body >
</body>
</html>