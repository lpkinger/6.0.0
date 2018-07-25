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
<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/ext/4.2/resources/ext-theme-gray/ext-theme-gray-all.css"/>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<style>
	.x-grid-cell-inner {
	    height: 26px;
	    line-height: 26px;
	    overflow: hidden;
	    -o-text-overflow: ellipsis;
	    text-overflow: ellipsis;
	    padding: 0px 6px;
	    white-space: nowrap;
    }
	.x-grid-group-hd  {
	    padding: 6px;
	    background: #EEEEE0;
	    border-width: 0 0 2px 0;
	    border-style: solid;
	    border-color: #bcb1b0;
	    cursor: pointer;
	}
	.custom-total .x-grid-cell{
		font-weight:bold;
		background-color: #CDB38B;
	}
	.x-form-display-field {
		font-size: 14px;
		color: blue
	}
	.x-grid-highlight .x-grid-cell,.x-grid-highlight.x-grid-cell{
		background-color: #FFE4B5;
	}
	.x-panel-with-col-lines .x-grid-row .cell-split.x-grid-cell{
		border-right-style: dashed !important;
		background-color: #FFE4B5!important;
		border-right-color: #999 !important;
	}
	.x-btn-default-toolbar-small-icon-text-left .x-btn-inner {
	    line-height: 20px !important;
	}
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/4.2/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">
Ext.override(Ext.grid.column.Number, {
	defaultRenderer : function(value) {
		return value ? Ext.util.Format.number(value, this.format) : '';
    }
});
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'fa.fix.ShowDifferAll'
    ],
    launch: function() {
    	Ext.create('erp.view.fa.fix.ShowDifferAll');//创建视图
    }
});
/* var caller = '${param.whoami}'; */
var yearmonth = getUrlParam('yearmonth');
var chkun = getUrlParam('chkun');
</script>
</head>
<body >
</body>
</html>