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
<base href="<%=basePath%>jsps/mobile/" />
<link rel="stylesheet" href="process.css" type="text/css" />
<!-- A link to a Boostrap  and jqGrid Bootstrap CSS siles-->
<link rel="stylesheet" type="text/css" media="screen" href="<%=basePath%>resource/css/ui.jqgrid.css" />
<link rel="stylesheet" type="text/css" media="screen" href="<%=basePath%>resource/css/jquery-ui.css" />

<script type="text/javascript"
	src="<%=basePath%>resource/jquery/jquery-1.11.3.min.js"></script>  	
<script type="text/javascript"
	src="<%=basePath%>resource/jquery/jquery.json-2.2.min.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/jquery/jquery-ui.min.js"></script>
<script type="text/javascript" src="process.js"></script>
<script type="text/javascript" src="processinquiry.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/jquery/grid.locale-cn.js"></script>
 
 
<!-- This is the Javascript file of jqGrid -->   
<script type="text/javascript" src="<%=basePath%>resource/jquery/jquery.jqGrid.min.js"></script> 
<script type="text/javascript" src="<%=basePath%>resource/bootstrap/bootstrap.min.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/jquery/showtip.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/jquery/sh/thenBy.js"></script>

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
		<div class="row" id='buttons'>
			<div class="col-xs-12 text-center">
				<div class="btn-group btn-group-xs btn-group-justified">
					<div class="btn-group">
						<button id="agree" type="button" class="btn btn-default line">
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
						<button id="change" type="button" class="btn btn-default line">
							<span class="glyphicon glyphicon-repeat"></span>&nbsp;变更
						</button>
					</div>
					<div class="btn-group">
						<button id="next" type="button" class="btn btn-default line">
							<span class="glyphicon glyphicon-chevron-right"></span>&nbsp;下一条
						</button>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- 基本操作 End -->
	<!-- 单据信息 Start -->
    <div id="bill" class="container">
       <div id = "bill-info" >
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
		</div>
		<div class="row" >
				<div class="col-xs-12" >				
					<ul class="list-unstyled nav nav-tabs justified " id="topToolbar">
						<li id="main-header" class="active"><a><span
								class="glyphicon glyphicon-book"></span>&nbsp;单据信息</a></li>
								<!-- 单据明细信息 -->		
						<li id="history-header"><a><span
								class="glyphicon glyphicon-map-marker"></span>&nbsp;审批记录</a></li>
						<li id="all-history-header"><a><span
								class="glyphicon glyphicon-map-marker"></span>&nbsp;历史审批</a></li>
					</ul>
					<div class="tab-content">
						<div id="bill-main" class="tab-pane active">
							<!-- 单据主记录信息 -->
							<div class="empty"></div>
						</div>					
						<!-- 单据明细信息 -->					
						<div id="history" class="tab-pane">
							<!-- 历史节点 -->
							<div class="empty"></div>
						</div>
						<!-- 单据所有历史明细信息 -->					
						<div id="all-history" class="tab-pane">
							<!-- 历史节点 -->
							<div class="empty"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- 相关查询 End -->
		<!-- modal -->
		<div class="modal-backdrop in" style="z-index: 9998; display: none"></div>
		<div class="modal in" style="z-index: 9999; display: none">
			<div class="modal-dialog modal-sm">
				<div class="modal-content">
					<div class="modal-header">
						<strong class="modal-title text-lg"></strong>
						<a class="clearbutton" id="clearbutton"></a>
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
</html>