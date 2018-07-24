<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String caller=request.getParameter("caller");
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet"
	href="<%=basePath%>resource/ext/4.2/resources/ext-theme-gray/ext-theme-gray-all.css"
	type="text/css"></link>
<link rel="stylesheet" href="<%=basePath%>resource/css/opensys.css"
	type="text/css"></link>
<style type="text/css">
.single-field  .x-field-label-cell {
	background: transparent;
	padding-left: 3px;
}

.progress {
	padding: -10px 0px 0px 20px;
	margin: 10px 0px 0px 20px;
	clear: both;
	color: #888;
	font-size: 13px;
}

.progress>div {
	height: 100%;
}

.progress>div>div {
	margin: 0px -4px;
	width: 100px;
	height: 100px;
	position: relative;
	background: none;
	border: 0;
}

.progress>div>div>span.lines {
	position: absolute;
	content: '';
	z-index: 1;
	width: 2px;
	height: 110%;
	background: #D1D1D1;
	/* background: #D1D1D1; */
	/* left: 0; */
	top: 10px;
	margin-top: 0;
	-webkit-border-radius: 2px;
	-moz-border-radius: 2px;
	border-radius: 2px;
	margin-left: 5px;
}

.progress>div>p.remark {
	position: absolute;
	width: 80%;
	word-wrap: break-word;
	width: 500px;
	margin-left: 50px;
}

.progress>div>div>span.circle {
	z-index: 2;
	position: relative;
	height: 5px;
	width: 5px;
	-webkit-border-radius: 50em;
	-moz-border-radius: 50em;
	border-radius: 50em;
	font-size: 12px;
	border: 3px solid #D1D1D1;
	/* background: white; */
	background: #D1D1D1;
	-webkit-box-sizing: content-box;
	-moz-box-sizing: content-box;
	box-sizing: content-box;
	padding: 0;
	display: block;
	/* margin: 0px auto; */
	cursor: pointer;
}

.progress>div>div>span.active {
	height: 10px;
	width: 10px;
	border-color: #EF9E9E;
	background-color: #E83030;
	border: 2px solid #EF9E9E;
	margin-left: -1px;
}

.progress>div>p.start {
	color: black;
}
</style>
<script type="text/javascript"
	src="<%=basePath%>resource/ext/4.2/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">	
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'opensys.FeedBack'
    ],
    launch: function() {
    	Ext.create('erp.view.opensys.feedback.ViewPort');//创建视图
    }
});
var caller = '<%=caller %>';
var enUU  = '<%=session.getAttribute("enUU")%>';
var formCondition = '';
var gridCondition = '';
</script>
</head>
<body>
</body>
</html>