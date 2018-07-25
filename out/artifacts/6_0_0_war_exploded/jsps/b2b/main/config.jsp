<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
request.setCharacterEncoding("utf-8");
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="<%=basePath %>resource/ext/4.2/resources/ext-theme-gray/ext-theme-gray-all.css" type="text/css"></link>
<link rel="stylesheet" href="<%=basePath %>resource/ext/4.2/resources/ext-theme-neptune/tree-neptune.css" type="text/css"></link>
<style type="text/css">
	.x-border-box .x-tab-default-top {
		padding: 5px 20px;
		height: auto;
	}
	.x-tab-default-top button, .x-tab-default-top .x-tab-inner {
		height: auto;
	}
	.x-form-empty .x-panel-body-default {
		background: transparent;
		border-width: 0;
		color: gray;
	}
	.x-panel-header-text {
		color: #563d7c;
		font-family: "\5fae\8f6f\96c5\9ed1", sans-serif;
	}
	
	.x-grid-view .x-grid-row .x-grid-cell {
		background-color: #e0e0ff;
		border-color: #ededed;
		border-style: solid;
		border-width: 1px 0;
		border-top-color: #fafafa;
	}
	
	.x-grid-view tbody>.x-grid-wrap-row td.x-grid-rowwrap .x-grid-cell {
		background-color: #f1f2f5;
	}
	
	.x-grid-view tbody>.x-grid-wrap-row:nth-child(odd) td.x-grid-rowwrap .x-grid-cell {
		background-color: #e0e0ff;
	}
	
	.x-grid-view .x-grid-row-alt .x-grid-cell,.x-grid-row-alt .x-grid-rowwrap-div {
		background-color: #f1f2f5;
	}
	
	.x-grid-view .x-grid-row-over .x-grid-cell,.x-grid-row-over .x-grid-rowwrap-div {
		border-color: #eaeaea;
		background-color: #bcd2ee;
		color: green
	}
	
	.x-grid-view .x-grid-row-focused .x-grid-cell,.x-grid-row-focused .x-grid-rowwrap-div
		{
		border-color: #157fcc;
		background-color: #dce9f9
	}
	
	.x-btn-tb {
		width: 22px !important
	}
	.tree-back {
		background-image: url("<%=basePath %>resource/images/refresh.gif")
	}
	
	.x-form-color-trigger {
		background: url("<%=basePath %>resource/images/paintbrush.png") no-repeat center center !important
	}
	
	.help-block .x-form-display-field{
		color: gray;
		padding-left: 17px;
	}
	
	.help-block .x-form-display-field:before {
		content: '-- '
	}
	
	/*toast*/
	.toast {
		font-size: 14px
	}
	
	.toast-div {
		position: absolute;
		left: 50%;
		top: 10px;
		width: 400px;
		margin-left: -200px;
		z-index: 20000
	}
	
	.toast-div .toast {
		border-radius: 8px;
		-moz-border-radius: 8px;
		background-color: #f6f6f6;
		background-position: 15px center;
		background-repeat: no-repeat;
		border: 2px solid #ccc;
		margin: 0 0 6px;
		padding: 15px 15px 15px 60px;
		color: #555
	}
	
	.toast-div .toast h3 {
		margin: 0 0 8px;
		font-weight: bold;
		font-size: 15px
	}
	
	.toast-div .toast p {
		margin: 0
	}
	
	.dl-horizontal dt {
		float: left;
		width: 40px;
		overflow: hidden;
		clear: left;
		text-align: right;
		text-overflow: ellipsis;
		white-space: nowrap;
	}
	 .x-column-header-inner{
	text-align:center
	} 
.x-button-icon-close {
    padding-top: 20px;
	background-image: url('<%=basePath%>/resource/images/icon/trash.png')
}
.x-button-search{
	background-image: url('<%=basePath%>resource/images/btn-search.jpg')
}
	dt {
		font-weight: 700;
	}
	
	dt, dd {
		line-height: 1.42857143;
	}
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/4.2/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">
	var caller = "Config", whoami = getUrlParam('whoami');
	Ext.Loader.setConfig({
		enabled: true
	});//开启动态加载
	Ext.application({
	    name: 'erp',//为应用程序起一个名字,相当于命名空间
	    appFolder: basePath+'app',//app文件夹所在路径
	    controllers: [//声明所用到的控制层
	        'b2b.main.Config'
	    ],
	    launch: function() {
	    	Ext.create('erp.view.b2b.main.Config');//创建视图
	    }
	});
	var em_type="<%=session.getAttribute("em_type")%>";
</script>
</head>
<body >
</body>
</html>