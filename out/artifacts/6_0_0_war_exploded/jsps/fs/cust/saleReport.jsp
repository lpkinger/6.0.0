<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
request.setCharacterEncoding("utf-8");
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">  
<html xmlns="http://www.w3.org/1999/xhtml">  
<head>
<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css" type="text/css"></link>
<%-- <link rel="stylesheet" href="<%=basePath %>jsps/sys/css/init.css" type="text/css"></link> --%>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<style>
.x-btn-default-small{
	border-radius:0px;
}

td, th {
    display: table-cell;
    vertical-align: inherit;
    line-height:26px;
}

.progress>div>ul>li>span{
	display:inline-block!important;
}
.progress>div>ul>li{
	line-height:35px;
	cursor: pointer;
}
.progress>div>ul>li>span.font{
	font-size:14px!important;
	margin-left: 5px;
}
.redcircle {
   z-index: 2;
    /* position: relative; */
    height: 10px;
    width: 10px;
    -webkit-border-radius: 50em;
    -moz-border-radius: 50em;
    border-radius: 50em;
    font-size: 10px;
    background: red;
    -webkit-box-sizing: content-box;
    -moz-box-sizing: content-box;
    box-sizing: content-box;
    padding: 0;
    display: block;
    cursor: pointer;
}
.greencircle{
	z-index: 2;
    /* position: relative; */
    height: 10px;
    width: 10px;
    -webkit-border-radius: 50em;
    -moz-border-radius: 50em;
    border-radius: 50em;
    font-size: 10px;
    background: #6dd866;
    -webkit-box-sizing: content-box;
    -moz-box-sizing: content-box;
    box-sizing: content-box;
    padding: 0;
    display: block;
    cursor: pointer;
}
 .salereportna {
	background-color:white !important;
	border-width:0 px !important;
} 


</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/RowExpander.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">
/**
 * 优化
 */
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'fs.cust.SaleReport'
    ],
    launch: function() {
    	Ext.create('erp.view.fs.cust.SaleReport');//创建视图
    }
});

var custcode = getUrlParam('sa_custcode');
var ordercode = getUrlParam('sa_code');
function changeModule(module){
	var href = "#"+module;
	location.hash=href; 
}
</script>
</head>
<body >
</body>
</html>