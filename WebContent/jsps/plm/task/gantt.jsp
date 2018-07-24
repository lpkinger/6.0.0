<%@ page language="java" import="java.util.*,java.text.SimpleDateFormat"
	contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	String date = format.format(new Date());
%>
<!DOCTYPE html>
<html>
<head>
<!--Ext and ux styles -->
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%-- <link href="<%=basePath%>resource/gnt/resources/css/ext-all-gray.css"
	rel="stylesheet" type="text/css" /> --%>
<%--   <link href="<%=basePath%>resource/gnt/css/sch-gantt-all.css"
	rel="stylesheet" type="text/css" /> --%>
<link href="<%=basePath%>resource/ext/4.2/resources/ext-theme-gray/ext-theme-gray-all.css"
	rel="stylesheet" type="text/css" />
 <link href="<%=basePath%>resource/gnt/2.9/sch-gantt-all.css"
	rel="stylesheet" type="text/css" />
 <link href="<%=basePath%>resource/gnt/advanced/advanced.css"
	rel="stylesheet" type="text/css" />
 <link href="<%=basePath%>resource/gnt/2.9/gantt.css"
	rel="stylesheet" type="text/css" />  

<script src="<%=basePath%>resource/ext/4.2/ext-all-debug.js"
	type="text/javascript"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/grid/GridHeaderFilters.js"></script>
<script src="<%=basePath%>resource/gnt/2.9/gnt-all-debug.js"
	type="text/javascript"></script>	
<script src="<%=basePath%>app/util/BaseUtil.js" type="text/javascript"></script>
<script src="<%=basePath%>resource/ext/ext-lang-zh_CN.js"
	type="text/javascript"></script>
<script src="<%=basePath%>resource/i18n/i18n.js" type="text/javascript"></script>
<!--Application files-->
<script type="text/javascript">
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'plm.task.gantt'
    ],
    launch: function() {
    	Ext.create('erp.view.plm.task.gantt');//创建视图
    }
});
    var assignmentEditor='';
    var recorder = '<%=session.getAttribute("em_name")%>';
    var em_id='<%=session.getAttribute("em_uu")%>'
    var recorddate="<%=date%>";
    var basePath="<%=basePath%>";
	var projectplandate = '';
	var projectplandata = '';
	var caller = 'ProjectTask';
	var codeField = 'taskcode';
	var BaseUtil = Ext.create('erp.util.BaseUtil');
	var formCondition = getUrlParam('formCondition');
	var Live = getUrlParam('Live');
	formCondition=formCondition.replace('IS','=');
	var prjplanid=formCondition.split('=')[1];
	var hideToolBar = getUrlParam('hideToolBar');
	hideToolBar = hideToolBar==='true';//传过来的为字符串，做一下转换
</script>
<style type="text/css">
 .x-btn-gray {
	background: #d5d5d5;
	background: -moz-linear-gradient(top, #fff 0, #efefef 38%, #d5d5d5 88%);
	background: -webkit-gradient(linear, left top, left bottom, color-stop(0%, #fff),
		color-stop(38%, #efefef), color-stop(88%, #d5d5d5));
	filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#fff',
		endColorstr='#d5d5d5', GradientType=0);
	border-color: #bfbfbf;
	border-radius: 2px;
	vertical-align: bottom;
	text-align: center;
}
.x-button-icon-close {
    padding-top: 20px;
	background-image: url('<%=basePath%>/resource/images/icon/trash.png')
}
.x-nbutton-icon-log{
	background-image:url('<%=basePath%>/resource/images/icon/caozuo.png')
}
.x-grid-cell{
	height:25px;
	text-align:center;
	font-size:12px;
}
.x-button-search{
	background-image: url('<%=basePath%>resource/images/btn-search.jpg')
}
</style>
</head>
<title>项目甘特图</title>
<body>
	<div id="north"></div>
</body>
</html>