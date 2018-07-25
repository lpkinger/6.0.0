<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>数据导出</title>
<link rel="icon" href="<%=basePath%>resource/images/icon_title.png"
	type="image/x-icon" />
<style>
body {
	font-family: Microsoft YaHei, Arial, Helvetica, "Lucida Grande",
		sans-serif;
	font-size: .875rem;
	line-height: 1.5rem;
	color: #40444f;
	background: #f5f9fa;
	text-shadow: 0 .0625rem 0 rgba(255, 255, 255, .6);
}

.container {
	width: 80%;
	margin: 10% auto;
	text-align: center;
}

.progress {
	margin-bottom: 1.875rem;
	padding: .1875rem;
	border: .0625rem solid #ddd;
	-webkit-border-radius: .9375rem;
	border-radius: .9375rem;
	background-color: #fff;
	height: 1.875rem;
	-webkit-transform: translateZ(0);
	-moz-transform: translateZ(0);
	-o-transform: translateZ(0);
	-ms-transform: translateZ(0);
	transform: translateZ(0);
}

.box {
	width: 100%;
	-webkit-box-sizing: border-box;
	-moz-box-sizing: border-box;
	box-sizing: border-box;
	float: left;
	white-space: nowrap;
	overflow: hidden;
	-o-text-overflow: ellipsis;
	text-overflow: ellipsis;
}

.bar {
	min-width: 1.375rem;
	width: 100%;
	height: 100%;
	-webkit-border-radius: .6875rem;
	border-radius: .6875rem;
	-webkit-box-shadow: rgba(0, 0, 0, .08) 0 -.6rem 1rem 0 inset;
	box-shadow: rgba(0, 0, 0, .08) 0 -.6rem 1rem 0 inset;
	-webkit-transition: width .3s ease;
  	-o-transition: width .3s ease;
  	transition: width .3s ease;
}

.bar.bar-striped {
	background: -webkit-linear-gradient(315deg, rgba(0, 0, 0, 0) 0%,
		rgba(0, 0, 0, 0) 31%, rgba(0, 0, 0, .05) 33%, rgba(0, 0, 0, .05) 67%,
		rgba(0, 0, 0, 0) 69%);
	background: -moz-linear-gradient(315deg, rgba(0, 0, 0, 0) 0%,
		rgba(0, 0, 0, 0) 31%, rgba(0, 0, 0, .05) 33%, rgba(0, 0, 0, .05) 67%,
		rgba(0, 0, 0, 0) 69%);
	background: -o-linear-gradient(315deg, rgba(0, 0, 0, 0) 0%,
		rgba(0, 0, 0, 0) 31%, rgba(0, 0, 0, .05) 33%, rgba(0, 0, 0, .05) 67%,
		rgba(0, 0, 0, 0) 69%);
	background: -ms-linear-gradient(315deg, rgba(0, 0, 0, 0) 0%,
		rgba(0, 0, 0, 0) 31%, rgba(0, 0, 0, .05) 33%, rgba(0, 0, 0, .05) 67%,
		rgba(0, 0, 0, 0) 69%);
	background: linear-gradient(135deg, rgba(0, 0, 0, 0) 0%,
		rgba(0, 0, 0, 0) 31%, rgba(0, 0, 0, .05) 33%, rgba(0, 0, 0, .05) 67%,
		rgba(0, 0, 0, 0) 69%);
	-webkit-background-size: 3rem 1.4rem;
	-moz-background-size: 3rem 1.4rem;
	background-size: 3rem 1.4rem;
}

.bar-striped.active {
	-webkit-animation: bar-striped 1.5s linear 0 infinite;
	-moz-animation: bar-striped 1.5s linear 0s infinite;
	-o-animation: bar-striped 1.5s linear 0 infinite;
	-ms-animation: bar-striped 1.5s linear 0 infinite;
	animation: bar-striped 1.5s linear 0s infinite;
}

@-webkit-keyframes bar-striped { 
	0%{
		background-position: 0;
	}
	100%{
		background-position: 3rem;
	}
}
@-o-keyframes bar-striped { 
	0%{
		background-position: 0;
	}
	100%{
		background-position: 3rem;
	}
}
@-ms-keyframes bar-striped { 
	0%{
		background-position: 0;
	}
	100%{
		background-position: 3rem;
	}
}
@keyframes bar-striped { 
	0%{
		background-position: 0;
	}
	100%{
		background-position: 3rem;
	}
}
.progress.waiting .bar {
	width: 0;
	background-color: transparent;
}

.progress.loading .bar {
	width: 1%;
	background-color: #3ee283;
}

.progress.creating .bar {
	width: 100%;
	background-color: #48d9ea;
}

