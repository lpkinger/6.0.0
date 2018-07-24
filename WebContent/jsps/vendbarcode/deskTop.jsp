<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" type="text/css"
	href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css" />
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css"
	type="text/css"></link>
<style type="text/css">
.lock {
    background: url('../../resource/images/16/lock.png') center center no-repeat;
}
.lockopen {
    background: url('../../resource/images/16/lock_open.png') center center no-repeat no-repeat;
}
font {
	font-size: 14px;
}
ul li a {
	font-size: 13.5px;
	text-decoration: none;
}
.x-panel-body-default {
	background: #f1f2f5;
	border-left: none !important;
	border-top: none !important;
	border-bottom: none !important;
}
.bench {
	background: url('<%=basePath %>resource/images/background_1.jpg');
}
.mydiv {
	height: 100%;
	background: #f1f1f1;
}
.home-div {
	height: 100%;
	background: url('<%=basePath %>resource/images/background_2.jpg');
}

.home-img {
	cursor: pointer;
}

.home-img:hover {
	background: url('<%=basePath %>resource/images/background_2.jpg');
}

.home-img span {
	font-size: 1;
	color: red;
}

.news,.notify {
	height: 100%;
	background: url('<%=basePath %>resource/images/background_1.jpg');
}

#bench_news,#bench_notify {
	width: 100%;
}

.schedule {
	background: url('<%=basePath %>resource/images/background_1.jpg');
}

.news ul {
	list-style: none;
}

.div-more a {
	color: #CD9B9B;
}

.subscription {
	height: 100%;
	background: url('<%=basePath %>resource/images/background_1.jpg');
}

.div-link {
	padding-left: 10px;
	margin-top: 3px;
}

a:hover {
	color: #8B8B00;
}

.btn-title {
	font-size: 13px;
	color: blue;
	margin-bottom: 0px;
}

.btn-title:hover {
	color: red;
}

.news-img {
	cursor: pointer;
	margin-left: 5px;
}

.news-img:hover {
	background: transparent;
	background-color: #D6BEF9;
	height: 38px;
	width: 38px;
}

.div-left {
	display: inline;
	float: left;
	color: green
}

.div-right {
	display: inline;
	float: right;
	font-family: sans-serif;
}

.div-right a {
	color: blue;
	text-decoration: none;
	font-size: 13px;
}

.div-right a:hover {
	color: red;
	cursor: pointer;
	font-weight: bold;
}

.div-left font {
	float: left;
	color: green;
	font-size: 13px;
	display: inline;
}

.x-panel-header-text {
	font-size: 13px;
	color: #09a471;
	font-weight: normal;
}

.x-panel-header-text-default-framed {
	font-size: 13px;
	color: #09a471;
	font-weight: normal;
}

#bench .x-panel-header .x-box-inner .x-box-item {
	display: inline;
	height: 18px;
}

.number {
	height: 24px;
	width: 24px;
	background: url('<%=basePath %>resource/images/number/number.png');
	margin-top: -30px;
}

.custom-grid .x-grid-row .x-grid-row-alt {
	height: 20px;
}

.custom-grid .x-grid-row .x-grid-cell {
	height: 20px;
	line-height: 20px;
	vertical-align: top;
}

.custom-grid .x-grid-row .x-grid-cell-inner {
	height: 20px;
	line-height: 20px;
	vertical-align: top;
}

.custom-grid .x-grid-row .x-grid-cell:hover {
	height: 26px;
	line-height: 26px;
}

.custom-grid a {
	text-decoration: none;
}

.custom-grid a:hover {
	font-weight: 800;
}

.custom-grid .x-action-col-icon {
	margin: 0px 5px 0px 5px;
	cursor: pointer;
}

.task-grid .x-grid-row .x-grid-row-alt {
	background-color: red !important;
}

