<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>    
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>

<html lang="zh-CN">
<head>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta name="baidu-site-verification" content="S0kf5fz0uA" />
<meta charset="utf-8">
<title>登录界面</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="icon" href="http://saas-static.ubtob.com//resource/images/icon_title.png?_v=22062"
	type="image/x-icon">
<link rel="stylesheet"
	href="http://saas-static.ubtob.com//resource/bootstrap/bootstrap.min.css?_v=22062" />
<link rel="stylesheet" href="http://saas-static.ubtob.com//resource/css/saas.css?_v=22062" />
<style type="text/css">
	.form-group{
		margin-top: 30px;
	}
	#login-wrap{
		width: 386px;
	}
</style>
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
					src="http://saas-static.ubtob.com//resource/images/uas_logo.png?_v=22062" height="20px"
					alt="" />
				</a> <b></b> <span>客户服务</span>
			</div>
		</div>
	</nav>
	<!-- header End -->
	<!-- main Start -->
	<div id="banner">
		<div class="container" >
			<div id="en-info">
				<h1>${enterpriseName}</h1>
			</div>
			<div id="login-wrap" class="slidein">
				<h3 class="title">登录</h3>
				<h5 style="color: red">${error}</h5>
				<form name="myform" id="default_form" action= "<%=basePath %>common/VisitERP/customer.action" method="post">
					<div class="form-group">
						<div class="has-feedback-left">
							<span class="glyphicon glyphicon-user form-control-feedback-left"></span>
							<input type="text" id="s_username" class="form-control"
								placeholder="账号" name="username" 
								onkeydown="keyDown(event)"  required 
								oninvalid="setCustomValidity('请输入您的账号');" oninput="setCustomValidity('');" />
						</div>
					</div>
					<div class="form-group">
						<div class="has-feedback-left">
							<span class="glyphicon glyphicon-lock form-control-feedback-left"></span>
							<input type="password" id="s_password" class="form-control"
								placeholder="密码" name="password" value="" 
								onkeydown="keyDown(event)" required
								oninvalid="setCustomValidity('请输入您的密码');" oninput="setCustomValidity('');" />
						</div>
					</div>
					<input type="hidden" name="master" value="${param.master}" />
					<c:if test="${param.accesskey == null}">
						<input type="hidden" name="accesskey" value="${params.accesskey}" />
					</c:if>
					<c:if test="${param.accesskey != null}">
						<input type="hidden" name="accesskey" value="${param.accesskey}" />
					</c:if>
					<input type="hidden" name="cu_uu" value="${param.cu_uu}" />
					<input type="hidden" name="success" value="true" />
					<div class="form-group">
						<div>
							<input type="submit" value="登陆" class="btn btn-primary btn-submit" type="button" style="width: 315px"></input>
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
			<div class="link-group">
				<ul class="list-inline text-center">
					<li><a href="http://www.usoftchina.com" target="_blank">关于优软</a></li>
					<li><a href="http://www.ubtob.com/contact" target="_blank">联系我们</a></li>
					<li><a href="http://www.ubtob.com" target="_blank">在线客服</a></li>
					<li><a href="http://www.ubtob.com" target="_blank">客服中心</a></li>
					<li><a href="http://www.ubtob.com" target="_blank">服务条款 </a></li>
					<li><a href="http://www.ubtob.com" target="_blank">投诉反馈 </a></li>
					<li>&copy;&nbsp;2015&nbsp;深圳市优软科技有限公司&nbsp;版权所有&nbsp;粤ICP备15112126号-1</li>
				</ul>
			</div>
		</div>
	</div>
	<!-- footer End -->
	<!-- loading -->
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
	src="http://saas-static.ubtob.com//resource/jquery/jquery-1.4.min.js?_v=22062"></script>
<script type="text/javascript"
	src="http://saas-static.ubtob.com//resource/jquery/jquery.json-2.2.min.js?_v=22062"></script>
<script type="text/javascript"
	src="http://saas-static.ubtob.com//resource/jquery/showtip.js?_v=22062"></script>
<!-- <script type="text/javascript" src="http://saas-static.ubtob.com//resource/other/saas.js?_v=22062"></script> -->
</html>