.progress.complete .bar {
	width: 100%;
	background-color: #95e22d;
	background-image: none;
	-webkit-animation: none;
	-moz-animation: none;
	-o-animation: none;
	-ms-animation: none;
	animation: none;
}

.bar:after {
	position: absolute;
	margin: 0;
	left: 0;
	right: 0;
	padding: .3rem 0;
	font-size: .8rem;
	font-weight: 400;
	text-align: center;
	letter-spacing: .02rem;
	opacity: .9;
	-ms-filter: "progid:DXImageTransform.Microsoft.Alpha(Opacity=90)";
	filter: alpha(opacity = 90);
	line-height: 1em;
	padding: .3rem 0;
}

.waiting .bar:after {
	content: "请稍等"
}

.loading .bar:after {
	content: "正在获取数据, 请稍等"
}

.creating .bar:after {
	content: "正在创建文件, 请稍等"
}

.complete .bar:after {
	content: "下载完成"
}

.muted {
	color: #999;
}

.num {
	font-style: normal;
	font-family: verdana;
	font-weight: 700;
	color: #ffb433;
}
</style>
<script type="text/javascript"
	src="<%=basePath%>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/i18n/i18n.js"></script>
<script type="text/javascript"
	src="<%=basePath%>resource/ux/downloadify/swfobject.js"></script>
<script type="text/javascript"
	src="<%=basePath%>resource/ux/downloadify/downloadify.min.js"></script>
<script type="text/javascript"
	src="<%=basePath%>resource/ux/downloadify/excel.js"></script>
<script type="text/javascript">
var setting = ${setting}, caller = getUrlParam('caller'), condition = getUrlParam('condition'), title = getUrlParam('title');
</script>
</head>
<body>
	<div class="container">
		<div id="progress" class="progress waiting box">
			<div id="progress-bar" class="bar bar-striped active"></div>
		</div>
		<p class="muted">
			已找到<span id="dataCount" class="num">0</span>条数据
		</p>
		<p id="downloadify"></p>
	</div>
</body>
<script type="text/javascript">
function loadData(callable) {
	var step = 20, stepSize = 5000, req = function(idx, callback){
		Ext.Ajax.request({
			url: basePath + 'common/datalist/data.action',
			params: {
				caller: caller,
				condition: condition,
				page: (idx + 1),
				pageSize: stepSize,
				_f: 1,
				_alia: 1
			},
			timeout: 600000,
			callback: function(opt, s, r) {
				callback.call(null, idx, s, r);
			}
		});
	}, comp = 0, data = {}, total = 0;
	for(var i = 0;i < step;i++ ) {
		req(i, function(idx, s, r){
			++comp;
			if(s && r.responseText) {
				data[idx] = Ext.JSON.decode(r.responseText).data;
				total += data[idx].length;
				setState(total, 100 * comp / step);
			}
			if(comp == step) {
				var sortedData = [];
				for(var k in data) {
					var t = data[k];
					for(var j in t) {
						sortedData.push(t[j]);
					}
				}
				callable.call(null, sortedData);
				return;
			}
		});
	}
}
function setState(dataCount, width) {
	Ext.defer(function(){
		Ext.get('dataCount').dom.innerHTML = dataCount;
		Ext.get('progress-bar').setWidth(width + '%');
	}, 20);
}
function updateProgress(oldState, newState) {
	Ext.get('progress').replaceCls(oldState, newState);
}
function createFile(params, data, title) {
	var el = new $excel(params);
	el.addData(data);
	if(typeof window.webkitRequestFileSystem != 'undefined') {
		var url = el.create();
		var a = document.createElement("a");
	    document.body.appendChild(a);
	    a.style = "display: none";
		a.href = url;
		a.download = title;
		a.click();
		URL.revokeObjectURL(url);
		updateProgress('creating', 'complete');
		Ext.defer(function(){
			window.close();
		}, 100);
	} else {
		Downloadify.create('downloadify', {
			filename : function() {
				return title;
			},
			data : function() {
				return el.getXml();
			},
			onError : function() {
				alert('文件下载失败!');
			},
			swf : basePath + 'resource/ux/downloadify/media/downloadify.swf',
			downloadImage : basePath + 'resource/ux/downloadify/images/download.png',
			width : 100,
			height : 30,
			transparent : true,
			append : false
		});
	}
}
Ext.onReady(function(){
	updateProgress('waiting', 'loading');
	loadData(function(data){
		updateProgress('loading', 'creating');
		Ext.defer(function(){
			createFile(setting, data, (title || '导出') + '.xls');
		}, 50);
	});
});
</script>
</html>