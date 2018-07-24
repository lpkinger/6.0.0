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
<style type="text/css">
   .x-grid-cell-inner {
    overflow: hidden;
    -o-text-overflow: ellipsis;
    text-overflow: ellipsis;
    padding: 0px 6px;
    height:auto;
  }  
  .x-grid-row-selected .x-grid-cell-special{
   background: #cecece !important;
  }
  .x-tree-arrows .x-tree-elbow-plus,.x-tree-arrows .x-tree-elbow-minus,.x-tree-arrows .x-tree-elbow-end-plus,.x-tree-arrows .x-tree-elbow-end-minus
	{
	background-image:
		url('../../../resource/ext/resources/themes/images/gray/tree/arrows.gif')
		!important;
}

.x-node-expanded .x-tree-icon-parent {
	background-position: 0 0 !important;
	background: no-repeat;
	background-image: url('resources/images/folder-open.gif') !important;
}

.x-grid-tree-node-expanded .x-tree-elbow-plus,.x-grid-tree-node-expanded .x-tree-elbow-end-plus
	{
	background-image:
		url('../../resources/themes/images/neptune/tree/icons-gray.png');
	background-position: -20px 0;
}

.button1 {
	overflow: visible;
	display: inline-block;
	/* padding: 0.5em 1em; */
	border: 1px solid #d4d4d4;
	text-decoration: none;
	text-align: center;
	text-shadow: 1px 1px 0 #fff;
	font: 11px/normal sans-serif;
	color: #333;
	white-space: nowrap;
	cursor: pointer;
	outline: none;
	height: 24px !important;
	background: url(../../../resource/images/bg_back.png);
	font-weight: 400 !important;
	background-clip: padding-box;
	border-radius: 0.2em;
	zoom: 1;
}

.button1.pill {
    border-radius: 50em;
}

.x-tree-parent {
	background-color: rgb(250, 250, 250) !important;
	/* font-size: 15; */
	/* margin-top: 5px; */
	/* 	height: 23 !important */
}
.x-grid-cell-special > .x-grid-cell-inner{
	height:26px
}
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript"
	src="<%=basePath%>resource/ux/layout/component/form/ItemSelector.js"></script>
<script type="text/javascript"
	src="<%=basePath%>resource/ux/layout/component/form/MultiSelect.js"></script>
<script type="text/javascript"
	src="<%=basePath%>resource/ux/form/MultiSelect.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/grid/GridHeaderFilters.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/data/PagingMemoryProxy.js"></script>
<script type="text/javascript">
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'oa.info.PagingSent'
    ],
    launch: function() {
    	Ext.create('erp.view.oa.info.Viewport');//创建视图
    }
});
var page = 1;
var height = window.innerHeight;
if(Ext.isIE){
	height = screen.height*0.73;
}
var pageSize = parseInt(height*0.7/25);
var dataCount = 0;
var url = '';
var msg = '';
</script>
</head>
<body>
</body>
</html>