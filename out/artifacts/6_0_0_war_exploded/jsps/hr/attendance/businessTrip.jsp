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
<style>
.MyBtn{
  line-height:30px;
  height:40px;
  width:175px;
  color:#777777;
  background-color:#f1f1f1;
  font-size:16px;
  font-weight:normal;
  font-family:Arial;
  background:-webkit-gradient(linear, left top, left bottom, color-start(0.05, #ededed), color-stop(1, #f5f5f5));
  background:-moz-linear-gradient(top, #ededed 5%, #f5f5f5 100%);
  background:-o-linear-gradient(top, #ededed 5%, #f5f5f5 100%);
  background:-ms-linear-gradient(top, #ededed 5%, #f5f5f5 100%);
  background:linear-gradient(to bottom, #ededed 5%, #f5f5f5 100%);
  background:-webkit-linear-gradient(top, #ededed 5%, #f5f5f5 100%);
  filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#ededed', endColorstr='#f5f5f5',GradientType=0);
  border:1px solid #dcdcdc;
  -webkit-border-top-left-radius:0px;
  -moz-border-radius-topleft:0px;
  border-top-left-radius:0px;
  -webkit-border-top-right-radius:0px;
  -moz-border-radius-topright:0px;
  border-top-right-radius:0px;
  -webkit-border-bottom-left-radius:0px;
  -moz-border-radius-bottomleft:0px;
  border-bottom-left-radius:0px;
  -webkit-border-bottom-right-radius:0px;
  -moz-border-radius-bottomright:0px;
  border-bottom-right-radius:0px;
  -moz-box-shadow: inset 0px 0px 0px 0px #ffffff;
  -webkit-box-shadow: inset 0px 0px 0px 0px #ffffff;
  box-shadow: inset 0px 0px 0px 0px #ffffff;
  text-align:center;
  display:inline-block;
  text-decoration:none;
}
.MyBtn:hover{
  background-color:#f5f5f5;
  background:-webkit-gradient(linear, left top, left bottom, color-start(0.05, #f5f5f5), color-stop(1, #ededed));
  background:-moz-linear-gradient(top, #f5f5f5 5%, #ededed 100%);
  background:-o-linear-gradient(top, #f5f5f5 5%, #ededed 100%);
  background:-ms-linear-gradient(top, #f5f5f5 5%, #ededed 100%);
  background:linear-gradient(to bottom, #f5f5f5 5%, #ededed 100%);
  background:-webkit-linear-gradient(top, #f5f5f5 5%, #ededed 100%);
  filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#f5f5f5', endColorstr='#ededed',GradientType=0);
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
        'hr.attendance.BusinessTrip'
    ],
    launch: function() {
    	Ext.create('erp.view.hr.attendance.BusinessTrip');//创建视图
    }
});
var caller ="FeePlease!CCSQ!new";
caller = caller.replace(/'/g, "");
var formCondition = '';
var gridCondition = '';
</script>
</head>
<body >
</body>
</html>