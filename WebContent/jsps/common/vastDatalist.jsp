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
<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css"/>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/grid/GridHeaderFilters.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/data/PagingMemoryProxy.js"></script>
<style type="text/css">
	.x-btn-gray{
		border:1px solid #bdbdbd !important;
	}
</style>
<script type="text/javascript">
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'common.VastDatalist'
    ],
    launch: function() {
        Ext.create('erp.view.common.vastDatalist.Viewport');
    }
});
	var caller = '';
	var condition = '';
	var page = 1;
	var value = 0;
	var total = 0;
	var dataCount = 0;//结果总数
	var msg = '';
	var height = window.innerHeight;
	var pageSize = parseInt(height*0.7/23);
	var keyField = "";
	var pfField = "";
	var url = "";
	//给datalist加上ctrl+alt+s键盘事件,自动跳转datalist配置界面
	function onDatalistKeyDown(){
		if(Ext.isIE && !Ext.isIE11){
			document.body.attachEvent('onkeydown', function(){//ie的事件名称不同,也不支持addEventListener
				if(window.event.altKey && window.event.ctrlKey && window.event.keyCode == 83){
					openTable('DataList维护(' + caller + ')', "jsps/ma/dataList.jsp?formCondition=dl_callerIS'" + caller + "'" + 
							"&gridCondition=dld_callerIS'" + caller + "'");
				}
			});
		} else {
			document.body.addEventListener("keydown", function(e){
				if(Ext.isFF5){//firefox不支持window.event
					e = e || window.event;
				}
				if(e.altKey && e.ctrlKey && e.keyCode == 83){
					openTable('DataList维护(' + caller + ')', "jsps/ma/dataList.jsp?formCondition=dl_callerIS'" + caller + "'" + 
							"&gridCondition=dld_callerIS'" + caller + "'");
				}			
				
	    	});
		}
	}
</script>
</head>
<body onload="onDatalistKeyDown()">
</body>
</html>