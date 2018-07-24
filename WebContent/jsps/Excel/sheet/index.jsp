<!DOCTYPE HTML>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
response.setContentType("text/html; charset=utf-8");
response.setCharacterEncoding("utf-8");
%>
<html>
<head>
    <title>优软  WEBEXCEL</title>

    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="keywords" content="Online Office tools, Online Calendar, online SpreadSheet, online Excel, online Word, online Presentation, Online Schedule, online file management, online MyActivity, Web2.0 application software" />
    <meta name="description" content="FeyaSoft offers a suite of online applications which allow user to create and share online documents. FeyaSoft's online office tool includes online Calendar, online Spreadsheet Excel, online Presentation, online Word processor, online Gantt etc." />
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
    <meta http-equiv="Cache-Control" content="no-store"/>
    <meta http-equiv="Cache-Control" content="no-cache"/>
    <meta http-equiv="Expires" content="0"/>
    <meta http-equiv="Pragma" content="no-cache, must-revalidate, no-store"/>
    <meta name="robots" content="all" />

	<link rel="stylesheet" href="<%=basePath %>jsps/Excel/sheet/css/initloading.css"  type="text/css"></link>
    <link rel="shortcut icon" href="images/favicon.ico" type="image/x-icon"></link>

    <link rel="stylesheet" type="text/css" href="js/extjs/resources/css/ext-all.css"/>
    
    <script type="text/javascript" src="js/feyaSoft/Util.js"></script> 
    <script type="text/javascript" src="js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="js/extjs/ext-all.js"></script> 
    <script type="text/javascript">
        var basePath="<%=basePath%>";
        var em_code= '<%=session.getAttribute("em_code")%>';
        var caller = '';
    	var condition = '';
    	var page = 1;
    	var value = 0;
    	var total = 0;
    	var dataCount = 0;//结果总数
    	var msg = '';
    	var height = window.innerHeight;
    	if(Ext.isIE){//ie不支持window.innerHeight;document.documentElement.clientHeight == 0
    		height = screen.height*0.75;
    	}
    	var pageSize = parseInt(height*0.7/21);
    	var keyField = "";
    	var pfField = "";
    	var url = "";
    	var relative = null;
    	var FieldStore=null;
    	var FieldData=null;
    	var FnData=null;
    </script>
	<link rel="stylesheet" type="text/css" href="<%=basePath %>jsps/Excel/sheet/css/main.css">
    <link rel="stylesheet" type="text/css" href="<%=basePath %>jsps/Excel/sheet/css/common/data-view.css">
	<link rel="stylesheet" type="text/css" href="js/feyaSoft/Ext.ux/LovCombo/Ext.ux.form.LovCombo.css">
    <link rel="stylesheet" type="text/css" href="<%=basePath %>jsps/Excel/sheet/css/common/common.css">
    <link rel="stylesheet" type="text/css" href="<%=basePath %>jsps/Excel/sheet/css/common/ribbon.css">
	<link rel="stylesheet" type="text/css" href="<%=basePath %>jsps/Excel/sheet/css/common/icons.css">
	<link rel="stylesheet" type="text/css" href="js/feyaSoft/home/program/ss/resources/css/56123520130224234155.css"/>
    <link rel="stylesheet" type="text/css" href="js/feyaSoft/Ext.ux/spinner/css/Spinner.css">
    <link rel="stylesheet" type="text/css" href="js/feyaSoft/Ext.ux/statusbar/css/statusbar.css">
    <link rel="stylesheet" type="text/css" href="js/feyaSoft/Ext.ux/css/GridFilters.css">
    <link rel="stylesheet" type="text/css" href="js/feyaSoft/Ext.ux/css/RangeMenu.css">	
	<script type="text/javascript" src="js/feyaSoft/JsonP.js"></script>
	<script type="text/javascript" src="js/feyaSoft/home/CONST.js"></script>	
	<script type="text/javascript" src="js/feyaSoft/lang/en.js"></script>
    <script type="text/javascript" src="js/feyaSoft/home/program/ss/resources/lang/en.js"></script>
    <script type="text/javascript" src="js/feyaSoft/home/program/editor/resources/lang/en.js"></script>	
    <script type="text/javascript" src="js/feyaSoft/util/Common.js"></script>
    <script type="text/javascript" src="js/feyaSoft/util/ClipBoard.js"></script>
    <script type="text/javascript" src="js/feyaSoft/Ext.ux/TabCloseMenu.js"></script>
    <script type="text/javascript" src="js/feyaSoft/Ext.ux/GridKeyNav.js"></script>
    <script type="text/javascript" src="js/feyaSoft/Ext.ux/Message.js"></script>
    <script type="text/javascript" src="js/feyaSoft/Ext.ux/FileUploadField.js"></script>
    <script type="text/javascript" src="js/feyaSoft/Ext.ux/statusbar/StatusBar.js"></script>
    <script type="text/javascript" src="js/feyaSoft/Ext.ux/spinner/Spinner.js"></script>
    <script type="text/javascript" src="js/feyaSoft/Ext.ux/spinner/SpinnerField.js"></script>
    <script type="text/javascript" src="js/feyaSoft/Ext.ux/LovCombo/Ext.ux.form.LovCombo.js"></script>
    <script type="text/javascript" src="js/feyaSoft/Ext.ux/menu/RangeMenu.js"></script>
    <script type="text/javascript" src="js/feyaSoft/Ext.ux/grid/BufferView.js"></script>
    <script type="text/javascript" src="js/feyaSoft/Ext.ux/SearchField.js"></script>  
    <script type="text/javascript" src="js/feyaSoft/Ext.ux/DyConditionField.js"></script> 
    <script type="text/javascript" src="js/feyaSoft/Ext.ux/ArgsField.js"></script> 
     <script type="text/javascript" src="js/feyaSoft/Ext.ux/ExcelForm.js"></script>   
	<script type="text/javascript" src="js/feyaSoft/home/561235201302242341370.js"></script>
</head>
<body>
    <div id="loginUserFullname" value="demo demo"></div>
    <div id="editFileId" value=""></div>
    <div id="publicView" value="no"></div>
    <div id="flag" value="false"></div>
    <div id="maxExcelImportSize" value="1024"></div>
    <div id="maxUploadSize" value="1024"></div>	

</body>
</html>