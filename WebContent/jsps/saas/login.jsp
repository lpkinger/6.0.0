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
	content="saas.ubtob.com,erp,scm,crm,mrp,企业管理,优软,优软科技,优企云服,优软ERP,SAAS,供应链" />
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
				<a class="navbar-brand title" href="http://www.ubtob.com"
					target="_blank"> <img
					src="<%=basePath%>resource/images/uas_logo.png" height="20px"
					alt="" />
				</a> <b></b> <span>优企云服</span>
			</div>
			<ul class="nav navbar-nav navbar-right">
				<li><a href="http://www.ubtob.com" target="_blank">优软云首页</a></li>
			</ul>
		</div>
	</nav>
	<!-- header End -->
	<!-- main Start -->
	<div id="banner">
		<div class="container">
			<div id="en-info">
				<h1></h1>
			</div>
			<div id="guest-wrap">
				<button class="btn btn-primary btn-block" onclick="login();"
					target="_blank">体验登录</button>
				<a class="btn btn-success btn-block"
					href="http://www.ubtob.com/signup" target="_blank">快速注册</a>
			</div>
			<div id="login-wrap" class="slidein">
				<h3 class="title">登录</h3>
				<form name="myform" id="default_form">
					<div class="form-group">
						<div class="has-feedback-left">
							<span class="glyphicon glyphicon-user form-control-feedback-left"></span>
							<input type="text" id="s_username" class="form-control"
								placeholder="员工号/手机号" name="username" 
								onkeydown="keyDown(event)"  required>
						</div>
					</div>
					<div class="form-group">
						<div class="has-feedback-left">
							<span class="glyphicon glyphicon-lock form-control-feedback-left"></span>
							<input type="password" id="s_password" class="form-control"
								placeholder="密码" name="password" value="" 
								onkeydown="keyDown(event)" required>
						</div>
					</div>
					<div class="form-group">
						<div class="checkbox">
							<label> <input type="checkbox" id="remember"
								checked="checked"> 记住密码
							</label>
							<label style="float:right;"><a id="resetPwdLink" class="btn btn-link pull-right" style="padding-top: 0;">忘记密码</a></label>
						</div>
					</div>
					<div class="form-group">
						<div>
							<button class="btn btn-primary btn-submit" type="button" style="width: 315px"
								onclick="login();">登录</button>
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
	<!-- main End -->
	<!-- footer Start -->
	<div class="navbar navbar-static-bottom" id="footer">
		<!-- footer inner -->
		<div class="container">
		<div class="row">
			<div class="col-xs-2 text-left" style="margin-top:10px; margin-bottom: 10px">
			</div>
			<div class="col-xs-3 text-center" style="margin-top:10px; margin-bottom: 10px">
				<div class="qrcode-img pull-left"><!-- style="width:280px;" -->
					<img src="<%=basePath %>resource/sources/image/UU.png" width="100px" height="100px"></img>
				</div>
				<div class="qrcode-text pull-left">
				<div>手机UU</div>
				<div>快人一步</div>
				</div>	
			</div>		
			<div class="col-xs-3 text-left" style="margin-top:10px; margin-bottom: 10px">
				<div class="qrcode-img pull-left"><!-- style="width:280px;" -->
					<img src="<%=basePath %>resource/sources/image/UUoficial.png"></img>
				</div>
				<div class="qrcode-text pull-left">
				<div>微信扫描</div>
				<div>关注公众号</div>
				</div>	
			</div>			
			<div class="col-xs-3 text-left" style="margin-top:10px; margin-bottom: 10px">
				<div class="qrcode-img pull-left"><!-- style="width:280px;" -->
					<img src="<%=basePath %>resource/sources/image/UUcode.png"></img>
				</div>
				<div class="qrcode-text pull-left">
				<div>优软商城</div>
				<div>关注公众号</div>
				</div>	
			</div>
		</div>
		
		<div class="row">
			<div class="link-group">
				<ul class="list-inline text-center">
					<li><a href="http://www.usoftchina.com/about" target="_blank">关于优软</a></li>
					<li><a href="http://www.usoftchina.com/contact" target="_blank">联系我们</a></li>
					<li><a href="http://www.usoftchina.com" target="_blank">在线客服</a></li>
					<li>&copy;&nbsp;2015&nbsp;深圳市优软科技有限公司&nbsp;版权所有&nbsp;粤ICP备15112126号-1</li>
				</ul>
			</div>
		</div>
		</div>
	</div>
	<!-- footer End -->
	<!-- loading -->
	<div class="loading-container" id="loading" style="display: block;">
		请稍等...</div>
	<div class="loading-back"></div>
	<div id="pwdDialog" style="display: none">
		<div class="modal fade" style="display: block">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-body">
						<p>您的账号还未设置密码，是否先去设置？</p>
					</div>
					<div class="modal-footer">
						<a class="btn btn-primary">设置密码</a> <a class="btn btn-default">取消</a>
					</div>
				</div>
			</div>
		</div>
		<div class="modal-backdrop fade" style="display: block"></div>
	</div>
</body>
<script type="text/javascript"
	src="<%=basePath%>resource/jquery/jquery-1.4.min.js"></script>
<script type="text/javascript"
	src="<%=basePath%>resource/jquery/jquery.json-2.2.min.js"></script>
<script type="text/javascript"
	src="<%=basePath%>resource/jquery/showtip.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/other/saas.js"></script>
</html>