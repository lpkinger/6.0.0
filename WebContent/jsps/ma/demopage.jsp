<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html style="height:100%">
<head>
<title>单据样例</title>
<link rel="icon" href="<%=basePath %>resource/images/icon_title.png" type="image/x-icon"/>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body style="height:100% ; width: 100%; overflow: hidden; margin: 0">
<iframe src="${url}"   name="mainFrame" frameborder="0" marginheight="0" marginwidth="0"  width="100%" height="100%"></iframe>
</body>
</html>