<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>    
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=0">
<title>扫码登录</title>
<link rel="stylesheet" href="<%=basePath %>resource/bootstrap/bootstrap.min.css" />
<script type="text/javascript" src="<%=basePath %>resource/sources/jquery-3.0.0.min.js"></script>
<style type="text/css">
body{
	background-size: cover;
	background-image: url(<%=basePath %>resource/sources/image/loginMobileConfirm.png);
	background-repeat: no-repeat;
}
.group{
	width:100%;
	position:fixed;
    bottom:0;
}
.confirm{
	display: block;
	margin: 0 auto;
	width: 90%;
	background-color: #2F95DD;
	color: white;
}
.cancel{
	display: block;
	margin: 0 auto;
	width: 90%;
	background-color: #eee;
	color: #666666;
	margin-top: 20px;
	margin-bottom: 20px;
}
</style>
<script type="text/javascript">

	function loadURL(url) {
	    var iFrame;
	    iFrame = document.createElement("iframe");
	    iFrame.setAttribute("src", url);
	    iFrame.setAttribute("style", "display:none;");
	    iFrame.setAttribute("height", "0px");
	    iFrame.setAttribute("width", "0px");
	    iFrame.setAttribute("frameborder", "0");
	    document.body.appendChild(iFrame);
	    // 发起请求后这个 iFrame 就没用了，所以把它从 dom 上移除掉
	    iFrame.parentNode.removeChild(iFrame);
	    iFrame = null;
	}

	function closeWindow(){
		if (/(iPhone|iPad|iPod|iOS)/i.test(navigator.userAgent)) {
			//调用苹果
			loadURL("firstClick://shareClick?title=close");
	    } else if (/(Android)/i.test(navigator.userAgent)) {
	    	//调用安卓方法
			window.JSWebView.closeWebWindow();
	    };
		
		
		
	}

	$(function(){
		$(".confirm").click(function(){
			$.ajax({
				url: "<%=basePath %>common/checkQrcodeConfirm.action",
				type:"post",
				data:{
					"clientId":$("#clientId").val(),
					"em_code":$("#em_code").val(),
					"sob":$("#sob").val(),
					"time":$("#time").val(),
					"key":$("#key").val()
					
				},
				dataType:"json",
				success:function(result){
					//返回登录结果成功
					if (result.success) {
						//关闭页面
						closeWindow();
					}
				}
			});
		});
		$(".cancel").click(function(){
			closeWindow();
		});
	});
</script>
</head>
<body>
<div class="group">

	<form id="form" action="<%=basePath %>common/checkQrcodeConfirm.action" method="post">
		<input id="clientId" type="hidden" value="${clientId }">
		<input id="em_code" type="hidden" value="${em_code }">
		<input id="sob" type="hidden" value="${sob }">
		<input id="time" type="hidden" value="${time }">
		<input id="key" type="hidden" value="${key }">
	</form>

	<button class="confirm btn btn-default">确认登录</button>
	<button class="cancel btn btn-default">取消</button>
</div>
</body>
</html>