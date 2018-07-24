<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML>
<html>
<head>
    <title>EnterpriseSheet</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="keywords" content="online SpreadSheet, online Excel, enterprise spreadsheet solution, integration" />
    <meta name="description" content="EnterpriseSheet provides an enterprise solution to integrate and build your business spreadsheet. It is an online spreadsheet running on your server." />
    <meta http-equiv="Cache-Control" content="no-store"/>
    <meta http-equiv="Cache-Control" content="no-cache"/>
    <meta http-equiv="Expires" content="0"/>
    <meta http-equiv="Pragma" content="no-cache, must-revalidate, no-store"/>
    <meta name="robots" content="all" />
    <link rel="shortcut icon" href="resource/images/favicon.png" type="image/x-icon"></link>
    <link rel="stylesheet" type="text/css" href="<%=basePath %>resource/ext/4.2/packages/ext-theme-gray/build/resources/ext-theme-gray-all.css"></link>
    <link rel="stylesheet" type="text/css" href="<%=basePath %>resource/EnterpriseSheet/resources/css/common.css"></link>
    <link rel="stylesheet" type="text/css" href="<%=basePath %>resource/EnterpriseSheet/resources/css/sheet.css"></link>
    <link rel="stylesheet" type="text/css" href="<%=basePath %>resource/EnterpriseSheet/resources/css/icon.css"></link>
    <link rel="stylesheet" type="text/css" href="<%=basePath %>resource/EnterpriseSheet/resources/font-awesome-4.6.3/css/font-awesome.min.css"></link>
    <link rel="stylesheet" type="text/css" href="<%=basePath %>resource/EnterpriseSheet/resources/font-icons/font-icon.css"></link>
    <link rel="stylesheet" type="text/css" href="<%=basePath %>resource/EnterpriseSheet/resources/css/form.css"></link>
    <link rel="stylesheet" type="text/css" href="<%=basePath %>resource/EnterpriseSheet/resources/css/toolbar.css"></link>
    <link rel="stylesheet" type="text/css" href="<%=basePath %>resource/EnterpriseSheet/resources/css/main.css"></link>
    
    
    <script type="text/javascript" src="<%=basePath %>resource/ext/4.2/ext-all.js"></script>
    <script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
    <script type="text/javascript" src="<%=basePath %>resource/ext/4.2/locale/ext-lang-zh_CN.js"></script>
    <script type="text/javascript" src="<%=basePath %>resource/EnterpriseSheet/src/EnterpriseSheet/Config.js"></script>
    <script type="text/javascript" src="<%=basePath %>resource/EnterpriseSheet/src/language/zh_CN.js"></script>
    <script type="text/javascript" src="<%=basePath %>resource/EnterpriseSheet/src/override/zh_CN.js"></script>
	<script type="text/javascript" src="<%=basePath %>resource/EnterpriseSheet/enterprisesheet-debug.js"></script>
	<script type="text/javascript">
//全局是否为模板标识
var isTplfile = true;	

var em_name = '<%=session.getAttribute("em_name")%>';

Ext.Loader.setConfig({
	enabled:true
});
Ext.application({
	    name: 'erp',
	    appFolder:basePath+'app',
	    paths: {    	
	        'EnterpriseSheet': 'src/EnterpriseSheet',           
	        'Ext.ux': 'Ext.ux'
	    },
	    controllers: [
	        // TODO: add controllers here
	        'excel.Excel'
	    ],
	    launch: function() {
  	    	Ext.getDoc().on('keydown', function(e){    		
	    		var target = e.getTarget();
	    		if(e.BACKSPACE === e.getKey() && (target === document || target === document.body)){
	    			e.preventDefault();
	    		}
	    	}, this); 
	        var docIdDom = document.getElementById('editFileId');
	        var fileId = docIdDom ? docIdDom.getAttribute('data-value') : '';
	        var url = window.location.href.toString();
	        if(fileId && -1 === url.indexOf(fileId)){
	           window.location.search = '?editFileId='+fileId;
	           return;
	        }
	        if(SCOM.isEmptyValue(fileId) && window.location.search){
	        	var search = window.location.search.split('=');
	        	if(2 == search.length){
	        		fileId = search[1];
	        	}
	        } 
	        Ext.create('erp.view.excel.Excel');
	    }   
});

	</script>	
	
</head>
<body scroll="no">
<div id="sheet-markup"></div>
</body>
</html>