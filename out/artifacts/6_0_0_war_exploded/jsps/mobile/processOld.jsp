<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8" />
<meta name="viewport"
	content="initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=0, width=device-width">
<meta http-equiv="Cache-Control" content="no-siteapp" />
<title>流程处理-UAS管理系统</title>
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
<link rel="stylesheet" href="processOld.css" type="text/css" />
</head>
<body>
	<!-- 流程基本信息 Start -->
	<div id="top" class="container">
		<div class="title">
			<span id="jp_name" class="text"></span>
		</div>
		<div class="text-center">
			<ul class="list-unstyled list-inline info">
				<li class="round"><span class="glyphicon glyphicon-th-list"></span>&nbsp;<span
					id="jp_nodeName"></span></li>
				<li><span class="text-muted">发起：</span><span
					id="jp_launcherName"></span></li>
				<li><span class="text-muted">时间：</span><span id="jp_launchTime"></span></li>
			</ul>
		</div>
	</div>
	<!-- 流程基本信息 End -->
	<!-- 基本操作 Start -->
	<div id="deal" class="container">
		<form class="form">
			<!-- 审批要点 -->
			<div id="points"></div>
			<textarea id="deal-msg" rows="2" placeholder="填写您的审批意见..."
				class="form-control"></textarea>
		</form>
		<div class="row">
			<div class="col-xs-12 text-center">
				<div class="btn-group btn-group-xs btn-group-justified">
					<div class="btn-group">
						<button id="agree" type="button" class="btn btn-default line"
							id="agree">
							<span class="glyphicon glyphicon-thumbs-up"></span>&nbsp;我同意
						</button>
					</div>
					<div class="btn-group">
						<button id="disagree" type="button"
							class="btn btn-default line text-error">
							<span class="glyphicon glyphicon-thumbs-down"></span>&nbsp;不同意
						</button>
					</div>
					<div class="btn-group">
						<button id="end" type="button" class="btn btn-default line">
							<span class="glyphicon glyphicon-off"></span>&nbsp;结束
						</button>
					</div>
					<div class="btn-group">
						<button id="change" type="button" class="btn btn-default line">
							<span class="glyphicon glyphicon-repeat"></span>&nbsp;变更
						</button>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- 基本操作 End -->
	<!-- 单据信息 Start -->
	<div id="bill" class="container">
		<div class="title">
			<div class="text left">
				<span class="glyphicon glyphicon-copyright-mark"></span>&nbsp;相关单据
			</div>
			<!-- 单据编号 -->
			<div class="text right" id="jp_codevalue"></div>
		</div>
		<form id="ex">
			<div class="form-group">
				<textarea id="ex-msg" rows="1" placeholder="您对本次拜访的评价是..."
					class="form-control"></textarea>
			</div>
			<div class="form-group">
				<label>评分：</label>
				<span id="ex-rating">
					<button type="button" class="btn btn-circle" title="差">差</i></button>
					<button type="button" class="btn btn-circle" title="中">中</button>
					<button type="button" class="btn btn-circle" title="良">良</button>
					<button type="button" class="btn btn-circle" title="优">优</button>
				</span>
				<button id="ex-confirm" type="button" class="btn btn-default btn-sm pull-right">确认评价</button>
			</div>
		</form>
		<div class="row">
			<div class="col-xs-12">
				<ul class="list-unstyled nav nav-tabs justified">
					<li class="active"><a><span
							class="glyphicon glyphicon-book"></span>&nbsp;单据信息</a></li>
					<li id="detail-header"><a><span
							class="glyphicon glyphicon-list-alt"></span>&nbsp;单据明细</a></li>
					<li id="history-header"><a><span
							class="glyphicon glyphicon-map-marker"></span>&nbsp;审批历史</a></li>
				</ul>
				<div class="tab-content">
					<div id="bill-main" class="tab-pane active">
						<!-- 单据主记录信息 -->
						<div class="empty"></div>
					</div>
					<div id="bill-detail" class="tab-pane">
						<!-- 单据明细信息 -->
						<div class="empty"></div>
					</div>
					<div id="history" class="tab-pane">
						<!-- 历史节点 -->
						<div class="empty"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- 单据信息 End -->
	<!-- 相关查询 Start -->
	<!-- <div id="relative" class="container">
		<div class="title">
			<div class="text">
				<span class="glyphicon glyphicon-registration-mark"></span>&nbsp;相关查询
			</div>
		</div>
		<a id="expand" class="btn btn-inverse btn-block">没有相关查询信息</a>
		<div id="re-content" class="hidden">
			查询筛选
			<div id="re-filtrate" >
				这是查询筛选
			</div>
			查询结果
			<div id="re-result">
				这是查询结果
			</div>
		</div>
		<a id="shrink" class="btn btn-inverse btn-block hidden">收起&nbsp;<span class="glyphicon glyphicon-chevron-up"></span></a>
	</div> -->
	<!-- 相关查询 End -->
	<!-- modal -->
	<div class="modal-backdrop in" style="z-index: 9998; display: none"></div>
	<div class="modal in" style="z-index: 9999; display: none">
		<div class="modal-dialog modal-sm">
			<div class="modal-content">
				<div class="modal-header">
					<strong class="modal-title text-lg"></strong>
					<!-- <button type="button" class="pull-right btn btn-xs btn-default"
						onclick="closePage()">直接退出</button> -->
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
	src="<%=basePath%>resource/jquery/jquery-1.4.min.js"></script>
<script type="text/javascript"
	src="<%=basePath%>resource/jquery/jquery.json-2.2.min.js"></script>
<script type="text/javascript" src="processOld.js?version=2015"></script>
</html>