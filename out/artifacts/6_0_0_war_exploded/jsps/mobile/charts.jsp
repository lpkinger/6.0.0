<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0,maximum-scale=1.0,user-scalable=no" />
<title>订阅</title>
<%
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ request.getContextPath() + "/";
%>
<script type="text/javascript" src="<%=basePath%>resource/jquery/jquery-1.11.3.min.js"></script>  	
<script type="text/javascript" src="<%=basePath%>resource/jquery/jquery.json-2.2.min.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/other/highcharts-4.2.5.js"></script>

<link rel="stylesheet" href="<%=basePath%>resource/jquery/jsgrid/css/jsgrid.css" type="text/css" />
<link rel="stylesheet" href="<%=basePath%>resource/jquery/jsgrid/css/theme.css" type="text/css" /> 
<link rel="stylesheet" href="<%=basePath%>resource/bootstrap/bootstrap.min.css" type="text/css" />
<link rel="stylesheet" href="<%=basePath%>jsps/mobile/bootstrap/bootstrap-datetimepicker.min.css" type="text/css" />
<script type="text/javascript" src="<%=basePath%>resource/jquery/jsgrid/jsgrid.min.js"></script> 
<%-- <script type="text/javascript" src="<%=basePath%>resource/jquery/jquery.jqGrid.js"></script>  --%>
<script type="text/javascript" src="<%=basePath%>resource/ext/bootstrap.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/jquery/global.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/jquery/jquery.smart-form.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/jquery/jquery.editable-select.min.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/bootstrap/bootstrap.min.js"></script>
<script type="text/javascript" src="<%=basePath%>jsps/mobile/bootstrap/bootstrap-datetimepicker.min.js"></script>
<script type="text/javascript" src="<%=basePath%>jsps/mobile/bootstrap/bootstrap-datetimepicker.zh-CN.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>

<base href="<%=basePath%>jsps/mobile/" />
<link rel="stylesheet" href="charts.css" type="text/css" />
<script type="text/javascript" src="charts.js"></script>
<script type="text/javascript" src="chartsBase.js"></script>
<script type="text/javascript" src="chartForm.js"></script>

<style type="text/css">
input.es-input {
	padding-right: 20px !important;
	background:
		url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAICAYAAADJEc7MAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAIGNIUk0AAG2YAABzjgAA4DIAAIM2AAB5CAAAxgwAADT6AAAgbL5TJ5gAAABGSURBVHjaYvz//z8DOYCJgUzA0tnZidPK8vJyRpw24pLEpwnuVHRFhDQxMDAwMPz//x+OOzo6/iPz8WFGuocqAAAA//8DAD/sORHYg7kaAAAAAElFTkSuQmCC)
		right center no-repeat;
}

input.es-input.open {
	-webkit-border-bottom-left-radius: 0;
	-moz-border-radius-bottomleft: 0;
	border-bottom-left-radius: 0;
	-webkit-border-bottom-right-radius: 0;
	-moz-border-radius-bottomright: 0;
	border-bottom-right-radius: 0;
}

.es-list {
	position: absolute;
	padding: 0;
	margin: 0;
	border: 1px solid #d1d1d1;
	display: none;
	z-index: 1000;
	background: #fff;
	max-height: 320px;
	overflow-y: auto;
	-moz-box-shadow: 0 2px 3px #ccc;
	-webkit-box-shadow: 0 2px 3px #ccc;
	box-shadow: 0 2px 3px #ccc;
}

.es-list li {
	display: block;
	padding: 5px 10px;
	margin: 0;
}

.es-list li.selected {
	background: #f3f3f3;
}

.es-list li[disabled] {
	opacity: .5;
}

#myModal {
	position: absolute;
	width: 100%;
	overflow: hidden;
}

#showButton {
	background-image: url(img/search.png);
	width: 25px;
	height: 25px;
	background-size: 70%;
	background-repeat: no-repeat;
	background-color: rgba(0, 0, 0, 0);
	border-radius: 20px;
	background-position: center;
	border: 0.5px solid gray;
	position: fixed;
	right: 6px;
	top: 6px;
	z-index: 3;
	opacity: 0.80;
}

.filterDiv {
	display: inline-block;
	padding: 4px 6px;
	color: #555;
	float-left: 10%;
	vertical-align: middle;
	border-radius: 20px;
	width: 100%;
	line-height: 22px;
	cursor: text;
	padding-right: 30px;
}

