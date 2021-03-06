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
<style type="text/css">

#container { 
	padding:auto; 
	margin:auto;
	text-align:center;
	height: 100%;
	display: table;/*让元素以表格形式渲染*/ 
}
 
#content {  
	display:table-cell;/*让元素以表格的单元素格形式渲染*/ 
	vertical-align: middle;/*使用元素的垂直对齐*/
}

#content p{  
	line-height:200%; 
}


.x-window-body-default {
    border-color: #bcb1b0;
    border-width: 0px!important;
    background: #e0e0e0;
    color: black;
}

.x-window-body {
    position: relative;
    border-style: none!important; 
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
        'fs.credit.CustCreditTargets'
    ],
    launch: function() {
        Ext.create('erp.view.fs.credit.CustCreditTargets');
    }
});
var caller=getUrlParam('whoami');
var type = getUrlParam('type');
var condition = getUrlParam('gridCondition');

var pCaller = parent.caller;
var readOnly = "${param.readOnly}";

readOnly = readOnly||type==null;

if(type==null){
	var gridCondition = condition.replace(/IS/g, "=")+' order by cct_type asc,cct_ctdetno asc';
}else{
	var gridCondition = "cct_type = '"+type+"' and "+condition.replace(/IS/g, "=")+' order by cct_ctdetno asc';
}

if(condition.indexOf('IS')>0){
	craid = condition.substring(condition.indexOf('IS')+2,condition.length); 
}else{
	craid = condition.substring(condition.indexOf('=')+2,condition.length-1); 
}
</script>
</head>
<body >
</body>
</html>