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
	.x-hrorgTree {
		color: null;
	    font: normal 11px tahoma, arial, verdana, sans-serif;
	    background-color: #f1f1f1 !important;
	    border-color: #ededed;
	    border-style: solid;
	    border-width: 1px 0;
	    border-top-color: #fafafa;
	    height: 26px;
	    line-height: 26px;
	} 
	 .x-hrorgTree:hover {
	 	background-color:#FAFAFA !important;
	    font-size: 14px;
	    font-weight: bold;
	    color: green;
	}
	.x-tree-icon-leaf{
	    width: 16px;
    	background-image: url(../../../resource/css/images/leaf.png);
	}
	.x-grid-tree-node-expanded .x-tree-icon-parent{
		background: url(../../../resource/css/images/folder-open.png) no-repeat !important;
	}
	.x-tree-icon-parent{
	    width: 16px;
	    background-image: url(../../../resource/css/images/folder-close.png);
	}
	.span-focus{
		background-color: rgb(255,255,204) ;
	    cursor: pointer;
	    border-radius: 8px;
	    border-left: 1px solid gray;
	    border-right: 1px solid gray;
	}
	#autotip{
		position: absolute;
		z-index: 99;
		top: 25px;
		border: 1px solid gray;
		background-color: white;
		border-radius: 3px;
	}
	#autotip ul li{
	    color:black;
	    cursor:pointer;
	    font-size: 14px;
	    margin-top: 3px;
	}
	#autotip ul li:hover{
	    background-color: rgb(255,255,204);
	}
	.mySpan{
	 	color:red;
	 	float:left;
	 	font-family:microsoft yahei,sans-serif;
		font-size:14px;
		margin-right:5px;
 		width:100px;
	 	padding-left: 3px;
   	 	padding-top: 2px;
	}
	#choseButton{
		background: url(<%=basePath %>resource/images/upgrade/bluegray/icon/maindetail/search-trigger.png) no-repeat;
		width: 18px;
		border: none;
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
        'oa.batchMail.batchMail'
    ],
    launch: function() {
    	Ext.create('erp.view.oa.batchMail.batchMail');//创建视图
    }
});
var caller = getUrlParam('whoami');//caller决定了该页面的一切信息和逻辑
var formCondition = '';
var gridCondition = '';
</script>
</head>
<body>
</body>
</html>