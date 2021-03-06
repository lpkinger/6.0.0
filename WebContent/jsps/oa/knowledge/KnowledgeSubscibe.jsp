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
#sidebar a:link { 
color:#1C86EE; 
text-decoration:none; 
} 
#sidebar a:visited { 
color:#1C86EE; 
text-decoration:none; 
} 
#sidebar a:hover { 
color:#CD2626; 
text-decoration:none; 
} 
#sidebar a:active { 
color:#1C86EE; 
text-decoration:none; 
} 
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/grid/GridHeaderFilters.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/data/PagingMemoryProxy.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/grid/Export.js"></script>
<script type="text/javascript" src="<%=basePath%>jsps/oa/knowledge/Knowledge.js"></script>
<script type="text/javascript">	
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'oa.knowledge.KnowledgeSubscibe'
    ],
    launch: function() {
    	Ext.create('erp.view.oa.knowledge.KnowledgeSubscibe');//创建视图
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
var caller = 'RecKnowledge';
var emid='<%=session.getAttribute("em_id")%>';
</script>
</head>
<body >
</body>
</html>