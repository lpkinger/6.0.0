<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<style type="text/css">

.search-item {
	font: normal 11px tahoma, arial, helvetica, sans-serif;
	padding: 3px 10px 3px 10px;
	border: 1px solid #fff;
	border-bottom: 1px solid #eeeeee;
	white-space: normal;
	color: #555;
}

.search-item h3 {
	display: block;
	font: inherit;
	font-weight: bold;
	color: #222;
}

.search-item h3 span {
	float: right;
	font-weight: normal;
	margin: 0 0 5px 5px;
	width: 150px;
	display: block;
	clear: none;
}

.msg .x-box-mc {
	font-size: 14px;
}
#msg-div {
	position: absolute;
	left: 50%;
	top: 10px;
	width: 400px;
	margin-left: -200px;
	z-index: 20000;
}

#msg-div .msg {
	border-radius: 8px;
	-moz-border-radius: 8px;
	background: #F6F6F6;
	border: 2px solid #ccc;
	margin-top: 2px;
	padding: 10px 15px;
	color: #555;
}

#msg-div .msg h3 {
	margin: 0 0 8px;
	font-weight: bold;
	font-size: 15px;
}

#msg-div .msg p {
	margin: 0;
}
.x-button-icon-query {
	background-image: url('<%=basePath %>resource/images/query.png')
}
.x-button-icon-excel {
	background-image: url('<%=basePath %>resource/images/excel.png')
}
.x-button-icon-close {
	background-image: url('<%=basePath %>resource/images/icon/trash.png')
}

/***
进度
*/
.progress {
  height: 40px;
  padding-left:50px;
  margin-left:50px;
  margin-top:-2px;
  clear: both;
  color: #888;
  font-size: 13px;
}
.flowchart {padding: 10px 15px 1px 10px; min-width: 500px;}
.flow-item {float: left; width: 10.66667%; max-width: 140px; text-align: center; margin-bottom: 9px; padding-right: 5px;}
.flow-item > div {position: relative; padding: 2px 0 2px 10px; line-height: 20px; background: #90A4AE; white-space:nowrap; overflow: visible; color: #fff;}
.flow-item > div:before, .flow-item > div:after {content: ' '; display: block; width: 0; height: 0; border-style: solid; border-width: 12px 0 12px 12px; border-color: transparent transparent transparent #90A4AE; position: absolute; left: 0; top: 0}
.ie-8 .flow-item > div:before {display: none}
.flow-item > div:before {border-left-color: #fff; z-index: 1}
.flow-item > div:after {left: auto; right: -12px; z-index: 2}
.ie-8 .flow-item > div {margin-right: 10px}
/* .flow-item-0 > div:before {display: none}
.flow-item-1 > div {background: #1976D2}
.flow-item-1 > div:after {border-left-color: #1976D2}
.flow-item-2 > div {background: #4CAF50}
.flow-item-2 > div:after {border-left-color: #4CAF50}
.flow-item-3 > div {background: #F57C00}
.flow-item-3 > div:after {border-left-color: #F57C00}
.flow-item-4 > div {background: #EF5350}
.flow-item-4 > div:after {border-left-color: #EF5350}
.flow-item-5 > div {background: #AB47BC}
.flow-item-5 > div:after {border-left-color: #AB47BC} */
.flow-item-0-finish > div:before {display: none}
.flow-item-0-finish > div {background: #1976D2}
.flow-item-0-finish > div:after {border-left-color: #1976D2}
.flow-item-finish > div {background: #1976D2}
.flow-item-finish > div:after {border-left-color: #1976D2}

.flow-item-0-doing > div:before {display: none}
.flow-item-0-doing > div {background: #F57C00}
.flow-item-0-doing > div:after {border-left-color: #F57C00}
.flow-item-doing > div {background: #F57C00}
.flow-item-doing > div:after {border-left-color: #F57C00}
.color-column-inner {
	border-radius: 15px;
	height: 25px;
	margin-left: 10px;
	width: 25px;
	border: 1px solid #d0d0d0
}
.custom-rest{
	background:  url('<%=basePath %>/resource/images/x.png') no-repeat center
		center !important;
	width: 16px !important;
	background-image: url('<%=basePath %>/resource/ext/4.2/resources/ext-theme-gray/images/form/text-bg.gif'); 
    height: 16px;
    border: 0!important;
    visibility:hidden;
    margin: 2px 0px 0px 0px;
}
.x-button-icon-help{
  background:  url('<%=basePath %>/resource/images/help.png')
}
.tip-block{
   width: 13px;
   height: 13px;
   float:left;
   margin-left:10px;
}
.tip-block-finish{
   background-color: #1976D2;
}
.tip-block-doing{
   background-color: #F57C00;
}
.tip-block-undone{
  background-color: #90A4AE;
}
</style>
<link rel="icon" href="<%=basePath%>resource/images/icon_title.png"
	type="image/x-icon" />
<link rel="stylesheet"
	href="<%=basePath%>resource/ext/4.2/resources/ext-theme-gray/ext-theme-gray-all.css"
	type="text/css"></link>
<link rel="stylesheet"
	href="<%=basePath%>resource/ext/4.2/resources/ext-theme-neptune/tree-neptune.css"
	type="text/css"></link>
<link rel="stylesheet" type="text/css"
	href="<%=basePath%>resource/ux/css/CheckHeader.css" />
<link rel="stylesheet" type="text/css"
	href="<%=basePath%>jsps/sys/css/GroupTabPanel.css" />
<style type="text/css">
.x-toolbar-sencha {
	background: #e0e0e0;
	color: #304c33;
	border: none !important;
}

.x-toolbar-sencha .x-logo {
	padding: 10px 10px 10px 31px;
	/*  background: url(../images/logo.png) no-repeat 10px 11px; */
	color: #fff;
	text-align:center;
	font-size: 22px;
	font-weight: bold;
	text-shadow: 0 1px 0 #4e691f;
}
.help-terms{
    position: absolute;
    background-repeat: no-repeat;
    width: 16px;
    margin-left:5px;
    height: 18px;

}
.x-tip-default{
  background-color:rgba(219, 225, 206, 0.2) !important;
 }
</style>
<script type="text/javascript"
	src="<%=basePath%>resource/ext/4.2/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">	
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',
    appFolder: basePath+'app',
    controllers: ['plm.project.ProjectProgress'],
    launch: function() {
    	Ext.create('erp.view.plm.project.ProjectProgress');
    }
});
</script>
</head>
<body >
</body>
</html>