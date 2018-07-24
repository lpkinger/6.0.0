<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
request.setCharacterEncoding("utf-8");
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css" type="text/css"></link>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<style>
.custom-button {
	cursor: pointer;
	border-top: 1px solid #96d1f8;
	background: #65a9d7;
	background: -webkit-gradient(linear, left top, left bottom, from(#3e779d), to(#65a9d7) );
	background: -webkit-linear-gradient(top, #3e779d, #65a9d7);
	background: -moz-linear-gradient(top, #3e779d, #65a9d7);
	background: -ms-linear-gradient(top, #3e779d, #65a9d7);
	background: -o-linear-gradient(top, #3e779d, #65a9d7);
	padding: 1px 5px;
	-webkit-border-radius: 8px;
	-moz-border-radius: 8px;
	border-radius: 8px;
	-webkit-box-shadow: rgba(0, 0, 0, 1) 0 1px 0;
	-moz-box-shadow: rgba(0, 0, 0, 1) 0 1px 0;
	box-shadow: rgba(0, 0, 0, 1) 0 1px 0;
	text-shadow: rgba(0, 0, 0, .4) 0 1px 0;
	color: white;
	font-size: 13px;
	font-family: Georgia, serif;
	text-decoration: none;
	vertical-align: middle;
	height: 24;
}
.custom-label {
	color: red;
	font-size: 15px;
	font-weight: bold;
}
.loading {
	background-image: url("<%=basePath %>resource/images/loading.gif") !important;
	background-position: center;
	background-repeat: no-repeat;
}
.custom-field {
	margin-left: 10px;
	color: #666;
	font-weight: normal;
}
.checked {
	margin-left: 30% !important;
	background-image: url("<%=basePath %>resource/images/renderer/finishrecord.png") !important;
	background-repeat: no-repeat;
	background-position: center;
	color: blue;
}
.error {
	background-image: url("<%=basePath %>resource/images/renderer/important.png") !important;
	background-repeat: no-repeat;
	background-position: center;
	color: red;
	font-weight: normal;
}
.detail {
	cursor: pointer;
	color: blue;
	text-decoration: underline;
}
.custom .x-grid-row, .custom .x-grid-row .x-grid-cell,.custom .x-grid-cell-inner {
	height: auto !important;
}
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">
/**
 * 校验转单数据
 */
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'ma.ShiftCheck'
    ],
    launch: function() {
    	Ext.create('erp.view.ma.ShiftCheck');//创建视图
    }
});
</script>
</head>
<body >
</body>
</html>