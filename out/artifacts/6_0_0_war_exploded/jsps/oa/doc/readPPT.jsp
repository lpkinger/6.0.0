<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page isELIgnored="false" %>
<!DOCTYPE html>
<html style="height:100%">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<script type="text/javascript">
document.onmousedown = function(){return false;}
</script>
</head> 
<body style="height:100% ; width: 100%; overflow: hidden; margin: 0">
	<div style="height:100%;overflow: auto;text-align: center">
		<c:forEach begin="0" end="${param.pageSize}" varStatus="status">
			<img src='<%=basePath %>plm/project/getImage.action?path=${param.path}&page=${status.index}' /><br>
		</c:forEach>
	</div>
</body> 
</html>