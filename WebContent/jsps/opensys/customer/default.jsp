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
<title>客户服务</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<link rel="icon" href="<%=basePath%>resource/images/icon_title.png"
	type="image/x-icon" />
<link rel="stylesheet"
	href="<%=basePath%>resource/ext/4.2/resources/ext-theme-gray/ext-theme-gray-all.css"
	type="text/css"></link>
<style type="text/css">
.x-accordion-item .x-accordion-hd {
	border-top-color: white;
	padding: 8px 10px;
}

.x-accordion-hd .x-panel-header-text-container {
	font-weight: bold;
	font-family: helvetica, arial, verdana, sans-serif;
	text-transform: none;
}

.x-toolbar-sencha {
	background: #e0e0e0;
	color: #304c33;
	border: none !important;
}

.x-toolbar-sencha .x-logo {
	padding: 10px 10px 10px 31px;
	/*  background: url(../images/logo.png) no-repeat 10px 11px; */
	color: #fff;
	text-align: center;
	font-size: 22px;
	font-weight: bold;
	text-shadow: 0 1px 0 #4e691f;
}

.header-btn {
	-webkit-border-radius: 3px;
	-moz-border-radius: 3px;
	-ms-border-radius: 3px;
	-o-border-radius: 3px;
	border-radius: 27px;
	padding: 3px 3px 3px 3px;
	border-width: 1px;
	border-style: solid;
	border-color: #bbb;
	background-image: none;
	background-color: #f8f8f8;
	background-image: -webkit-gradient(linear, 50% 0, 50% 100%, color-stop(0%, #fff),
		color-stop(50%, #E5E5E5), color-stop(51%, #fff),
		color-stop(100%, #E5E5E5));
	background-image: -webkit-linear-gradient(top, #fff, #f5f5f5 50%, #E5E5E5 51%, #f5f5f5);
}

.ux-shortcut {
	cursor: pointer;
	text-align: center;
	padding: 4px;
	margin: 4px;
	width: 90px;
	float: left;
	
}

.ux-shortcut-icon {
	width: 48px;
	height: 48px;
	padding-left:20px;
	background-color: transparent;
	background-repeat: no-repeat;
	
}

.ux-shortcut-text {
	/* font: normal 10px tahoma, arial, verdana, sans-serif; */
	text-decoration: none;
	font-size: 12px;
	height: 23px;
	/* padding-top: 5px; */
	color: #000;
}

.bottom-left {
	float: left;
	cursor: pointer;
	padding-bottom: 6px;
	border:none;
	margin-left: 3px;
	text-decoration: none;
	font-weight: lighter;
}

.main-btn-left {
	border: none;
	float: left;
	font-size: 13px;
}

.main-btn-right {
	border: none;
	float: right;
	font-size: 13px;
}

.main-btn-left a:hover,.main-btn-right font:hover {
	text-decoration: underline;
}

.main-btn-left :hover {
	background-color: #E8E8E8 !important;
}

.main-btn-right:hover {
	background: #E7E7E7 !important;
}

.main-btn-user {
	background-image: url('<%=basePath%>resource/images/wishmaster.gif');
}

.main-btn-link {
	background-image: url('<%=basePath%>resource/images/ie.png');
}
</style>

<script type="text/javascript"
	src="<%=basePath%>resource/ext/4.2/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/ext/IFrame.js"></script>
<script type="text/javascript">
	var msgCt;
	var showResult = function(title, s) {
		if (!msgCt) {
			msgCt = Ext.DomHelper.insertFirst(document.body, {
				id : 'msg-div'
			}, true);
		}
		var m = Ext.DomHelper.append(msgCt, createBox(title, s), true);
		m.hide();
		m.slideIn('t').ghost("t", {
			delay : 1000,
			remove : true
		});
	};
	function createBox(t, s) {
		return '<div class="msg"><h3>' + t + '</h3><p>' + s + '</p></div>';
	}
	Ext.Loader.setConfig({
		enabled : true
	});//开启动态加载
	Ext.application({
		name : 'erp',//为应用程序起一个名字,相当于命名空间
		appFolder : basePath + 'app',//app文件夹所在路径
		controllers : [//声明所用到的控制层
		'opensys.Default' ],
		launch : function() {
			Ext.create('erp.view.opensys.default.ViewPort');//创建视图
		}
	});
	var enUU  = '<%=session.getAttribute("enUU")%>';
	var em_code ='<%=session.getAttribute("em_code")%>';
	var em_name ='<%=session.getAttribute("em_name")%>'; 
	var cu_name ='<%=session.getAttribute("cu_name")%>'; 
	var em_id ='<%=session.getAttribute("em_id")%>'; 
	var emUU  = '<%=session.getAttribute("em_uu")%>';
	var cu_code  = '<%=session.getAttribute("cu_code")%>';
	var role  = '<%=session.getAttribute("role")%>';
	var cu_uu  = '<%=session.getAttribute("cu_uu")%>';
</script>
</head>
<body>
</body>
</html>