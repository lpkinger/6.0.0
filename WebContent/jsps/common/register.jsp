<%@ page language="java" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
	<head>
		<title>创建帐套</title>
		<meta http-equiv="Content-type" content="text/html;
			charset=utf-8">
		<link rel="stylesheet" type="text/css" href="<%=basePath %>/resource/css/style.css"/>
		<link rel="stylesheet" type="text/css" href="<%=basePath %>/resource/css/register.css"/>
		<script type="text/javascript" src="<%=basePath %>resource/jquery/jquery-1.4.min.js"></script>
		<script type="text/javascript" src="<%=basePath %>resource/jquery/jquery.json-2.2.min.js"></script>
		<script type="text/javascript" src="<%=basePath %>resource/other/register.js"></script>
	</head>
	<body>
		<div id="wrap">
			<%@include file="register_header.jsp"%>
			<div id="tab">
				 <ul>
				 <li id="tab1" class="up"><h3><a href="javascript:toggleTo(1);">基本信息</a></h3>
				 <div id="oDIV1">
				 	<%@include file="register_body1.jsp"%>
				 </div>
				 </li>
				 <li id="tab2"><h3><a href="javascript:toggleTo(2);">其他信息</a></h3>
				 <div id="oDIV2" style="display:none;">
				 	<%@include file="register_body2.jsp"%>
				 </div>
				 </li>
				 <li id="tab3"><h3><a href="javascript:toggleTo(3);">邮箱确认</a></h3>
				 <div id="oDIV3" style="display:none;">
				 	<%@include file="register_body3.jsp"%>
				 </div>
				 </li>
				 <li id="tab4"><h3><a href="javascript:toggleTo(4);">信息确认</a></h3>
				 <div id="oDIV4" style="display:none;">
				 	<%@include file="register_body4.jsp"%>
				 </div>
				 </li>
				 </ul>
			</div>
			<%@include file="register_footer.jsp"%>
		</div>
	</body>
</html>