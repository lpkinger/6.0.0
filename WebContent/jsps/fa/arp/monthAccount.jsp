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
	.x-panel .x-grid-body {
	    background: #f1f1f1;
	    border-color: #d0d0d0;
	    border-style: solid;
	    border-width: 1px;
	    border-top-color: #c5c5c5;
	}
	.x-grid-cell-inner {
	    padding: 0 6px;
	}
	.x-grid-group-hd  {
	    padding: 6px;
	    background: #EEEEE0;
	    border-width: 0 0 2px 0;
	    border-style: solid;
	    border-color: #bcb1b0;
	    cursor: pointer;
	}
	.isCount .x-grid-cell{
		font-style: italic;
		background-color: #FFE4B5;
	}
	.x-grid-with-col-lines .x-grid-row .x-grid-cell.x-grid-cell-warn{
		border-right-style: dashed !important;
		background-color: #FFE4B5!important;
		border-right-color: #999 !important;
		font-style: italic;
	}
	.x-form-display-field {
		margin-top:7px;
	}
	.x-btn-default-toolbar-small-icon-text-left .x-btn-inner {
	    line-height: 20px !important;
	}
	.x-btn-default-toolbar-small-noicon .x-btn-inner {
	    line-height: 20px;
	}
	.x-group-header{
		background:#4b9de3 !important;
	}
	.x-group-sub-header{
	    border-bottom-width: 0px !important;
	}
	.x-form-item-label, .x-form-cb-label-after {
	    font-size: 12px !important;
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
/**
 * 期末对账
 */
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'fa.arp.MonthAccount'
    ],
    launch: function() {
        Ext.create('erp.view.fa.arp.MonthAccount');
    }
});
var caller = 'MonthAccount!AP';
</script>
</head>
<body >
</body>
</html>