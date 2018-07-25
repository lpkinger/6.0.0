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
	 <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="<%=basePath %>resource/gnt/resources/css/ext-all-gray.css" rel="stylesheet" type="text/css" />
    <link href="<%=basePath %>resource/gnt/css/sch-gantt-all.css" rel="stylesheet" type="text/css" />
    <link href="<%=basePath %>resource/gnt/css/examples.css" rel="stylesheet" type="text/css" /> 
    <script src="<%=basePath %>resource/gnt/ext-all.js" type="text/javascript"></script>
    <script src="<%=basePath %>resource/gnt/sch-all-debug.js" type="text/javascript"></script>
    <script src="<%=basePath %>app/util/BaseUtil.js" type="text/javascript"></script>
    <script src="<%=basePath %>resource/i18n/i18n.js" type="text/javascript"></script>
    <script type="text/javascript">	
        Ext.Loader.setConfig({
	         enabled: true
         });//开启动态加载
        Ext.application({
             name: 'erp',//为应用程序起一个名字,相当于命名空间
             appFolder: basePath+'app',//app文件夹所在路径
             controllers: [//声明所用到的控制层
               'oa.SchedulerResource'
             ],
        launch: function() {
    	   Ext.create('erp.view.oa.SchedulerResource');//创建视图
        }
     });
    var caller = getUrlParam('caller');
    var triggerId = getUrlParam('trigger');
	var trigger = parent.Ext.getCmp(triggerId);
</script>
</head>
<body>
    <div id="north">
    </div>
</body>
</html>