<%@ page language="java" import="java.util.*,java.text.SimpleDateFormat" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
 <%SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    String date=format.format(new Date());%>
<!DOCTYPE html>
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
    <link href="<%=basePath %>resource/gnt/advanced/css/style1.css" rel="stylesheet" type="text/css" />
    <link href="<%=basePath %>resource/gnt/advanced/css/style2.css" rel="stylesheet" type="text/css" />
    <link href="<%=basePath %>resource/gnt/advanced/css/style3.css" rel="stylesheet" type="text/css" />
    <link href="<%=basePath %>resource/gnt/css/examples.css" rel="stylesheet" type="text/css" /> 
	<!--Ext lib and UX components-->
    <script src="<%=basePath %>resource/gnt/ext-all.js" type="text/javascript"></script>
	<!--Gantt components-->
    <script src="<%=basePath %>resource/gnt/gnt-all-debug.js" type="text/javascript"></script>
    <script src="<%=basePath %>app/util/BaseUtil.js" type="text/javascript"></script>
     <script src="<%=basePath %>resource/ext/ext-lang-zh_CN.js" type="text/javascript"></script>
    <!--Application files-->
    <script src="<%=basePath %>jsps/plm/projectplan/advanced1.js" type="text/javascript"></script>
    <script type="text/javascript">
    var assignmentEditor='';
    var recorder = '<%=session.getAttribute("em_name")%>';
    var em_id='<%=session.getAttribute("em_uu")%>'
    var recorddate="<%=date%>";
    var basePath="<%=basePath%>";
    var projectplandate='';
    var projectplandata='';
    var caller='ProjectTask';
    var codeField='taskcode';
    var BaseUtil=Ext.create('erp.util.BaseUtil');
    </script>
    <script src="<%=basePath %>resource/i18n/i18n.js" type="text/javascript"></script>
    <title>项目甘特图</title>
</head>
<body>
    <div id="north">
    </div>
</body>
</html>