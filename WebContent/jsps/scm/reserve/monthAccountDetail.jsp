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
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<style>
	.isCount .x-grid-cell{
		background-color: #FFE4B5;
	}
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">
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
        'scm.reserve.MonthAccountDetail'
    ],
    launch: function() {
        Ext.create('erp.view.scm.reserve.MonthAccountDetail');
    }
});
var caller = 'MonthAccountDetail';

var yearmonth = getUrlParam('yearmonth');
var catecode = getUrlParam('catecode');
var catename = getUrlParam('catename');
var chkun = getUrlParam('chkun');

</script>
</head>
<body >
</body>
</html>