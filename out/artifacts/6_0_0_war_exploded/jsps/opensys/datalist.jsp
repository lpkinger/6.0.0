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
<link rel="stylesheet" href="<%=basePath %>resource/css/opensys.css" type="text/css"></link>
<script type="text/javascript" src="<%=basePath%>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
 <script type="text/javascript" src="<%=basePath %>resource/ux/grid/GridHeaderFilters.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/data/PagingMemoryProxy.js"></script> 
<script type="text/javascript">
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',
    appFolder: basePath+'app',
    controllers: [
        'common.Datalist'
    ],
    launch: function() {
        Ext.create('erp.view.common.datalist.Viewport',{
        	items: [{
  	    	  xtype:'erpDatalistGridPanel',
  	    	  anchor: '100% 100%',
  	    	  dockedItems: [{
				xtype: 'erpDatalistToolbar',
				dock: 'bottom',
				displayInfo: true,
				items: ['-',{
					id:'datalistexport',
					name: 'export',
					tooltip: $I18N.common.button.erpExportButton,
					iconCls: 'x-button-icon-excel',
					cls: 'x-btn-tb',
					width: 24,
					hidden:getUrlParam('_noexport')==-1,
					handler : function(i) {
						var me = i.ownerCt;
						me.exportData(me.ownerCt, i);
					}
				},'-',{
					itemId: 'close',
					tooltip:$I18N.common.button.erpCloseButton,
					iconCls: 'x-button-icon-close',
					width: 24,
					cls: 'x-btn-tb',
					handler: function(){
						var main = parent.Ext.getCmp("content-panel");
						if(main)
							main.getActiveTab().close();
						else if(typeof parentDoc !== 'undefined' && parentDoc) {
							var doc = parent.Ext.getCmp(parentDoc);
							if(doc) {
								doc.fireEvent('close', doc);
							}
						}
					}
				}]
  	         }]
        	}]	
        });
    }
});
	var caller = '';
	var enUU  = '<%=session.getAttribute("enUU")%>';
	var condition = '';
	var page = 1;
	var value = 0;
	var total = 0;
	var dataCount = 0;//结果总数
	var msg = '';
	var height = window.innerHeight;
	if(Ext.isIE){//ie不支持window.innerHeight;document.documentElement.clientHeight == 0
		height = screen.height*0.73;
	};
	var pageSize = parseInt(height*0.8/22);
	var keyField = "";
	var pfField = "";
	var url = "";
	var relative = null;	
    var Contextvalue="";
    var LastValue="";
	//给datalist加上ctrl+alt+s键盘事件,自动跳转datalist配置界面
	function onDatalistKeyDown(){
		if(Ext.isIE){
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
</script>
</head>
<body onload="onDatalistKeyDown()">
</body>
</html>