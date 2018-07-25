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
<style>
	tr.x-grid-record-gray .x-grid-cell {
    background: #CDC9C9 !important;
    font-style: italic !important;
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
        'ma.Form'
    ],
    launch: function() {
    	Ext.create('erp.view.ma.Form');//创建视图
    }
});
	var caller = "Form";
	var dbfinds = [];
	var formCondition = '';
	var gridCondition = '';
    function Delete(){
    	var grid=Ext.getCmp('dbgrid');
	    var lastselected=grid.getSelectionModel().getLastSelected();
	    grid.getStore().remove(lastselected);
    }
    var em_type="<%=session.getAttribute("em_type")%>";
</script>
</head>
<body >
</body>
</html>