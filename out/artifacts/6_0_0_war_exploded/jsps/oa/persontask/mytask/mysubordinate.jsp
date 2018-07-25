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
	
    <link href="<%=basePath %>resource/gnt/resources/css/ext-all-gray.css" rel="stylesheet" type="text/css" />
    <%-- <link href="<%=basePath %>resource/css/main.css" rel="stylesheet" type="text/css" /> --%>
	<!--Scheduler styles-->
    <link href="<%=basePath %>resource/gnt/css/sch-gantt-all.css" rel="stylesheet" type="text/css" />
	<!--Implementation specific styles-->
    <link href="<%=basePath %>resource/gnt/advanced/advanced.css" rel="stylesheet" type="text/css" />
    <link href="<%=basePath %>resource/gnt/css/examples.css" rel="stylesheet" type="text/css" /> 
	<!--Ext lib and UX components-->
    <script src="<%=basePath %>resource/gnt/ext-all.js" type="text/javascript"></script>
	<!--Gantt components-->
    <script src="<%=basePath %>resource/gnt/gnt-all-debug.js" type="text/javascript"></script>
    <script src="<%=basePath %>resource/gnt/sch-all-debug.js" type="text/javascript"></script>
    <script src="<%=basePath %>app/util/BaseUtil.js" type="text/javascript"></script>
    <!--Application files-->
    <script src="task.js" type="text/javascript"></script>
    <script type="text/javascript">
    var assignmentEditor='';
    var recorder = '<%=session.getAttribute("em_name")%>';
    var recorddate="<%=date%>";
    var basePath="<%=basePath%>";
    </script>
    
    <title>Gantt demo</title>
</head>
<body>
    <div id="north">
    </div>
</body>
</html>