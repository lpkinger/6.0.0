<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html style="height:100%">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<script type="text/javascript">
document.onmousedown = function(){return false;}
</script>
</head> 
<body style="height:100% ; width: 100%; overflow: hidden; margin: 0">
	<img width="80%" height="80%"
		src="<%=basePath%>doc/readPng.action?folderId=<%=request.getParameter("folderId")%> &path=<%=request.getParameter("path")%> &type=<%=request.getParameter("type") %>" >
</body> 
</html>