.span {
	display: inline-block;
	padding: .2em .6em .3em;
	/* font-size: 75%; */
	font-weight: 700;
	/* line-height: 1; */
	color: #fff;
	text-align: center;
	white-space: nowrap;
	vertical-align: baseline;
	border-radius: .25em;
	background: rgb(125, 181, 237);
	margin: 1px;
}

.head {
	width: 100%;
	position: fixed;
	background-color: #fff;
	z-index: 3;
	border-bottom: 0.5px solid #e6e6e6;
	border-radius: 0px;
}

.showModel {
	position: absolute;
	top: 5px;
}

.jsgrid-grid-body {
	max-height: 480px;
	overflow-y: auto;
}

.loading-container {
	position: absolute;
	width: 100%;
	height: 100%;
	background-repeat: no-repeat;
	background-image: url("<%=basePath%>/resource/images/loading.gif");
	background-position: 48%;
	background-size: 24px 24px;
}
.modal-content{
	border-width: 0px;
	border-radius: 0px;
	-webkit-box-shadow: none;
	box-shadow:none;
	border-radius: 6px 6px 0 0;
}
.modal-footer{
	border-radius: 0 0 6px 6px;
}
.scanner{
	background: url("img/scan.png") no-repeat;
	background-size: 20px 20px;
	background-position:center center;
	width: 40px;
	height: 34px;
}
</style>
</head>
<body style="background-color:#EDEDED;">

	<!-- Modal -->
	<div class="modal fade " id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
	  <div class="modal-dialog" role="document" style="top:10%;margin:0 auto;height:60%;width:80%;z-index: 7;">
	    <div class="modal-content" style="height:100%">
	      <div class="modal-header" style="height:8%"">
	        <button id="close1" type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
	        <h4 class="modal-title" id="myModalLabel">查找</h4>
	      </div>
	      <div class="modal-body" id="formModelBody" style="height:92%; overflow-y:scroll;">
	       	<div id='form'></div>
	      </div>
	      
	    </div>
	    <div class="modal-footer" style="height:46px;background-color:white;z-index: 8;">
	       	<button type="button" class="btn btn-primary" id='submit'>确认</button>
	        <button type="button" class="btn btn-default" data-dismiss="modal" id='cancel'>取消</button>
	      </div>
	  </div>
	</div>
	
	<!-- Modal2 -->
	<div class="modal fade " id="gridModel" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
	  <div class="modal-dialog" role="document" style="top:10%;margin:0 auto;width:100%;">
	    <div class="modal-content" style="height:100%">
	      <div class="modal-header" style="height:8%"">
	        <button id="close2" type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
	        <h4 class="modal-title" id="gridModelLabel"></h4>
	      </div>
	      <div class="modal-body" style="height:79%; overflow-y:scroll; padding-left: 3px">
	      	<div id="externalGridPager_" class="external-pager"></div>
	       	<div id='grid'></div>
	      </div>
	      <!-- <div class="modal-footer">
	       	<button type="button" class="btn btn-primary" id='submit'>确认</button>
	        <button type="button" class="btn btn-default" data-dismiss="modal" id='cancel'>取消</button>
	      </div> -->
	    </div>
	  </div>
	</div>
	
<div class="head">
 	 <div class="showModel"><button id="showButton"></button></div>
 	 <div id="showLabel"></div>
</div>
<div id="panel">
	<div class="loading-container"></div>
	<div id='sum'></div>
	<div id='container'></div>
	<div id='list'></div>
</div>
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
</body>
<script>
	
	var emId = '<%=request.getAttribute("emId")%>';
	var numId= '<%=request.getAttribute("numId")%>';
	var mainId= '<%=request.getAttribute("mainId")%>';
	var insId= '<%=request.getAttribute("insId")%>';
	var title= '<%=request.getAttribute("title")%>';
	var isMobile= '<%=request.getAttribute("isMobile")%>';
	var dialog;
		
	var basePath = (function() {
		var fullPath = window.document.location.href;
		var path = window.document.location.pathname;
		var subpos = fullPath.indexOf('//');
		var subpath = subpos > -1 ? fullPath.substring(0, subpos + 2) : '';
		if (subpos > -1)
			fullPath = fullPath.substring(subpos + 2);
		var pos = fullPath.indexOf(path), sname = path.substring(0, path.substr(1).indexOf('/') + 1);
		sname = (['/jsps','/workfloweditor','/resource','/system','/process','/demo','/exam','/oa','/opensys','/mobile','/common'].indexOf(sname) > -1 ? '/' : sname);
		return subpath + fullPath.substring(0, pos) + sname + (sname == '/' ? '' : '/');
	})();
</script>
</html>