<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css" type="text/css"/>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"/>
<link rel="stylesheet" href="<%=basePath %>resource/css/tree.css" type="text/css"/>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">	
/**
 * 压力测试
 * 1.选择测试任务
 * 2.测试页面、测试人员、时间
 * 3.选择压力指数(5,10,20,50,200,500,1000)
 * 4.选择测试项(保存、提交、删除、审核、过账...)
 * 5.生成模拟数据
 * 6.进行测试
 * 7.生成测试结果.继续测试...
 * 8.生成并完善测试报告，提交、导出测试报告
 * 9.清除模拟的测试数据
 */
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'plm.test.Pressure'
    ],
    launch: function() {
    	Ext.create('erp.view.plm.test.Pressure');//创建视图
    }
});
var caller = 'Pressure';
</script>
</head>
<body >
</body>
</html>