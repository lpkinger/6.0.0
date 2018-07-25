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
<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/ux/css/CheckHeader.css" />
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/CheckColumn.js"></script>
<style>
  .x-panel-body-default {
        border-style: none;  
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
        'ma.bench.SceneSet'
    ],
    launch: function() {
        Ext.create('erp.view.ma.bench.SceneSet');
    }
});
var bench = getUrlParam('bench');
var bscode = '';
var caller = 'SceneSet';
var formCondition = getUrlParam('formCondition');
if(formCondition){
	bscode = formCondition.substring(formCondition.indexOf('IS')+3,formCondition.length-1);
}
var gridCondition =  getUrlParam('gridCondition');
<%
Object is_saas = session.getAttribute("isSaas");
boolean isSaas = is_saas != null && Boolean.valueOf(is_saas.toString());
%>
var isSaas = <%=isSaas%>;
</script>
</head>
<body>
</body>
</html>