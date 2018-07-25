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
		.mydiv{
			height: 100%;
			background: #f1f1f1;
		}
		.div-right{
			display: inline;
			float: right;
		}
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath %>jsps/oa/attention/AttentionManage.js"></script>
<script type="text/javascript">	
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
var emid=getUrlParam('attentedemid');
var condition = '';
var days=null;
var height = window.innerHeight;
      Ext.application({
        name: 'erp',//为应用程序起一个名字,相当于命名空间
        appFolder: basePath+'app',//app文件夹所在路径
       controllers: [//声明所用到的控制层
        'oa.attention.AttentionManageDetail'
       ],
       launch: function() {
    	Ext.create('erp.view.oa.attention.AttentionManageDetail');//创建视图
    	getColumn();
        }
      });
</script>
</head>
<body >
  <div id="myToDo" class="mydiv"></div>
  <div id="mydata" class="mydiv"></div>
  <div id="mytask" class="mydiv"></div>
  <div id="mydairy" class="mydiv"></div>
  <div id="myworkplan" class="mydiv"></div>
  <div id="mySynergy" class="mydiv"></div>
</body>
</html>