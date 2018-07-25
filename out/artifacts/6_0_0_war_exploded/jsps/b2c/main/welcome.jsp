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
<link rel="stylesheet"	href="<%=basePath%>resource/ext/4.2/resources/ext-theme-gray/ext-theme-gray-all.css"type="text/css"></link>
<style type="text/css">
*{
	border-corlor:#ededed;
}
.addToTree.x-tree-arrows .x-tree-elbow-plus, 
.x-tree-arrows .x-tree-elbow-minus, 
.x-tree-arrows .x-tree-elbow-end-plus,
.x-tree-arrows .x-tree-elbow-end-minus {
    background-image: url('#');
}
.x-grid-row-before-selected .x-grid-td {
    border-bottom-style: none;
}
.x-grid-row-selected .x-grid-cell
	{
	border-style:  none !important; 
	background-color:   #e0e0e0 !important; 
	color: blue;
	font-weight: normal
}

.default-panel{
	background:-webkit-gradient(linear, 0 0, 0 bottom, from(#FEFEFE), to(#E5E5E5)); 
}
.x-window-body-default{
border: none;
 }
.x-panel-default-framed{
	padding:0px !important;
}
.next,.prev,.end{
	position:absolute !important;
	top: 260px !important;
}
#w1{
	background: url("<%=basePath %>jsps/b2c/main/images/1.welcome-prosources.png") center no-repeat;
}
#w2{
	background: url("<%=basePath %>jsps/b2c/main/images/2.welcome-proShowOrNot.png") center no-repeat;
}
#w3{
	background: url("<%=basePath %>jsps/b2c/main/images/3.welcome-Trans.png") center no-repeat;
}
#w4{
	background: url("<%=basePath %>jsps/b2c/main/images/4.welcome-accounts.png") center no-repeat;
}
#w5{
	background: url("<%=basePath %>jsps/b2c/main/images/5.welcome-shop-decoration.png") center no-repeat;
}
.helpImage{
	width:100%;
	height:100%;
    background-size: 100% 86.5% !important;
    background-color: #e6e6e6 !important;
}
.prev{
	 background: url("<%=basePath %>jsps/b2c/main/images/left.png") center no-repeat; 
}
.next{
	 background: url("<%=basePath %>jsps/b2c/main/images/right.png") center no-repeat; 
}
.end{
	background: url("<%=basePath %>jsps/b2c/main/images/end.png") center no-repeat;
} 
.x-panel-body-default-framed,.x-panel-default-framed{
    background-color: #e8e8e8 !important;
}
.x-panel-body{
	background-color: #e8e8e8 !important;
    border-color: #e8e8e8 !important;
    border:none !important;
}
.welcomenew{
    width: 80px;
    height: 28px;
    text-align: center;
    line-height: 22px;
    float: left;
    letter-spacing: 1px;
    color: #333;
    font-size: 18px;
    font-weight: 600;
    border-bottom: 3px solid !important;
    border-bottom-color: #333;
    margin: 9px 0 10px 0;
    cursor: default;
}
.welcome-colse{
    width: 35px !important;
    height: 35px !important;
    position: absolute;
    right: 15px;
    top: 55px;
    cursor: pointer;
    background: url(<%=basePath %>/jsps/b2c/main/images/w-close.png) center no-repeat;
    background-size: 35px 35px;
}
</style>
<script type="text/javascript"	src="<%=basePath%>resource/ext/4.2/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'b2c.main.Welcome'
    ],
    launch: function() {
    	Ext.create('erp.view.b2c.main.Welcome');//创建视图
    }
});
var MAXCARD = 5;
var TITLE = "新手指引";
</script>
</head>
<body >
</body>
</html>