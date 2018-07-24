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
<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css"/>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<style type="text/css">

/* .x-grid-row .x-grid-cell {
	color: null;
	padding:0px;
	font: normal 11px tahoma, arial, verdana, sans-serif; 
	 border-color: #ededed;
	border-style: solid;
	border-width: 1px 0;
	border-top-color: #fafafa;
	vertical-align: middle;
	height: auto !important;  
}

.x-grid-cell-inner {
    height: auto;  
    line-height: 26px;  
    overflow: hidden;
    -o-text-overflow: ellipsis;
    text-overflow: ellipsis;
    padding: 0px; 
   	/*white-space:normal; 
    word-wrap: break-word; 
    word-break:break-all; 
    vertical-align: middle; 
    text-align:center;  
}  */

.tdcss{
	background-color: #e0e0e0!important;
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
        'fs.credit.AssessScheme'
    ],
    launch: function() {
        Ext.create('erp.view.fs.credit.AssessScheme');
    }
});
var caller='AssessScheme';
var formCondition = '';
var gridCondition = '';
</script>
</head>
<body >
</body>
</html>