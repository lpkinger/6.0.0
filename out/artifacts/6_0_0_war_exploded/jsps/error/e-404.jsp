<%@ page contentType="text/html; charset=UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head><meta http-equiv="Content-Type" content="text/html; charset=utf-8"><title>404!</title></head>
	<script type="text/javascript">
	function setPage(){
		document.getElementById('page').innerHTML = document.URL;
	}
	function refresh(){
		window.location.href = document.URL;
	}
	</script>
	<body onload="setPage();">
		<img src="<%=basePath %>/resource/images/error.png"></img>
		<H2>404错误!</H2>
		<hr />
		<P>错误描述：</P>
		您访问的页面:<font id="page" color=blue></font>不存在!<br />
		请与系统管理员联系!
		<P>错误信息：</P>
		您访问的页面不存在!<br/>
		<input value="尝试刷新" title="刷新" style="width:80;height:30;" onclick="refresh();" type="button"></input>
	</body>
</html>