.x-grid-cell-topic b {
	display: block;
}
.x-grid-cell-topic .x-grid-cell-inner {
	white-space: normal;
	height: auto !important;
	background-color:#FAFAFA;
	border-color: #ededed;
	border-style: solid;
	border-width: 1px 0;
	border-top-color: #F5F5F5;
	border-bottom: none;
	line-height: 26 px;
	white-space: nowrap
}

.x-grid-cell-topic1 .x-grid-cell-inner {
	white-space: normal;
	height: auto !important;
	background-color:#FAFAFA;
	border-color: #ededed;
	border-style: solid;
	border-width: 1px 0;
	border-top-color: #F5F5F5;
	border-bottom: none;
	line-height: 26 px;
	white-space: nowrap
}
.x-grid-cell-topic a {
	text-decoration: none;
}

.x-grid-cell-topic a:hover {
	text-decoration: underline;
}

.x-grid-cell-topic .x-grid-cell-innerf {
	padding: 5px;
}
.list-default>li {
	padding: 2px 5px;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}
.custom-framed{
    padding:0px 0px 5px 0px;
}
.more-tool .x-tool-more {
	width: 47px;
	height: 16px;
	background-image: url('<%=basePath %>resource/images/mainpage/more.gif');
}
.x-grid-header-simple{
    border:none!important
   }
.set-tool .x-tool-set {
	width: 16px;
	height: 16px;
	margin-right:5px;
	background-image: url('<%=basePath %>resource/images/mainpage/setting.png');
}
#app-header {
    color: #596F8F;
    font-size: 22px;
    font-weight: 200;
    padding: 8px 15px;
    text-shadow: 0 1px 0 #fff;
}
#app-msg {
    background: #D1DDEF;
    border: 1px solid #ACC3E4;
    padding: 3px 15px;
    font-weight: bold;
    font-size: 13px;
    position: absolute;
    right: 0;
    top: 0;
}
.x-panel-ghost {
    z-index: 1;
}
.x-border-layout-ct {
    background: #DFE8F6;
}
.x-portal-body {
    padding: 0 0 0 8px;
    overflow-y:auto;
    overflow-x:hidden !important;
}
.x-portal .x-portal-column {
    padding: 8px 8px 0 0;
}
.x-portal .x-panel-dd-spacer {
    border: 2px dashed #99bbe8;
    background: #f6f6f6;
    border-radius: 4px;
    -moz-border-radius: 4px;
    margin-bottom: 10px;
}
.x-portlet {
    margin-bottom:10px;
    padding: 1px;
    border-width: 1px;
    border-style: solid;
}
.x-portlet .x-panel-body {
    background: #fff;
}
.portlet-content {
    padding: 10px;
    font-size: 11px;
}

#app-options .portlet-content {
    padding: 5px;
    font-size: 12px;
}
.settings {
    background-image:url(../shared/icons/fam/folder_wrench.png);
}
.nav {
    background-image:url(../shared/icons/fam/folder_go.png);
}
.info {
    background-image:url(../shared/icons/fam/information.png);
}
 .x-grid-cell-topic b {
            display: block;
        }
        .x-grid-cell-topic .x-grid-cell-inner {
            white-space: normal;
        }
        .x-grid-row .x-grid-cell a {
           // color: #385F95;
            text-decoration: none;
        }
        .x-grid-row .x-grid-cell a:hover {
           text-decoration: none;
        }
		.x-grid-cell-topic .x-grid-cell-innerf {
			padding: 5px;
		}
		.x-grid-row .x-grid-cell{
		     background-color:#FAFAFA!important;
		}
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">
var hasReminded ='<%=session.getAttribute("hasReminded")%>';
var WHeight=window.innerHeight;
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'vendbarcode.deskTop'
    ],
    launch: function() {
        Ext.create('erp.view.vendbarcode.main.viewPort');
    }
});
var em_id ='<%=session.getAttribute("em_id")%>';


</script>
</head>
<body >
</body>
</html>