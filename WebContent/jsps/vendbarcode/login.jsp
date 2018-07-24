<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
Object employee = session.getAttribute("employee");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="icon" href="<%=basePath %>resource/images/icon_title.png" type="image/x-icon">
<link rel="stylesheet" href="<%=basePath %>resource/bootstrap/bootstrap.min.css" />
<link rel="stylesheet" href="<%=basePath %>resource/fontawesome/css/font-awesome.min.css" />
<link rel="stylesheet" href="<%=basePath %>resource/sources/toaster.css" />
<link rel="stylesheet" href="<%=basePath %>resource/sources/signin.css" />
<script type="text/javascript" src="<%=basePath %>resource/sources/jquery-3.0.0.min.js"></script>
<%-- <script type="text/javascript" src="<%=basePath %>resource/jquery/jquery-1.4.min.js"></script> --%>
<script type="text/javascript" src="<%=basePath %>resource/sources/bootstrap.min.js"></script>
<base target="_blank"/>
<meta name="keywords" content="ERP,企业供应链,办公自动化,人力资源管理,财务会计管理,工作流程管理 ">
<script type="text/javascript" src="<%=basePath %>resource/jquery/jquery.json-2.2.min.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/jquery/showtipTest.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/other/vendLogin.js"></script>

</head>
	<body onload='getMa()'>
		<div class="header">
		<div class="Jwrap">
			<div class="container">
				<div class="navbar-header">	
					<span class="header-logo">供应商条码打印系统</span>
				</div>
			</div>
			<!-- 
			<div class="navbar-right">
			<ul class="nav nav-pills" role="tablist">
  			<li role="presentation"><a href="#">手机端</a></li>
  			<li role="presentation"><a href="#">关于优软</a></li>
  			<li role="presentation"><a href="#">客户服务</a></li>
  			<li role="presentation"><a href="#">帮助</a></li>
			</ul>
			</div> -->
			<div class="clear"></div>
		</div>
		</div>
	<!-- </nav> -->
	<!-- header End -->
	<!-- main Start -->
	<div id="banner">
		<img src="<%=basePath %>/resource/sources/image/banner/532.jpg" style="width:100%;min-height: 360px;"></img>
		<!-- 内容 -->
		<div class="banner-content">
			<div class="container">
				<div id="en-infovertions">
					<span class="system-vertions">供应商条码打印系统</span>
				</div>
				<div class="pull-left">
					<div id="en-info">
							<span class="compny-name">${defaultenterprise_name}</span>
					</div>
					<!-- <div id="en-infovertions">
							<span class="system-vertions">优软管理系统v1.1版</span>
					</div> -->
				</div>
				
				<div id="login-wrap" class="pull-right">
				
					<p class="login_til">登&nbsp;录</p>
					<!-- 扫码打开和切换按钮 -->
					<!-- <a id="qrcode-btn" href="javascript:void(0)"></a> -->
					
					<!-- 普通登录 -->
					<div class="prmiary-login">
						<form name="myform" novalidate>
						<div class="left tgldown">
								<div class="has-feedback-left float_left">
								<span class="fa fa-tags form-control-feedback-left2"></span>
		  						<button class="btn btn-default dropdown-toggle float_left" type="button" id="dropdownMenu1" data-toggle="dropdown"><span class="myform-master" id="master" value="">${defaultmaster_fun}</span>
		    					<span class="caret"></span>
		  						</button>
		  						<ul id='box' class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu1">
		    					<!-- <li role="presentation"><a role="menuitem" tabindex="-1" href="#" align="center">UAS系统</a></li>
		    					<li role="presentation"><a role="menuitem" tabindex="-1" href="#" align="center">UAS测试</a></li>
		    					<li role="presentation"><a role="menuitem" tabindex="-1" href="#" align="center">集团中心</a></li>
		    					<li role="presentation"><a role="menuitem" tabindex="-1" href="#" align="center">测试都是</a></li>
		    					<li role="presentation"><a role="menuitem" tabindex="-1" href="#" align="center">深圳华商龙发的方法</a></li> -->
		    					<!--<c:forEach items="${masters}" var="master">
								<li role="presentation"><a  role="menuitem" tabindex="-1" align=left value="${master.ma_name}" style="cursor: pointer;margin:0px 32px" onclick="changeMaster('${master.ma_function}','${master.ma_name}');">${master.ma_function}</a></li>
								</c:forEach>-->
		  						</ul>
		  						</div>	
						</div>
						<!-- <lable class="form_til">用户名</lable> -->
						<div class="form-group" style="height: 34px;">
							<div class="has-feedback-left float_left">
							<span class="fa fa-user form-control-feedback-left"><!-- <i class="fa fa-camera-retro"></i> --><%-- <img src="<%=basePath %>resource/sources/image/log/usernew.png" width="18px" height="16px"> --%></span>
							<input type="hidden" name="master1" id="master1" value="${defaultmaster_name}">
							<input type="text" class="form-control-username" placeholder="账号" name="username" id="username" onkeydown="keyDown(event)">
							</div>
						</div>
						<!-- <lable class="form_til">密码</lable> -->
						<div class="form-group">
							<div class="has-feedback-left">
								<span class="fa fa-lock form-control-feedback-left3"><%-- <img src="<%=basePath %>resource/sources/image/log/passnew.png" width="20px" height="18px"> --%></span>
								<input type="password" class="form-control" placeholder="请输入密码" name="password" id="password" onkeydown="keyDown(event)">
							</div>
							<!-- <span class="form-forget-password"><a href="#" class="pull-right">忘记密码？</a></span> -->
							<!-- 大小写开启提示 -->
							<div class="caps" id="capslocktpl">
								<p>大写锁定已打开</p>
							</div>
						</div>
						
						
						<!-- <lable class="form_til">验证码</lable> -->
						<div class="form-group" id="checkcode" name="checkcode" style="display:none;">
							<div>
							<div class="has-feedback-left">
								<span class="fa fa-cc form-control-feedback-left1"></span> 
								<input type="text" class="form-control-checkcode" placeholder="验证码" id="validcode" name="validcode" size="20" onkeydown="keyDown(event)" required>
			  				</div>
							<div class="checkcodeimg"><span><img id="validimg" src="<%=basePath %>jsps/common/vcode.jsp" onclick="document.getElementById('validimg')
	                									.src='<%=basePath %>jsps/common/vcode.jsp?'+Math.random();" style="cursor: pointer;margin-top: 2px;" width="122px" height="32px"></span></div>
							<span class="form-forget-checkcode"><a class="pull-right" style="cursor: pointer;" onclick="document.getElementById('validimg').src='<%=basePath %>jsps/common/vcode.jsp?'+Math.random();">看不清，换一张</a></span>
							</div>
						</div>	
							<div class="form-group">
							<div class="checkbox rmbcount">
								<label> <input type="checkbox" name="RmbUser" id="RmbUser" checked="true" class="check" hidefocus="true" onclick="checkboxOnclick(this)">记住密码
								</label>
								<label style="float:right;"><a >忘记密码</a></label>
							</div>
							</div>
						<div class="form-group">
							<div>
								<button class="btn btn-inverse" type="button" id="loginBtn" onclick="login();">登录<%-- <img src="<%=basePath %>resource/sources/image/log/loginwait.gif"></img> --%></button>
							</div>
						</div>
					</form>
					</div>
					
					
					<!-- 扫码登录 -->
					<div class="qrcode-login">
						<img class="qrcode-img" src="">
						<a id="qrcode_refresh" href="javascript:void(0)"></a>
						<p class="qrcode-tip"><s class="saoyisao"></s>请使用<a class="qrcode-uu" href="http://static.ubtob.com/tpl/start/index.html">UU互联</a>APP扫描登录</p>	
					</div>
					
				</div>
				<!-- 尾部 -->
				<!-- <div class="clearfix"></div> -->
				
			</div>
		</div>		
		
	</div>
	<div style="margin-top: 25px; margin-bottom: 30px;">
		<div class="container">
			<dl class="notics-box">
				<dt><i class="icon-login"></i>系统公告</dt>
				<dd class="systemnotice1">
					<!-- <i>●</i> -->
					<marquee scrollamount=3 FONT style="WIDTH: 100%;" onmouseover=this.stop(); onmouseout=this.start();><!-- <i>●</i> --><a class="notices_title" href ="javascript:volid(0);">暂无<img src="<%=basePath %>resource/sources/image/log/new2.png"></img></a></marquee>
					<%-- <a class="notices_title" href ="javascript:volid(0);">UAS 1.1版本 增加了企业数据订阅功能，企业用户可以结合自身的岗位需求订阅关注的数据！<img src="<%=basePath %>resource/sources/image/log/new2.png"></img></a> --%>
					<span class="lable_new"></span>
				</dd>	
				 <!--  <dd class="systemnotice2">
					<i>●</i>
					<a class="notices_title" href ="javascript:volid(0);">华商龙商务科技有限公司开展员工动员大会</a>
					<span class="lable_new"></span>
				</dd> -->
				<!-- <dd class="extra">
					<a>查看更多
						<i></i>
					</a>
				</dd> -->
			</dl>
			<div class="row">
				<%-- <div class="col-xs-2 text-right col-sm-offset-1" style="margin-top:10px;">
					<div class="qrcode-img pull-left"><!-- style="width:280px;" -->
						<img src="<%=basePath %>resource/sources/image/UU.png"></img>
						<!-- <p class="text-left">手机 UU<br>快人一步</p> -->
						
					</div>
					<div class="qrcode-text pull-left">
					<div>手机 UU</div>
					<div>快人一步</div>
					</div>	
				</div>			
				<div class="col-xs-3 text-right">
					<div class="qrcode-img"><!-- style="width:280px;" -->
						<img src="<%=basePath %>resource/sources/image/UU.png"></img>
						<p class="text-left">手机 UU<br>快人一步</p>
					</div>
				</div>
				<div class="col-xs-3 text-center"><!-- style="width:35%;" -->
					<div class="qrcode-img">
						<img src="<%=basePath %>resource/sources/image/UUoficial.png"></img>
						<p class="text-left">微信扫描<br>关注公众号</p>
					</div>
				</div>
				<div class="col-xs-3 text-left" style="margin-top: 18px;"><!-- width: 320px; -->
					<p><a class="text-muted" href="#">关于优软</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a class="text-muted" href="#">联系我们</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a class="text-muted" href="#">帮助</a></p>
					<p class="text-muted">© 2016 深圳优软科技有限公司<br>粤 ICP 备 15112126 号-2</p>
				</div> --%>
				<div class="col-xs-3 text-center" style="margin-top:10px;">
					<div class="qrcode-img pull-left"><!-- style="width:280px;" -->
						<img src="<%=basePath %>resource/sources/image/UU.png"></img>
						<!-- <p class="text-left">手机 UU<br>快人一步</p> -->
						
					</div>
					<div class="qrcode-text pull-left">
					<div>手机 UU</div>
					<div>快人一步</div>
					</div>	
				</div>		
				<div class="col-xs-3 text-right" style="margin-top:10px;">
					<div class="qrcode-img pull-left"><!-- style="width:280px;" -->
						<img src="<%=basePath %>resource/sources/image/UUoficial.png"></img>
						<!-- <p class="text-left">手机 UU<br>快人一步</p> -->
						
					</div>
					<div class="qrcode-text pull-left">
					<div>微信扫描</div>
					<div>关注公众号</div>
					</div>	
				</div>			
				<div class="col-xs-3 text-right" style="margin-top:10px;">
					<div class="qrcode-img pull-left"><!-- style="width:280px;" -->
						<img src="<%=basePath %>resource/sources/image/UUcode.png"></img>
						<!-- <p class="text-left">手机 UU<br>快人一步</p> -->
						
					</div>
					<div class="qrcode-text pull-left">
					<div>优软商城</div>
					<div>关注公众号</div>
					</div>	
				</div>			
				<div class="col-xs-3 text-left" style="margin-top: 18px;"><!-- width: 320px; -->
					<p><a class="text-muted" href="http://www.usoftchina.com/">关于优软</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a class="text-muted" href="http://www.usoftchina.com/">联系我们</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a class="text-muted" href="http://www.usoftchina.com/">帮助</a></p>
					<p class="text-muted">© 2016 深圳优软科技有限公司<br>粤 ICP 备 15112126 号-2</p>
				</div>
			</div>
		</div>
			
	</div>
</body>
</html>