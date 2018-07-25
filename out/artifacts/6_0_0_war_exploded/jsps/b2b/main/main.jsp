<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<html>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<head>
<script>
function geturl(){
	var b2cUrl="";
	var accountUrl="";  
	/* if(true){//测试
		 b2cUrl="http://192.168.253.12:23400"; //商城测试
		//b2cUrl="http://192.168.253.131:8080/platform-b2c"; //王宇超商城测试
		 accountUrl="http://113.105.74.135:8001"; //账套中心测试  
		 //accountUrl="http://192.168.253.66:8080"; //账套中心汪测试 地址
	} */
	var n=location.href.indexOf("?url=");
 	var typeIndex=location.href.indexOf("&urlType="); 
 	var selflocation = self.location.href;
	para="<%=basePath%>b2b/ucloudUrl_token.action"+((n>0) ? selflocation.substring(n) : "")+"&b2cUrl="+b2cUrl+"&accountUrl="+accountUrl;
	para=para.replace("#","%23");
  	document.getElementById("iframe_b2bpage").src=para; 
  	var CurvePane=document.getElementById("iframe_b2bpage");
  	CurvePane.height=window.document.body.clientWidth;
  	CurvePane.width=window.document.body.clientHeight;
}
</script>
</head>
<body onload="geturl();">
<div id="maindiv">
	<iframe id="iframe_b2bpage" style="width:100%;height:98%;" target="iframe_b2bpage" align="left" scrolling="auto" frameborder="0"></iframe>
</div>
</body>	
</html>
