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
.x-livesearch-match {
    font-weight: lighter;
    background-color:#EED8AE;
}
.x-livesearch-matchbase{
   font-weight: bold;
   background-color:#EE6A50;
}
.x-tree-arrows .x-tree-elbow-plus,.x-tree-arrows .x-tree-elbow-minus,.x-tree-arrows .x-tree-elbow-end-plus,.x-tree-arrows .x-tree-elbow-end-minus
	{
	background-image:url('../../../resource/ext/resources/themes/images/gray/tree/arrows.gif')
}
.x-node-expanded .x-tree-icon-parent{
	background-image:url('../../../resource/ext/resources/themes/images/gray/tree/folder-open.gif') !important;
	}
.tree-cls-parent {
	background-color: #f0f0f0 !important;
	font-size: 15;
	margin-top: 5px;
	height: 23 !important
}		
</style> 
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/grid/GridHeaderFilters.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/data/PagingMemoryProxy.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/grid/Export.js"></script>
<script type="text/javascript">	
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',
    controllers: [//声明所用到的控制层
        'plm.document.DOCManage'
    ],
    launch: function() {
    	Ext.create('erp.view.plm.document.DOCManage');//创建视图
    }
});
var page = 1;
var height = window.innerHeight;
if(Ext.isIE){//ie不支持window.innerHeight;document.documentElement.clientHeight == 0
	height = screen.height*0.75;
}
var pageSize = parseInt(height*0.7/28);
var pageSize = 13;
var dataCount = 0;
var url = '';
var msg = '';
var caller = 'DOCManage';
</script>
</head>
<body >
</body>
</html>