<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css" type="text/css"></link>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<script type="text/javascript">	
function fn(){
	window.open("http://218.17.158.219:8086/svnwebclient/logout.jsp", "svn版本管理", "Status=yes,scrollbars=yes,resizable=yes,width=" + (screen.availWidth - 10) + ",height=" + (screen.availHeight - 50) + ",top=0,left=0");
	setTimeout("close()",300);
}
function close(){
	parent.Ext.getCmp('2803').close();
}
</script>
</head>
<body onload="fn()">
</body>
</html>