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
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<style>

#container { 
	padding:auto; 
	margin:auto;
	text-align:center;
	height: 100%;
	display: table;/*让元素以表格形式渲染*/ 
}
 
#content {  
	display:table-cell;/*让元素以表格的单元素格形式渲染*/ 
	vertical-align: middle;/*使用元素的垂直对齐*/
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
        'fa.gla.RelatedParty'
    ],
    launch: function() {
        Ext.create('erp.view.fa.gla.RelatedParty');
    }
});

var caller = 'RelatedParty';

var height = window.innerHeight;
if(Ext.isIE){//ie不支持window.innerHeight;document.documentElement.clientHeight == 0
	height = screen.height*0.73;
}

</script>
</head>
<body >
</body>
</html>