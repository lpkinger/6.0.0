<%@ page contentType="text/html; charset=UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head><meta http-equiv="Content-Type" content="text/html; charset=utf-8"><title>500!</title></head>
<body>
<img src="<%=basePath %>/resource/images/error.png"></img>
<H2>500error!</H2>
<hr />
<P>错误描述：</P>
该页无法显示!请不要非正常访问<br />
请与系统管理员联系!
<P>错误信息：</P>
该页无法显示!
</body>
</html>