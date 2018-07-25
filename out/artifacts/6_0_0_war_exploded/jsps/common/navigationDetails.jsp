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
<link rel="stylesheet"
	href="<%=basePath%>resource/ext/4.2/resources/ext-theme-gray/ext-theme-gray-all.css"
	type="text/css"></link>
<%-- <link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css" type="text/css"></link> --%>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<style type="text/css">
	/* .x-html-editor-wrap textarea {
		background-color: #EEE8CD;
	} */
  	/* body{
		background: red;
	}  */ 
	/* .x-panel-body-default {
		background: #f9f9f9;
		border-color: #d0d0d0;
		color: black;
		border-width: 1px;
		border-style: solid
	} */
</style>
<%-- <script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script> --%>
<script type="text/javascript"	src="<%=basePath%>resource/ext/4.2/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'common.NavigationDetails'
    ],
    launch: function() {
    	Ext.create('erp.view.common.NavigationDetails');//创建视图
    }
});
var username = '<%=session.getAttribute("username")%>';
var em_name = '<%=session.getAttribute("em_name")%>';
var em_uu = '<%=session.getAttribute("em_uu")%>';
var en_uu = '<%=session.getAttribute("en_uu")%>';
var em_code = '<%=session.getAttribute("em_code")%>';
var en_email ='<%=session.getAttribute("en_email")%>';
var em_id ='<%=session.getAttribute("em_id")%>';
var em_type = '<%=session.getAttribute("em_type")%>';
var id = getUrlParam('id');
</script>
</head>
<body >
</body>
</html>