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
<script type="text/javascript">
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'b2c.common.b2cDatalist'
    ],
    launch: function() {
        Ext.create('erp.view.b2c.common.datalistViewport');
    }
});
	var caller = getUrlParam('whoami');
	var condition = '';
	var parentDoc = getUrlParam('parentDoc');// 对于Ext.window嵌套的datalist.jsp，可以将window的id传入，以便操作
	var page = 1;
	var value = 0;
	var total = 0;
	var dataCount = 0;//结果总数
	var msg = '';
	var height = window.innerHeight;
	if(Ext.isIE){//ie不支持window.innerHeight;document.documentElement.clientHeight == 0
		height = screen.height*0.73;
	}
	var pageSize = parseInt(height*0.7/23);
	var keyField = "";
	var pfField = "";
	var url = "";
	var relative = null;	
    var Contextvalue="";
    var LastValue="";
    var _self=null;
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
	function openTable1(id, caller, title, link, key, detailKey, condition,relateMaster,limit){
		var main = parent.Ext.getCmp("content-panel");
		var item=main.items.items[0];
		var panel = Ext.getCmp('' + id);
		var url = parseUrl(link);
		if(id){
			if(caller){
				panel = Ext.getCmp(caller + id);
				if(url.indexOf('?')>0) {
					url = link + '&whoami=' + caller;
				}
				else url = link + '?whoami=' + caller;
				url = url + '&formCondition=' + key + '=' + id + '&gridCondition=' + detailKey + '=' + id;
			}
		} else {
			if(condition != null){
				url += '&urlcondition=' + parseUrl(condition);
			}
		}
		if(!limit){
	    if(url.indexOf('?') > 0)
			url += '&_noc=1';
		else
			url += '?_noc=1';
		}
		if(relateMaster && relateMaster!='null'){
			url+='&newMaster='+relateMaster;
		}

		if( relateMaster  && relateMaster!='null' ){
			var currentMaster = parent.window.sob;
			if ( currentMaster/*  && currentMaster != relateMaster */) {// 无论账套是否一致都创建临时会话
				if (parent.Ext) {
		    		Ext.Ajax.request({
						url: basePath + 'common/changeMaster.action',
						params: {
							to: relateMaster
						},
						callback: function(opt, s, r) {
							if (s) {
								url+='&_center=1';
								var localJson = new Ext.decode(r.responseText);
								var win = parent.Ext.create('Ext.Window', {
					    			width: '100%',
					    			height: '100%',
					    			draggable: false,
					    			closable: false,
					    			modal: true,
					    			title: '创建到账套 ' + localJson.currentMaster + ' 的临时会话',
					    			id:'modalwindow',
					    			historyMaster:currentMaster,
					    			relateMaster:relateMaster,
					    			html : '<iframe src="' + url + '" height="100%" width="100%" frameborder="0" style="border-width: 0px;padding: 0px;" scrolling="auto"></iframe>',
					    			buttonAlign: 'center',
					    			buttons: [{
										text: $I18N.common.button.erpCloseButton,
										cls: 'x-btn-blue',
										id: 'close',
										handler: function(b) {
											Ext.Ajax.request({
												url: basePath + 'common/changeMaster.action',
												params: {
													to: currentMaster
												},
												callback: function(opt, s, r) {
													if (s) {
														b.up('window').close();
													} else {
														alert('切换到原账套失败!');
													}
												}
											});
										}
									}]
					    		});
								win.show();
							} else {
								alert('无法创建到账套' + relateMaster + '的临时会话!');
							}
						}
					});
		    	}
				return;
			}
		}
		if(!panel){ 
			panel = { 
					title : title.substring(0, title.toString().length > 5 ? 5 : title.toString().length),
					tag : 'iframe',
					tabConfig:{tooltip: title},
					border : false,
					layout : 'fit',
					iconCls : 'x-tree-icon-tab-tab',
					html : '<iframe id="iframe" src="' + basePath + url + '" height="100%" width="100%" frameborder="0" scrolling="auto"></iframe>',
					closable : true,
					listeners : {
						close : function(){
							main.setActiveTab(main.getActiveTab().id); 
						}
					} 
			};
			openTab(panel, panel.id);
		}else{ 
			main.setActiveTab(panel); 
		} 
	}
	function parseUrl(url) {
		var id = url.substring(url.lastIndexOf('?')+1);
		if (id == null) {
			id = url.substring(0,url.lastIndexOf('.'));
		}
		if(contains(url, 'session:em_uu', true)){
			url = url.replace(/session:em_uu/,em_uu);
		}
		if(contains(url, 'session:em_code', true)){
			url = url.replace(/session:em_code/, "'" + em_code + "'");
		}
		if(contains(url, 'sysdate', true)){
			url = url.replace(/sysdate/, "to_date('" + Ext.Date.toString(new Date()) + "','yyyy-mm-dd')");
		}
		if(contains(url, 'session:em_name', true)){
			url = url.replace(/session:em_name/,"'"+em_name+"'" );
		}
		return url;
	}
	function openTab(panel, id){ 
		var o = (typeof panel == "string" ? panel : id || panel.id); 
		var main = parent.Ext.getCmp("content-panel");
		var tab = main.getComponent(o); 
		if (tab) { 
			main.setActiveTab(tab); 
		} else if(typeof panel!="string"){ 
			panel.id = o; 
			var p = main.add(panel); 
			main.setActiveTab(p); 
		} 
	}
</script>
</head>
<body>

</body>
</html>