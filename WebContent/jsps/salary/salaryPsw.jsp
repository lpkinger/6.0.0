<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
    <%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<link rel="stylesheet"
	href="<%=basePath%>resource/ext/resources/css/ext-all-gray.css"
	type="text/css"></link>
<style text/css>
.x-button-icon-check{
	background-image: url('<%=basePath%>resource/images/hourglass.png') ;
}
.delete {
	background-image: url('<%=basePath%>resource/images/icon/trash.png');
}
</style>
<script type="text/javascript"
	src="<%=basePath%>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/i18n/i18n.js"></script>
<script type="text/javascript"
	src="<%=basePath%>resource/ux/grid/Export.js"></script>
<script type="text/javascript"
	src="<%=basePath%>resource/ux/RowExpander.js"></script>
<script type="text/javascript"
	src="<%=basePath%>resource/ux/CheckColumn.js"></script>
<script type="text/javascript">
Ext.Loader.setConfig({
	enabled : true
});//开启动态加载
Ext.application({
	name : 'erp',//为应用程序起一个名字,相当于命名空间
	appFolder : basePath + 'app',//app文件夹所在路径
	controllers : ['salary.SalaryPsw'],//声明所用到的控制层
	launch : function() {
		Ext.create('erp.view.salary.SalaryPsw');//创建视图
	}
});
var caller = 'SalaryPwd';
var title= getUrlParam('title');
var mobile = '<%=session.getAttribute("em_mobile")%>';
</script>
<title>Insert title here</title>
</head>
<body>

</body>
</html>