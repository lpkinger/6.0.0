<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%
	String url = request.getRequestURL().toString();
	String basePath = url.substring(0, url.length()
			- request.getRequestURI().length())
			+ request.getContextPath() + "/";
	String caller = request.getParameter("caller");
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<style type="text/css">
.search-item {
	font: normal 11px tahoma, arial, helvetica, sans-serif;
	padding: 3px 10px 3px 10px;
	border: 1px solid #fff;
	border-bottom: 1px solid #eeeeee;
	white-space: normal;
	color: #555;
}

.search-item h3 {
	display: block;
	font: inherit;
	font-weight: bold;
	color: #222;
}

.search-item h3 span {
	float: right;
	font-weight: normal;
	margin: 0 0 5px 5px;
	width: 150px;
	display: block;
	clear: none;
}

.msg .x-box-mc {
	font-size: 14px;
}
#msg-div {
	position: absolute;
	left: 50%;
	top: 10px;
	width: 400px;
	margin-left: -200px;
	z-index: 20000;
}

#msg-div .msg {
	border-radius: 8px;
	-moz-border-radius: 8px;
	background: #F6F6F6;
	border: 2px solid #ccc;
	margin-top: 2px;
	padding: 10px 15px;
	color: #555;
}

#msg-div .msg h3 {
	margin: 0 0 8px;
	font-weight: bold;
	font-size: 15px;
}

#msg-div .msg p {
	margin: 0;
}
</style>
<link rel="icon" href="<%=basePath%>resource/images/icon_title.png"
	type="image/x-icon" />
<link rel="stylesheet"
	href="<%=basePath%>resource/ext/4.2/resources/ext-theme-gray/ext-theme-gray-all.css"
	type="text/css"></link>
<link rel="stylesheet"
	href="<%=basePath%>resource/ext/4.2/resources/ext-theme-neptune/tree-neptune.css"
	type="text/css"></link>
<link rel="stylesheet" type="text/css"
	href="<%=basePath%>resource/ux/css/CheckHeader.css" />
<link rel="stylesheet" type="text/css"
	href="<%=basePath%>jsps/sys/css/GroupTabPanel.css" />
<link rel="stylesheet" type="text/css"
	href="<%=basePath%>jsps/sys/css/init.css" />
<style type="text/css">
.x-toolbar-sencha {
	background: #e0e0e0;
	color: #304c33;
	border: none !important;
}

.x-toolbar-sencha .x-logo {
	padding: 10px 10px 10px 31px;
	/*  background: url(../images/logo.png) no-repeat 10px 11px; */
	color: #fff;
	text-align:center;
	font-size: 22px;
	font-weight: bold;
	text-shadow: 0 1px 0 #4e691f;
}
</style>
<style>
.loading {
	background: url("<%=basePath %>resource/images/loading.gif") no-repeat center!important; 
}
.checked {
	background: url("<%=basePath %>resource/images/renderer/finishrecord.png") no-repeat center!important; 
}
.error {
	background: url("<%=basePath %>resource/images/renderer/important.png") no-repeat center!important; 
}
.refresh{
    background: url('<%=basePath %>resource/images/refresh.gif')  no-repeat;
}
</style>
<script type="text/javascript"
	src="<%=basePath%>resource/ext/4.2/ext-all.js"></script>
<%-- <script type="text/javascript"
	src="<%=basePath%>resource/ext/4.2/ext-all-debug.js"></script> --%>
<script type="text/javascript" src="<%=basePath%>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath%>jsps/sys/sysinit.js"></script>
<script type="text/javascript">	
var msgCt;
var showResult =function(title,s){
	  if(!msgCt){
        msgCt = Ext.DomHelper.insertFirst(document.body, {id:'msg-div'}, true);
    }
    var m = Ext.DomHelper.append(msgCt, createBox(title, s), true);
    m.hide();
    m.slideIn('t').ghost("t", { delay: 1000, remove: true});
};
function createBox(t, s){
    return '<div class="msg"><h3>' + t + '</h3><p>' + s + '</p></div>';
 }
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
      controllers: [//声明所用到的控制层
        'hr.employee.OrgArcSet'
    ],
    launch: function() {
    	Ext.create('erp.view.hr.employee.ViewPortNew');//创建视图
    }
});
var emid = <%=session.getAttribute("em_uu")%>;
/* var orcode='1001'; */ //enUU
/* var enUU=10041166;  */
var enUU=<%=session.getAttribute("enUU")%>;
var orcode = <%="'"+session.getAttribute("orcode")+"'"%>;
</script>
</head>
<body>
	<div id="legalese" style="display: none;">
		<h2>使用条款</h2>
	</div>
</body>
</html>