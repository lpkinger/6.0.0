<%@page import="com.uas.erp.model.Enterprise"%>
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
<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css" type="text/css"></link>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<style type="text/css">
#uas{
	height: 100%;
	background-image: url("<%=basePath%>resource/images/screens/bg_blue.jpg");
}
#uas .context{
	padding: 60px 15px 0 200px;
}
.addmore{
	color: gray;
	float: right;
}
ul li a{
	color:blue; text-decoration:none; display:block; margin-left: 10px; list-style-type: disc;
}
ul li a:hover{
	color:#35355D;
}
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'common.UU'
    ],
    launch: function() {
    	Ext.create('erp.view.common.init.UU');//创建视图
    }
});
<%
Object obj = session.getAttribute("enterprise");
if(obj != null) {
	Enterprise enterprise = (Enterprise)obj;
}
%>
var 
</script>
</head>
<body >
 <div id="uas" >
 <div class="context">
 	<center><h3>UAS B2B商务管理平台</h3></center><br>
      <p>&nbsp;&nbsp;&nbsp;&nbsp;优软商务管理平台为第三方经营的B2B平台，该平台协助购销企业双方进行企业信息管理，
      并为企业提供良好的信息交流平台。目前，该平台信息管理主要涵盖了基础资料、销售系统、采购系统、品质系统、财务系统、
      企业空间信息和系统问题反馈等7大板块。该平台多个板块的集成使得企业发布和获取相关信息更加全面、及时和便捷，
      为企业获取客户以及争取利益最大化提供广阔的渠道。通过该平台，企业可以打破地域限制，结识众多志同道合的网商，共同开启发财之门。</p>
<div class="addmore">更多精彩应用不断添加中...</div><br>
<ul>
	<li><a href="http://www.usoftchina.com/usoft/rege.html" target="_blank">.注册企业UU</a></li>
	<li><a href="http://www.usoftchina.com/usoft/perRegisterCh.html" target="_blank">.注册个人UU</a></li>
	<li><a href="http://www.usoftchina.com" target="_blank">.联系客服</a></li>
</ul>
</div>
</div>
</body>
</html>