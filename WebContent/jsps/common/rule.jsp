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
<style type="text/css">
	body{
		background-image: url("<%=basePath%>resource/images/screens/bg_blue.jpg"); 
	}
	h3{
		padding-top: 50px;
	}
	#context{
		padding-top: 20px;
		padding-left: 15%;
		padding-eight: 15%;
	}
	#footer_bg {
		padding: 0px 0px 13px 10px;
		position: absolute;
		bottom: 0;
		left: 50%;
		margin-left: -200px;
		font-size: 12px;
	}
</style>
<script type="text/javascript">
	function toggle(){//进入第二步
		var btn = parent.Ext.ComponentQuery.query('button[step=2]')[0];
		btn.fireEvent('click', btn);
	}
</script>
</head>
<body>
	<h3 align="center">优软ERP数据初始化向导</h3>
	<div id="context" >
			<div>&nbsp;&nbsp;导入ERP的过程，包括静态数据初始化、期初余额初始化（正式上线）、进销存业务处理、期末处理及管理功能。请按下面的要求进行操作：</div>
			<div>
				<b>&nbsp;&nbsp;1.设置公司明细</b><br/>
				......
			</div>
			<div>
				<b>&nbsp;&nbsp;2.公司的一般设置</b>
			</div>	
			<div>
				<b>&nbsp;&nbsp;3.添加用户</b>
			</div>
			<div>
				<b>&nbsp;&nbsp;4.给普通用户赋权</b>
			</div>
			<div>
				<b>&nbsp;&nbsp;5.定义销售员</b>
			</div>
			<div>
				<b>&nbsp;&nbsp;6.单据编号方式</b>
			</div>
			<div>
				<b>&nbsp;&nbsp;7.主数据的录入—会计科目</b>
			</div>
			<div>
				<b>&nbsp;&nbsp;8.业务伙伴—主数据的录入—业务伙伴</b>
			</div>
			<div>
				<b>&nbsp;&nbsp;9.期初余额录入—业务伙伴期初余额</b>
			</div>
	</div>
	<div id="footer_bg" align="center">
		<input type="checkbox" checked="checked"/>我同意<a href="http://www.usoftchina.com" target="_blank">《优软软件使用协议》</a>
		<button onclick="toggle();">下一步&raquo;</button><br><br>
		<font style="color:gray;">版权所有：深圳市优软科技有限公司 &copy; 2012 All Rights Reserved</font>
	</div>
</body>
</html>