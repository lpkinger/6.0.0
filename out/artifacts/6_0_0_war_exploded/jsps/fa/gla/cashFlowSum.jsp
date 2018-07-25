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
<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css"/>
<style type="text/css">
body {
	font: 14px "Microsoft YaHei";
}
.x-panel .x-grid-body {
	background: #f9f9f9 !important;
}
.u-treegrid .x-grid-row .x-grid-cell {
	background-color: #f9f9f9;
	padding: 4px 5px;
	font-size: 14px;
}
.x-tree-panel .x-grid-row .x-grid-cell {
	border: solid #ddd;
	border-width: 0 1px 1px 0;
}
.x-button-icon-query {
	background-image: url("<%=basePath%>resource/images/query.png")
}
.u-button {
	-moz-border-radius: 3px;
	-webkit-border-radius: 3px;
	-o-border-radius: 3px;
	-ms-border-radius: 3px;
	-khtml-border-radius: 3px;
	border-radius: 3px;
	padding: 2px 2px 2px 2px;
	border: 1px solid #bbbbbb;
	background-image: none;
	background-color: #f8f8f8;
	background-image: -webkit-gradient(linear, 50% 0%, 50% 100%, color-stop(0%, #ffffff), color-stop(100%, #eeeeee) );
	background-image: -webkit-linear-gradient(top, #ffffff, #eeeeee);
	background-image: -moz-linear-gradient(top, #ffffff, #eeeeee);
	background-image: -o-linear-gradient(top, #ffffff, #eeeeee);
	background-image: -ms-linear-gradient(top, #ffffff, #eeeeee);
	background-image: linear-gradient(top, #ffffff, #eeeeee);
}
.u-button button {
	min-height: 22px;
	height: auto;
	line-height: 1;
}
.u-button .x-btn-inner {
	font-size: 14px;
}
.u-button:hover {
	background-color: #f8f8f8;
	background-image: -webkit-gradient(linear, 50% 0%, 50% 100%, color-stop(0%, #ffffff), color-stop(100%, #d7d7d7) );
	background-image: -webkit-linear-gradient(top, #ffffff, #d7d7d7);
	background-image: -moz-linear-gradient(top, #ffffff, #d7d7d7);
	background-image: -o-linear-gradient(top, #ffffff, #d7d7d7);
	background-image: -ms-linear-gradient(top, #ffffff, #d7d7d7);
	background-image: linear-gradient(top, #ffffff, #d7d7d7);
}
.x-column-header {
	font: 13px "Microsoft YaHei";
	text-align: center;
}
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'fa.gla.CashFlowSum'
    ],
    launch: function() {
        Ext.create('erp.view.fa.gla.cashFlowSum.Viewport');
    }
});
</script>
</head>
<body>
</body>
</html>