<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
    <title>项目计划</title>
 <%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
    <link rel="stylesheet" href="<%=basePath %>resource2/resources/ext/resources/css/ext-all-gray.css" type="text/css"></link>
    <link rel="stylesheet" type="text/css" href="<%=basePath %>resource2/resources/ext/ux/css/CheckHeader.css" />


    <script type="text/javascript" src="<%=basePath %>resource2/resources/ext/ext-all.js"> </script>
    
	<!--Scheduler styles-->
	<link href="<%=basePath %>resource2/resources/css/advanced.css" rel="stylesheet" type="text/css" />
    <link href="<%=basePath %>resource2/resources/css/examples.css" rel="stylesheet" type="text/css" />
    <link href="<%=basePath %>resource2/resources/gntresources/css/sch-gantt-all.css" rel="stylesheet" type="text/css" />
	<link href="<%=basePath %>resource2/resources/schresources/css/sch-all.css" rel="stylesheet" type="text/css" />
	<link href="<%=basePath %>resource2/resources/css/gantt-scheduler.css" rel="stylesheet" type="text/css" />
	
   
	<script src="<%=basePath %>resource2/resources/ext-lang-zh_CN.js" type="text/javascript"></script>
	<script src="<%=basePath %>resource2/resources/sch-lang-zh_CN.js" type="text/javascript"></script>
    <script src="<%=basePath %>resource2/resources/gnt-all-debug.js" type="text/javascript"></script>
    <script src="<%=basePath %>resource2/resources/sch-all-debug.js" type="text/javascript"></script>
    <!-- <style type="text/css">
            .task {
                background-image: url(../ext/shared/icons/fam/cog.gif) !important;
            }
            .task-folder {
                background-image: url(../ext/shared/icons/fam/folder_go.gif) !important;
            }
            //'<%=basePath %>'.substring(0, '<%=basePath %>'.substr(1).indexOf('/') + 1);
     </style> -->
     
    <script type="text/javascript" src="<%=basePath %>app/projectscheduler/projectScheduler.js"> </script> 	 
    <script type="text/javascript" >
   		 var basePath=  '<%=path %>' + '/'
    </script>  
</head>
<body>
</body>
</html>