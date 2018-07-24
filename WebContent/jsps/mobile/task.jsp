<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8" />
<meta name="viewport"
	content="initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=0, width=device-width">
<meta http-equiv="Cache-Control" content="no-siteapp" />
<title>任务处理-UAS管理系统</title>
<meta name="description"
	content="USOFTCHINA.COM,ERP,SCM,CRM,MRP,企业管理,优软" />
<meta name="keywords" content="USOFTCHINA.COM,ERP,SCM,CRM,MRP,企业管理,优软" />
<%
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ request.getContextPath() + "/";
%>
<link href="<%=basePath%>resource/bootstrap/bootstrap.min.css"
	rel="stylesheet">
<!-- <link
	href="//cdn.bootcss.com/font-awesome/4.2.0/css/font-awesome.min.css"
	rel="stylesheet"> -->
<base href="<%=basePath%>jsps/mobile/" />
<link rel="stylesheet" href="task.css" type="text/css" />
</head>
<body>
	<!-- 流程基本信息 Start -->
	<div id="top" class="container">
		<div class="title">
			<span id="jp_name" class="text"></span>
		</div>
	</div>
	<!-- 流程基本信息 End -->
	<!-- 基本操作 Start -->
	<div id="deal" class="container">
		<form class="form">
			<!-- 处理情况描述 -->
			
		</form>
		<div class="row">
			<div class="col-xs-12 text-center">
				<div id="buttons" class="btn-group btn-group-xs btn-group-justified">
					<!-- 按钮组 -->
				</div>
			</div>
		</div>
	</div>
	<!-- 基本操作 End -->
	<!-- 单据信息 Start -->
	<div id="bill" class="container">
		<div class="title">
			<div class="text left">
				<span class="glyphicon glyphicon-copyright-mark"></span>&nbsp;任务信息
			</div>
			<!-- 单据编号 -->
			<div class="text right" id="jp_codevalue"></div>
		</div>
		<div class="row">
			<div class="col-xs-12">
				<div class="tab-content">
					<div id="bill-main" class="tab-pane active">
						<!-- 单据主记录信息 -->
						<div class="empty"></div>
					</div>
				</div>
			</div>
			<div id="record" class="col-xs-12">
				<!-- 任务记录 -->
			</div>
		</div>
	</div>
	<!-- 单据信息 End -->
	<!-- 关联单据 Start -->
	<div id="relative" class="container">
		<div class="title">
			<div class="text">
				<span class="glyphicon glyphicon-registration-mark"></span>&nbsp;相关单据
			</div>
		</div>
		<a id="expand" class="btn btn-inverse btn-block">展开&nbsp;<span
			class="glyphicon glyphicon-chevron-down"></span></a>
		<div id="relation" class="row hidden">
			<div class="col-xs-12">
				<ul id="tab-header" class="list-unstyled nav nav-tabs justified">
					<li class="active"><a><span
							class="glyphicon glyphicon-book"></span>&nbsp;单据信息</a></li>
					<li id="relation-detail-header"><a><span
							class="glyphicon glyphicon-list-alt"></span>&nbsp;单据明细</a></li>
				</ul>
			</div>
			<div class="col-xs-12">
				<div class="tab-content">
					<div id="relation-main" class="tab-pane active">
						<!-- 单据主记录信息 -->
						<div class="empty"></div>
					</div>
					<div id="relation-detail" class="tab-pane">
						<!-- 单据明细信息 -->
						<div class="empty"></div>
					</div>
				</div>
			</div>
			<div class="col-xs-12">
				<a id="shrink" class="btn btn-inverse btn-block">收起&nbsp;<span
				class="glyphicon glyphicon-chevron-up"></span></a>
			</div>
		</div>
	</div>
	<!-- 关联单据 End -->
	<!-- modal -->
	<div class="modal-backdrop in" style="z-index: 9998; display: none"></div>
	<div class="modal in" style="z-index: 9999; display: none">
		<div class="modal-dialog modal-sm">
			<div class="modal-content">
				<div class="modal-header">
					<strong class="modal-title text-lg"></strong>
				</div>
				<div class="modal-body"></div>
			</div>
		</div>
	</div>
	<!-- loading -->
	<div class="loading-container">请稍等...</div>
</body>
<script>
var basePath = (function() {
	var fullPath = window.document.location.href;
	var path = window.document.location.pathname;
	var subpos = fullPath.indexOf('//');
	var subpath = subpos > -1 ? fullPath.substring(0, subpos + 2) : '';
	if (subpos > -1)
		fullPath = fullPath.substring(subpos + 2);
	var pos = fullPath.indexOf(path), sname = path.substring(0, path.substr(1).indexOf('/') + 1);
	sname = (['/jsps','/workfloweditor','/resource','/system','/process','/demo','/exam','/oa','/opensys','/mobile'].indexOf(sname) > -1 ? '/' : sname);
	return subpath + fullPath.substring(0, pos) + sname + (sname == '/' ? '' : '/');
})();
</script>
<script type="text/javascript"
	src="<%=basePath%>resource/jquery/jquery-1.11.3.min.js"></script>
<script type="text/javascript" src="task.js"></script>
</html>