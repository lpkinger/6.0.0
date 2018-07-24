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
<style type="text/css">
grid-row-checked,.x-column-header-checkbox .x-column-header-text{
	height: 14px;
	width: 14px;
	background-image:
		url(basePath+'resource/ext/resources/themes/images/gray/grid/checked.gif');
	background-position: -1px -1px;
	background-repeat: no-repeat;
	background-color: transparent
}
grid-row-checked{
	margin-left: 1px;
	background-position: 50% -2px
}
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">	
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
var condition='';
var emid=<%=session.getAttribute("em_id")%>;
var caller=getUrlParam("caller");
var formCondition = '';
var gridCondition = '1=1';
      Ext.application({
        name: 'erp',//为应用程序起一个名字,相当于命名空间
        appFolder: basePath+'app',//app文件夹所在路径
       controllers: [//声明所用到的控制层
        'oa.attention.AttentionSub'
       ],
       launch: function() {
    	Ext.create('erp.view.oa.attention.AttentionSub');//创建视图
        }
      });
</script>
</head>
<body >
</body>
</html>