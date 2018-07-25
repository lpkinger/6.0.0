<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%
	request.setCharacterEncoding("utf-8");
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<!DOCTYPE html>
<html style="height: 100%; background: transparent !important;">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet"
	href="<%=basePath%>resource/ext/resources/css/ext-all-gray.css"
	type="text/css"></link>
<link rel="stylesheet" href="<%=basePath%>resource/css/main.css"
	type="text/css"></link>
<style type="text/css">
.bodyc {
	width: 100%;
	height: 100%;
	position: absolute;
	/*   background: #FFFFFF !important; */
	background: #F0F0F0 !important;
}
/*   @media ( min-width : 1025px) {
	img {
		margin: 0 auto;
	}
  } 
  @media ( max-width :1024px) {
	
  }  */
.cell {
	display: table-cell;
	vertical-align: middle;
	width: 100%;
	height: 100%;
}

/* 图片自动缩放*/
.imgh {
	margin: 0 auto;
	max-width: 100%;
	height: auto;
} 

.table {
	width: 100%;
	height: 100%;
	overflow: hidden;
	text-align: center;
	display: table;
	float: left;
	position: relative;
}
</style>
<script type="text/javascript"
	src="<%=basePath%>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/i18n/i18n.js"></script>
</head>

<body class="bodyc">
	<div class="table">
		<div class="cell">
			<img src="<%=basePath%>resource/images/sysnavigation/QC.png"
				class="imgh" border="0" usemap="#Map" />
			<map name="Map" id="Map">
				<!-- 客户资料列表 -->
			<!-- 	<area shape="rect" coords="13,11,151,50"
					href="javascript:openTable('客户资料列表','jsps/common/datalist.jsp?whoami=Customer','Customer');">
				客户资料维护
				<area shape="rect" coords="163,11,196,50"
					href="javascript:openTable('客户资料维护','jsps/scm/sale/customerBase.jsp','Customer!Base');">
				客户估价单列表
				<area shape="rect" coords="267,11,407,50"
					href="javascript:openTable('估计单列表','jsps/common/datalist.jsp?whoami=Evaluation','Evaluation');">
				客户估价单维护
				<area shape="rect" coords="413,11,448,50"
					href="javascript:openTable('估价单维护','jsps/scm/sale/evaluation.jsp','Evaluation');"> -->
			</map>
		</div>
	</div>
</body>
</html>