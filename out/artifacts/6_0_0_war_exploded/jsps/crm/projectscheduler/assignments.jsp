<!DOCTYPE HTML>
<html>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
    <title>员工工作安排</title>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
    <link rel="stylesheet" href="<%=basePath %>resource2/resources/ext/resources/css/ext-all-gray.css" type="text/css"></link>
    <link rel="stylesheet" type="text/css" href="<%=basePath %>resource2/resources/ext/ux/css/CheckHeader.css" />


    <script type="text/javascript" src="<%=basePath %>resource2/resources/ext/ext-all.js"> </script>
    
	<!--Scheduler styles-->
    <link href="<%=basePath %>resource2/resources/css/examples.css" rel="stylesheet" type="text/css" />
    <link href="<%=basePath %>resource2/resources/gntresources/css/sch-gantt-all.css" rel="stylesheet" type="text/css" />
	<link href="<%=basePath %>resource2/resources/schresources/css/sch-all.css" rel="stylesheet" type="text/css" />
	<link href="<%=basePath %>resource2/resources/css/assignment.css" rel="stylesheet" type="text/css" />
   
	<script src="<%=basePath %>resource2/resources/ext-lang-zh_CN.js" type="text/javascript"></script>
	<script src="<%=basePath %>resource2/resources/sch-lang-zh_CN.js" type="text/javascript"></script>
    <script src="<%=basePath %>resource2/resources/sch-all-debug.js" type="text/javascript"></script>
	
    <!--Application files-->
    <script type="text/javascript" src="<%=basePath %>app/projectscheduler/assignments.js"> </script> 	 
    
    <title>员工工作情况一览表</title>
    <script type="text/javascript" >
   		 var basePath=  '<%=path %>' + '/'
    </script>  
  </head>
  <body>
    
  </body>
</html>