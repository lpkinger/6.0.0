<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8" />
<meta name="viewport"
	content="initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=0, width=device-width">
<meta http-equiv="Cache-Control" content="no-siteapp" />
<title>优软管理系统,usoftchina.com</title>
<meta name="description"
	content="USOFTCHINA.COM,ERP,SCM,CRM,MRP,企业管理,优软" />
<meta name="keywords" content="USOFTCHINA.COM,ERP,SCM,CRM,MRP,企业管理,优软" />
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<base href="<%=basePath%>jsps/mobile/" />
<link rel="stylesheet" href="nav.css" type="text/css" />
</head>
<body>
	<div id="header-container">
		<div class="left">
			<a href="javascript:window.location.reload();">
				<div class="homeIcon"></div>
			</a>
		</div>
		<div class="right" style="margin-right: 2px; display: none;" id="next">
			<a href="javascript:next();">
				<div class="clubIcon"></div>
				<div>下一条</div>
			</a>
		</div>
		<div class="right" style="margin-right: 2px;">
			<a href="javascript:logout();">
				<div class="clubIcon"></div>
				<div>${mobileinfo.employee.em_name}</div>
			</a>
		</div>
		<div class="right" id="app">
			<a
				href="javascript:$('#app').toggleClass('active');$('#header-container ul').toggleClass('active');">
				<div class="appIcon"></div>
				<div>${mobileinfo.employee.currentMaster.ma_function}</div>
			</a>
			<ul>
				<c:forEach items="${masters}" var="master">
					<c:if
						test="${master.ma_name != mobileinfo.employee.currentMaster.ma_name}">
						<li id="${master.ma_name}"><a
							href="javascript:changeMaster('${master.ma_name}','${master.ma_function}');">${master.ma_function}</a>
						</li>
					</c:if>
				</c:forEach>
			</ul>
		</div>
	</div>
	<div id="app-container">
		<ul>
			<li><a id="link-flow" href="javascript:toogleAppCss('flow');">
					<img class="iconIn" src="img/news.png">
					<div class="desc">
						待办流程<span class="count">(${mobileinfo.flowCount})</span>
					</div>
			</a></li>
			<li><a id="link-procand"
				href="javascript:toogleAppCss('procand');"> <img class="iconIn"
					src="img/club.png">
					<div class="desc">
						可选流程<span class="count">(${mobileinfo.procandCount})</span>
					</div>
			</a></li>
			<li><a id="link-task" href="javascript:toogleAppCss('task');">
					<img class="iconIn" src="img/category.png">
					<div class="desc">
						待办任务<span class="count">(${mobileinfo.taskCount})</span>
					</div>
			</a></li>
		</ul>
		<div id="info-flow" class="info">
			<c:forEach items="${mobileinfo.flows}" var="flow" begin="0" end="49">
				<a target="_blank"
					href="javascript:itemClick('flow','${flow.CURRENTMASTER}',${flow.jp_nodeId});"
					class="info-item" id="info-item-flow-${flow.jp_nodeId}"
					data-type="flow" data="${flow}">
					<h4 class="info-item-heading">${flow.jp_form}&nbsp;&nbsp;(${flow.jp_codevalue}
						${flow.CURRENTMASTER})</h4>
					<p class="info-item-text">${flow.jp_launchTime}&nbsp;&nbsp;发起人:${flow.jp_launcherName}</p>
				</a>
			</c:forEach>
		</div>
		<div id="info-procand" class="info">
			<c:forEach items="${mobileinfo.procands}" var="procand" begin="0"
				end="49">
				<a target="_blank"
					href="javascript:itemClick('procand','${procand.CURRENTMASTER}',${procand.jp_nodeId});"
					class="info-item" id="info-item-procand-${procand.jp_nodeId}"
					data-type="procand" data="${procand}">
					<h4 class="info-item-heading">${procand.jp_form}&nbsp;&nbsp;(${procand.jp_codevalue}
						${procand.CURRENTMASTER})</h4>
					<p class="info-item-text">${procand.jp_launchTime}&nbsp;&nbsp;发起人:${procand.jp_launcherName}</p>
				</a>
			</c:forEach>
		</div>
		<div id="info-task" class="info">
			<c:forEach items="${mobileinfo.tasks}" var="task" begin="0" end="49">
				<a
					href="javascript:itemClick('task','${task.CURRENTMASTER}',${task.ra_id}, '${task.ra_type}');"
					class="info-item" id="info-item-task-${task.ra_id}"
					data-type="task" data="${task}">
					<h4 class="info-item-heading">${task.ra_taskname}&nbsp;&nbsp;${task.sourcecode}</h4>
					<p class="info-item-text">${task.ra_startdate}&nbsp;&nbsp;发起人:${task.recorder}</p>
					<p class="info-item-text">${task.description}</p>
				</a>
			</c:forEach>
		</div>
	</div>
	<div id="frame-container">
		<iframe src="javascript:void(0);" width="1280px" height="900px"></iframe>
	</div>
	<div id="footer-container">
		<p class="link">
			<a
				href="<%=basePath%>?master=${mobileinfo.employee.currentMaster.ma_name}">首页</a>
			| <a href="#" id="download_client">客户端下载</a> | <a
				href="<%=basePath%>?mobile=0">电脑版</a>
		</p>
	</div>
</body>
<script type="text/javascript"
	src="<%=basePath%>resource/jquery/jquery-1.4.min.js"></script>
<script type="text/javascript"
	src="<%=basePath%>resource/jquery/jquery.json-2.2.min.js"></script>
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
<script type="text/javascript" src="nav.js"></script>
</html>