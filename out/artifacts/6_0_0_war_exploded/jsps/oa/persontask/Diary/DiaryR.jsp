<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css" type="text/css"></link>
<!--[if IE]>
	<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-ie-scoped.css" type="text/css"></link>
<![endif]-->
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<style type="text/css">
body{
	background-image: url("<%=basePath %>resource/images/screens/note.jpg");
	background-repeat: no-repeat;
	background-position:center top;
	height: 1000;
	
}
#thoughts{
	background-image: url("<%=basePath %>resource/images/screens/diary.png");
	background-repeat: no-repeat;
	background-position:right top;
	height: 150;
	color: #8B0A50;
	font-size: 18px;
	font-family: 隶书;
}
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">	
/*日记界面*/
Ext.onReady(function(){
	Ext.Ajax.request({
   		url : basePath + 'common/singleFormItems.action',
   		params: {
   			caller: 'Diary',
   			condition: getUrlParam('formCondition').replace(/IS/g, '=')
   		},
   		method : 'post',
   		callback : function(options,success,response){
   			var localJson = new Ext.decode(response.responseText);
			if(localJson.data){
				var diary = Ext.decode(localJson.data);
				if(diary != null){
					Ext.get('date').insertHtml('afterBegin', diary.di_time);
					Ext.get('weather').insertHtml('afterBegin',diary.di_weather);
					Ext.get('name').insertHtml('afterBegin', diary.di_name);
					Ext.get('thoughts').insertHtml('afterBegin', diary.di_thoughts);					
				}
			} else {
				if(localJson.exceptionInfo){
					showError(localJson.exceptionInfo);
					window.location.href = basePath + 'jsps/error/e-500.jsp';
	   			}
			}
   		}
	});
});
</script>
</head>
<body >
	<center><div class="main" align="center">
		<font color=#999;><span id="date"></span></font>&nbsp;&nbsp;&nbsp;
		天气:<font color=#999;><span id="weather"></span></font>&nbsp;&nbsp;&nbsp;
		姓名:<font color=#999;><span id="name"></span></font>&nbsp;&nbsp;&nbsp;	<hr>
		<div id="thoughts"></div>
	</div></center>
</body>
</html>