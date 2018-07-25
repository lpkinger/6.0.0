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
        'plm.request.ProjectRequest'
    ],
    launch: function() {
    	Ext.create('erp.view.plm.request.ProjectRequest');//创建视图
    }
});
var caller = getUrlParam('whoami');
if(!caller){
	caller = 'ProjectRequest';
}
var formCondition = getUrlParam('formCondition');
var gridCondition = '';


function openFormUrl(value, keyField, url, title){
	if(url.indexOf("?")>0){
		url =url+'&formCondition='+keyField+"='"+value+"'";
	}else 
	url =url+'?formCondition='+keyField+"='"+value+"'";
	var panel = Ext.getCmp(keyField + "=" + value); 
	var main = parent.Ext.getCmp("content-panel");
	var showtitle='';
	url = url.replace(/IS/g, "=\'").replace(/&/g, "\'&");
	if(!panel){ 
    	if (title && title.toString().length>4) {
    		showtitle = title.toString().substring(0,4);	
    	}else {
    		showtitle=title;
    	}
    	panel = { 
    			title : showtitle,
    			tag : 'iframe',
    			tabConfig:{tooltip:title.toString() + '(' + keyField + "=" + value + ')'},
    			frame : true,
    			border : false,
    			layout : 'fit',
    			iconCls : 'x-tree-icon-tab-tab',
    			html : '<iframe src="' + basePath + url + '" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>',
    			closable : true,
    			listeners : {
    				close : function(){
    			    	main.setActiveTab(main.getActiveTab().id); 
    				}
    			} 
    	};
		openTab(panel, keyField + "=" + value);
	}else{ 
    	main.setActiveTab(panel); 
	} 
}
</script>
</head>
<body>
</body>
</html>