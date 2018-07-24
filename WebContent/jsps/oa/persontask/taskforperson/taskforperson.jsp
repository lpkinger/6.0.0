<%@ page language="java" import="java.util.*,java.text.SimpleDateFormat" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
 <%SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    String date=format.format(new Date());%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
  <!--Ext and ux styles -->
	 <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" href="<%=basePath %>resource/gnt/ext-all-gray.css" type="text/css"></link>
    
	<!--Scheduler styles-->
    <link href="<%=basePath %>resource/gnt/css/sch-all.css" rel="stylesheet" type="text/css" />
    
	<!--Implementation specific styles-->
    <link href="<%=basePath %>resource/gnt/css/sch/examples.css" rel="stylesheet" type="text/css" /> 
	<!--Ext lib and UX components-->
    <script src="<%=basePath %>resource/gnt/ext-all.js" type="text/javascript"></script>
    
    <!--Scheduler files-->
    <script src="<%=basePath %>resource/gnt/sch-all-debug.js" type="text/javascript"></script>

    <!--Application files-->
    <script src="assignresource.js" type="text/javascript"></script>

    <script type="text/javascript">
    var assignmentEditor='';
    var recorder = '<%=session.getAttribute("em_name")%>';
    var recorddate="<%=date%>";
    var basePath="<%=basePath%>";
    </script>
    <script src="<%=basePath %>resource/i18n/i18n.js" type="text/javascript"></script>
    <title>Gantt demo</title>
</head>
<body>
    <div id="north">
    </div>
</body>
</html>