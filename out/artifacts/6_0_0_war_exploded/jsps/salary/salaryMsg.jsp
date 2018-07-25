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
<script type="text/javascript"
	src="<%=basePath%>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/i18n/i18n.js"></script>
<script type="text/javascript">
Ext.Loader.setPath('Ext.ux', '../../resource/ux');//设置ux组件路径
Ext.Loader.setConfig({
	enabled : true
});//开启动态加载
Ext.application({
	name : 'erp',//为应用程序起一个名字,相当于命名空间
	appFolder : basePath + 'app',//app文件夹所在路径
	controllers : ['salary.SalaryMsg'],//声明所用到的控制层
	launch : function() {
		Ext.create('erp.view.salary.SalaryMsg');//创建视图
	}
});
var caller = 'SalaryMsg';
var title= getUrlParam('title');

/* //加上ctrl+alt+s键盘事件,自动跳转配置界面
function onKeyDown(){
	if(Ext.isIE){
		document.body.attachEvent('onkeydown', function(){//ie的事件名称不同,也不支持addEventListener
			if(window.event.altKey && window.event.ctrlKey && window.event.keyCode == 80){
				openTable('消息通知配置', "jsps/ma/logic/config.jsp?whoami="+caller);
			}
		});
	} else {
		document.body.addEventListener("keydown", function(e){
			if(Ext.isFF5){//firefox不支持window.event
				e = e || window.event;
			}
			if(e.altKey && e.ctrlKey && e.keyCode == 80){
				openTable('消息通知配置', "jsps/ma/logic/config.jsp?whoami="+caller);
			}
    	});
	}
};
function openTable(title, url){
	var panel = Ext.getCmp('datalist' + caller); 
	var main = parent.Ext.getCmp("content-panel");
	if(!panel){ 
    	panel = { 
    			title : title,
    			tag : 'iframe',
    			tabConfig:{tooltip: title},
    			frame : true,
    			border : false,
    			layout : 'fit',
    			iconCls : 'x-tree-icon-tab-tab',
    			html : '<iframe id="iframe_maindetail_pageSet" src="' + basePath + url + '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>',
    			closable : true,
    			listeners : {
    				close : function(){
    			    	main.setActiveTab(main.getActiveTab().id); 
    				}
    			} 
    	};
		var p = main.add(panel); 
		main.setActiveTab(p);
	}else{ 
    	main.setActiveTab(panel);
	} 
}; */
var caller = 'SalaryMsg';
var title= getUrlParam('title');
</script>
<title>Insert title here</title>
</head>
<body  onload="onKeyDown();">
</body>
</html>