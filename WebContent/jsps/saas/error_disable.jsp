<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE HTML>
<html lang="zh-CN">
<head>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta name="baidu-site-verification" content="S0kf5fz0uA" />
<meta charset="utf-8">
<title>优企云服</title>
<meta name="description" content="优企云服是建立在优软UAS产品上的云服务平台" />
<meta name="keywords"
	content="saas.ubtob.com,erp,scm,crm,mrp,企业管理,优软,优软科技,优软ERP,SAAS,供应链" />
<%
	String url = request.getRequestURL().toString();
	String basePath = url.substring(0, url.length()
			- request.getRequestURI().length())
			+ request.getContextPath() + "/";
%>
<link rel="icon" href="<%=basePath%>resource/images/icon_title.png"
	type="image/x-icon">
<link rel="stylesheet"
	href="<%=basePath%>resource/bootstrap/bootstrap.min.css" />
<link rel="stylesheet" href="<%=basePath%>resource/css/saas.css" />
</head>
<body>
	<!-- old brower Start -->
	<div id="brower-tip" style="display: none;">
		<div class="container">
			<div class="pull-left">
				<i class="glyphicon glyphicon-warning-sign text-danger"></i>浏览器更新提示：您的浏览器版本较低，可能会影响使用效果，建议升级浏览器到最新版本，或<a
					href="http://dlsw.baidu.com/sw-search-sp/soft/9d/14744/ChromeStandaloneSetup.1418195695.exe"
					target="_blank" class="btn btn-xs btn-info">下载Chrome高速浏览器</a>
			</div>
			<div class="pull-right">
				<a class="text-muted btn"
					onclick="document.getElementById('brower-tip').style.display='none';">我知道了<i
					class="glyphicon glyphicon-remove"></i></a>
			</div>
		</div>
		<script>
			var lessthenIE9 = function() {
				var agent = navigator.userAgent.toLowerCase(), mode = document.documentMode;
				return /msie 9/.test(agent) || /msie 8/.test(agent) || /msie 7/.test(agent)
						|| (mode && [ 7, 8, 9 ].indexOf(mode) > -1);
			};
			lessthenIE9() && (document.getElementById("brower-tip").style.display = 'block');
		</script>
	</div>
	<!-- old brower End -->
	<!-- header Start -->
	<nav id="top" class="navbar" style="margin-bottom: 0;">
		<div class="container">
			<div class="navbar-header">
				<a class="navbar-brand title" href="http://www.ubtob.com" target="_blank">
					<img src="<%=basePath%>resource/images/uas_logo.png" height="20px" alt="" />
				</a> <b></b> <span>优企云服</span>
			</div>
			<ul class="nav navbar-nav navbar-right">
				<li><a href="http://www.ubtob.com" target="_blank">优软云首页</a></li>
			</ul>
		</div>
	</nav>
	<!-- header End -->
	<!-- main Start -->
	<div class="container">
		<div id="search-wrap">
			<div class="page-header text-warning">
				<h1><span class="glyphicon glyphicon-warning-sign icon-left"></span>您的账套已被限制使用，可能存在以下情况</h1>
			</div>
			<ul class="text-muted">
				<li>企业管理员要求禁用</li>
				<li>合约已过期</li>
				<li>存在违反<a href>《优企云服使用协议》</a>相关条例的行为
				</li>
			</ul>
			<p>详情请咨询客服</p>
		</div>
	</div>
	<!-- main End -->
	<!-- footer Start -->
	<div class="navbar navbar-fixed-bottom" id="footer">
		<!-- footer inner -->
		<div class="container">
			<div class="link-group">
				<ul class="list-inline text-center">
					<li><a href="http://www.usoftchina.com" target="_blank">关于优软</a></li>
					<li><a href="http://www.usoftchina.com" target="_blank">联系我们</a></li>
					<li><a href="#" target="_blank">在线客服</a></li>
					<li><a href="serve" target="_blank">客服中心</a></li>
					<li><a href="http://www.usoftchina.com" target="_blank">联系邮箱</a></li>
					<li><a href="http://www.usoftchina.com" target="_blank">服务条款
					</a></li>
					<li><a href="#" target="_blank">投诉反馈 </a></li>
					<li>© 2014-2015 深圳市优软科技有限公司. 版权所有.</li>
				</ul>
			</div>
		</div>
	</div>
	<!-- footer End -->
</body>
<script type="text/javascript"
	src="<%=basePath%>resource/jquery/jquery-1.4.min.js"></script>
<script type="text/javascript"
	src="<%=basePath%>resource/jquery/jquery.json-2.2.min.js"></script>
<script type="text/javascript"
	src="<%=basePath%>resource/other/saas_domain.js"></script>
</html>