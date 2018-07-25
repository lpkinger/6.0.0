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
<style>
.custom-grid-autoheight .x-grid-row, .custom-grid-autoheight .x-grid-row .x-grid-cell,.custom-grid-autoheight .x-grid-cell-inner {
	height: auto !important;
	white-space: normal;
}
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/grid/GridHeaderFilters.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/data/PagingMemoryProxy.js"></script>
<script type="text/javascript">
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载

	var key = getUrlParam('key');
	var keyValue = getUrlParam('keyValue');
	if(Ext.isEmpty(keyValue)) {
		keyValue = '';
	}	
	if(keyValue == 0 || keyValue == '0') {
		keyValue = '';
	} 
	var caller = '';
	var keyField = '';
	var dbkind=getUrlParam('dbkind'), dbOrderby = getUrlParam('ob');
	var condition ="1=1";
	var dbfind = getUrlParam('dbfind');
	var which = 'form';
	var triggerId = getUrlParam('trigger');
	var trigger = parent.Ext.getCmp(triggerId);
	var dbCondition = getUrlParam('dbCondition');
	var dbBaseCondition = getUrlParam('dbBaseCondition');
	var dbGridCondition = getUrlParam('dbGridCondition');

	var likecondition= dbkind || Ext.isEmpty(keyValue) ? null :getLikeCondition(key , keyValue);//'upper(' +  key + ") like '%" + keyValue.replace(/\'/g,"''")   + "%'";
	if(!trigger || trigger.column || !trigger.ownerCt || trigger.hidden){//如果是grid的dbfind
		which = 'grid';
		dbfind=decodeURIComponent(dbfind);
		caller = dbfind.split('|')[0];
		keyField = dbfind.split('|')[1];
		likecondition=Ext.isEmpty(keyValue)?null:getLikeCondition(keyField , keyValue);
		if(dbGridCondition){
			condition=condition+" AND "+ decodeURIComponent(dbGridCondition).replace(/\s{1}IS\s{1}/g, '=');
		}
	} else {
		caller = getUrlParam('caller');
	}
	if(dbCondition){
		condition=condition+" AND "+ decodeURIComponent(dbCondition).replace(/\s{1}IS\s{1}/g, '=');
	}
	if(dbBaseCondition){
		condition=condition+" AND "+decodeURIComponent(dbBaseCondition).replace(/\s{1}IS\s{1}/g, '=').replace(/@/g,'%');
	}
	var page = 1;
	var value = 0;
	var total = 0;
	var dataCount = 0;
	var msg = '';
	var height = window.innerHeight;
	if(Ext.isIE){//ie不支持window.innerHeight;document.documentElement.clientHeight == 0
		height = screen.height*0.6;
	}
	var pageSize = parseInt(height*0.7/24);
	var dbfinds = [];
	Ext.application({
	    name: 'erp',//为应用程序起一个名字,相当于命名空间
	    appFolder: basePath+'app',//app文件夹所在路径
	    controllers: [//声明所用到的控制层
	        'common.Dbfind'
	    ],
	    launch: function() {
	        Ext.create('erp.view.common.dbfind.Viewport');
	    }
	});
	//加上ctrl+alt+s键盘事件,自动跳转配置界面
	function onKeyDown(){
		if(Ext.isIE){
			document.body.attachEvent('onkeydown', function(){//ie的事件名称不同,也不支持addEventListener
				if(window.event.altKey && window.event.ctrlKey && window.event.keyCode == 83){
					openTable('Dbfind维护(' + caller + ')', "jsps/ma/dbFindSet.jsp?formCondition=ds_callerIS'" + caller + "'" + 
							"&gridCondition=dd_callerIS'" + caller + "'");
				}
			});
		} else {
			document.body.addEventListener("keydown", function(e){
				if(Ext.isFF5){//firefox不支持window.event
					e = e || window.event;
				}
				if(e.altKey && e.ctrlKey && e.keyCode == 83){
					openTable('Dbfind维护(' + caller + ')', "jsps/ma/dbFindSet.jsp?formCondition=ds_callerIS'" + caller + "'" + 
							"&gridCondition=dd_callerIS'" + caller + "'");
				}
	    	});
		}
	}
	function openTable(title, url){
		var panel = Ext.getCmp('datalist' + caller); 
		var main = parent.Ext.getCmp("content-panel");
		if(!panel){ 
	    	panel = { 
	    			title : caller,
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
	}
	
 	function getLikeCondition(keyField,keyValue){
 		 if (trigger.isFast){
 		    return keyField + " like '" + keyValue.replace(/\'/g,"''")  + "%'";
 		 }else 
 			return 'upper(' + keyField + ") like '%" + keyValue.toUpperCase().replace(/\'/g,"''")  + "%'";
 	}
 	
</script>
</head>
<body onload="onKeyDown();">
</body>
</html>