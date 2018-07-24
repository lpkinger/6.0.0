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
<style type="text/css">
body{
	background-size: cover;
	background-image: url(<%=basePath %>resource/sources/image/loginMobileError.png);
	background-repeat: no-repeat;
}

</style>

</head>
<body>

</body>
</html>