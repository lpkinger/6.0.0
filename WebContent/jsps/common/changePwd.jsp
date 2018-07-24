<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
Object employee = session.getAttribute("employee");
if(employee != null)
	response.sendRedirect(basePath);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>UAS管理系统-修改密码</title>
<link rel="icon" href="<%=basePath %>resource/images/icon_title.png" type="image/x-icon">
<link rel="stylesheet" href="<%=basePath %>resource/bootstrap/bootstrap.min.css" />
<link rel="stylesheet" href="<%=basePath %>resource/fontawesome/css/font-awesome.min.css" />
<link rel="stylesheet" href="<%=basePath %>resource/sources/toaster.css" />
<link rel="stylesheet" href="<%=basePath %>resource/sources/signin.css" />
<style type="text/css">
	.labeltext{
		font-size: 16px;
    	padding-top: 3px !important;
	}
</style>
<script type="text/javascript" src="<%=basePath %>resource/sources/jquery-3.0.0.min.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/sources/bootstrap.min.js"></script>
<base target="_blank"/>
<meta name="keywords" content="ERP,企业供应链,办公自动化,人力资源管理,财务会计管理,工作流程管理 ">
<script type="text/javascript" src="<%=basePath %>resource/jquery/jquery.json-2.2.min.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/jquery/showtipTest.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/other/loginTest.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/other/qrcode.js"></script>
</head>
<body>
		<!-- head -->
		<div class="header">
		<div class="Jwrap">
			<div class="container">
				<div class="navbar-header">	
					<span class="header-logo">企业管理系统</span>
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
		<hr>
		<!-- content -->
		<div class="container" style="min-height: 55%" id="content">
			<c:if test="${result.success == true}">
				<input type="hidden" name="em_code" value="${result.em_code}" />
				<div style="margin-top: 10%">
					<h1 style="font-size: 24;margin-left: 13%;margin-bottom: 2%;">修改密码: </h1>
					<form class="form-horizontal" style="margin-left: 10%">
					  <div class="form-group" id="empassword_div">
					    <label for="inputCode" class="col-sm-2 control-label labeltext">新密码</label>
					    <div class="col-sm-4">
					      <input type="password" name="password" class="form-control" id="inputPassword" placeholder="新密码">
					    </div>
					  </div>
					  <div class="form-group" id="empassword2_div">
					    <label for="inputName" class="col-sm-2 control-label labeltext">确认密码</label>
					    <div class="col-sm-4">
					      <input type="password" name="password2" class="form-control" id="inputPassword" placeholder="确认密码">
					    </div>
					  </div>
					</form>
					<div style="margin-left: 35%">
					    <div class="col-sm-offset-2 col-sm-10">
					      <button style="width:70px" id="submit" class="btn btn-primary">确定</button>
					    </div>
					  </div>
				</div>
				<p id="message" style="color:red;margin-left:14%;"></p>
			</c:if>
			<c:if test="${result.success == false}">
				<div style="color:red;margin-left:15%;margin-top:10%"><h1 style="font-size: 28px;">${result.message}</h1></div>
			</c:if>
		</div>
		<!-- foot -->
		<div style="margin-top: 25px; margin-bottom: 30px;">
		<div class="container">
			<dl class="notics-box">
				<dt><i class="icon-login"></i>系统公告</dt>
				<dd class="systemnotice1">
					<!-- <i>●</i> -->
					<marquee scrollamount=3 FONT style="WIDTH: 100%;" onmouseover=this.stop(); onmouseout=this.start();><!-- <i>●</i> --><a class="notices_title" href ="javascript:volid(0);">UAS 1.1版本 增加了企业数据订阅功能，企业用户可以结合自身的岗位需求订阅关注的数据！<img src="<%=basePath %>resource/sources/image/log/new2.png"></img></a></marquee>
					<%-- <a class="notices_title" href ="javascript:volid(0);">UAS 1.1版本 增加了企业数据订阅功能，企业用户可以结合自身的岗位需求订阅关注的数据！<img src="<%=basePath %>resource/sources/image/log/new2.png"></img></a> --%>
					<span class="lable_new"></span>
				</dd>	
			</dl>
			<div class="row">
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
					<p><a class="text-muted" href="#">关于优软</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a class="text-muted" href="#">联系我们</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a class="text-muted" href="#">帮助</a></p>
					<p class="text-muted">© 2016 深圳优软科技有限公司<br>粤 ICP 备 15112126 号-2</p>
				</div>
			</div>
		</div>
			
	</div>
</body>
<script type="text/javascript">
	var basePath = '<%=basePath %>';
	function uploading(){
		var _PageHeight = $(window).height();
		var _PageWidth  = $(window).width();
		var _LoadingTop = _PageHeight > 61 ? (_PageHeight - 61) / 2 : 0;
		var _LoadingLeft = _PageWidth > 215 ? (_PageWidth - 215) / 2 : 0;
		var _LoadingHtml = '<div id="loadingDiv" style="position:absolute;left:0;width:100%;height:' + _PageHeight + 'px;top:0;background:#f3f8ff;opacity:0.8;filter:alpha(opacity=80);z-index:10000;"><div style="position: absolute; cursor1: wait; left: ' + _LoadingLeft + 'px; top:' + _LoadingTop + 'px; width: auto; height: 57px; line-height: 57px; padding-left: 50px; padding-right: 5px; background: #fff url(../resource/css/images/loading.png) no-repeat scroll 5px 10px; border: 2px solid #95B8E7; color: #696969; font-family:\'Microsoft YaHei\';">执行中，请稍等...</div></div>';    
		$("body").append(_LoadingHtml);
		}
	//加载状态为complete时移除loading效果
	function completeUpLoading() {
	 $('#loadingDiv').remove();
	}
	$(function(){
		$("input[name=password]").focus();
		$("input[name=password]").blur(function(e){
			if($("input[name=password]").val() == ''){
				$("#empassword_div").addClass('has-error');
			}else{
				$("#empassword_div").removeClass('has-error');
			}
		});
		$("input[name=password2]").blur(function(e){
			if($("input[name=password2]").val() == ''){
				$("#empassword2_div").addClass('has-error');
			}else{
				$("#empassword2_div").removeClass('has-error');
			}
		});
		$("#submit").click(function(){
			var em_password = $("input[name=password]").val(),
				em_password2 = $("input[name=password2]").val(),
				em_code = $("input[name=em_code]").val();
			if(em_password != em_password2){
				$("#message").text("两次密码不一致");
			}else{
				if(em_password.length < 6 || em_password.length > 20){
					$("#message").text("密码应为6-20位");
				}else{
					$("#message").text("");
					//提交
					$("#submit").attr("disabled","disabled");
					$.ajax({
						url: basePath + 'common/changePassword.action',
						data: {
							em_code: em_code,
							password: em_password
						},
						dataType: 'json',
						type: 'POST',
						beforeSend: function () {
					        uploading();
					    },
						success: function(response){
							if(response.success)
								alert('修改成功!');
							else
								alert(response.result);
						},
						complete: function () {
					          completeUpLoading();
					    }
					});
				}
			}
		});
	});
</script>
</html>