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
<link rel="stylesheet" type="text/css"
	href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css" />
<link rel="stylesheet" href="<%=basePath %>resource/css/upgrade/bluegray/homePage.css"
	type="text/css"></link>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css"
	type="text/css"></link>
<style type="text/css">

font {
	font-size: 14px;
}
ul li a {
	font-size: 13.5px;
	text-decoration: none;
}
.x-panel-body-default {
	/* background: #f1f2f5; */
	border-left: none !important;
	border-top: none !important;
	border-bottom: none !important;
}
.bench {
	background: url('<%=basePath %>resource/images/background_1.jpg');
}
.mydiv {
	height: 100%;
	background: #f1f1f1;
}
.home-div {
	height: 100%;
	background: url('<%=basePath %>resource/images/background_2.jpg');
}

.home-img {
	cursor: pointer;
}

.home-img:hover {
	background: url('<%=basePath %>resource/images/background_2.jpg');
}

.home-img span {
	font-size: 1;
	color: red;
}

.news,.notify {
	height: 100%;
	background: url('<%=basePath %>resource/images/background_1.jpg');
}

#bench_news,#bench_notify {
	width: 100%;
}

.schedule {
	background: url('<%=basePath %>resource/images/background_1.jpg');
}

.news ul {
	list-style: none;
}

.div-more a {
	color: #CD9B9B;
}

.subscription {
	height: 100%;
	background: url('<%=basePath %>resource/images/background_1.jpg');
}

.div-link {
	padding-left: 10px;
	margin-top: 3px;
}

a:hover {
	color: #8B8B00;
}

.btn-title {
	font-size: 13px;
	color: blue;
	margin-bottom: 0px;
}

.btn-title:hover {
	color: red;
}

.news-img {
	cursor: pointer;
	margin-left: 5px;
}

.news-img:hover {
	background: transparent;
	background-color: #D6BEF9;
	height: 38px;
	width: 38px;
}

.div-left {
	display: inline;
	float: left;
}

.div-right {
	display: inline;
	float: right;
	font-family: sans-serif;
}

.div-right a {
	color: blue;
	text-decoration: none;
	font-size: 13px;
}

.div-right a:hover {
	color: red;
	cursor: pointer;
	font-weight: bold;
}

.div-left font {
	float: left;
	color: green;
	font-size: 13px;
	display: inline;
}

.x-panel-header-text {
	font-size: 13px;
	color: #09a471;
	font-weight: normal;
}

.x-panel-header-text-default-framed {
	font-size: 13px;
	color: #09a471;
	font-weight: normal;
}

#bench .x-panel-header .x-box-inner .x-box-item {
	display: inline;
	height: 18px;
}

.number {
	height: 24px;
	width: 24px;
	background: url('<%=basePath %>resource/images/number/number.png');
	margin-top: -30px;
}

.custom-grid .x-grid-row .x-grid-row-alt {
	height: 20px;
}

.custom-grid .x-grid-row .x-grid-cell {
	height: 20px;
	line-height: 20px;
	vertical-align: top;
}

.custom-grid .x-grid-row .x-grid-cell-inner {
	height: 20px;
	line-height: 20px;
	vertical-align: top;
}

.custom-grid a {
	text-decoration: none;
}

.custom-grid a:hover {
	font-weight: 800;
}

.custom-grid .x-action-col-icon {
	margin: 0px 5px 0px 5px;
	cursor: pointer;
}

.x-grid-cell-topic b {
	display: block;
}

.x-grid-cell-topic a {
	text-decoration: none;
}

.x-grid-cell-topic a:hover {
	text-decoration: underline;
}

.x-grid-cell-topic .x-grid-cell-innerf {
	padding: 5px;
}
.list-default>li {
	padding: 2px 5px;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}
.custom-framed{
    padding: 0px;
    height: 10px;
}
.x-grid-header-simple{
    border:none;
   }
#app-header {
    color: #596F8F;
    font-size: 22px;
    font-weight: 200;
    padding: 8px 15px;
    text-shadow: 0 1px 0 #fff;
}
#app-msg {
    background: #D1DDEF;
    border: 1px solid #ACC3E4;
    padding: 3px 15px;
    font-weight: bold;
    font-size: 13px;
    position: absolute;
    right: 0;
    top: 0;
}
.x-panel-ghost {
    z-index: 1;
}
.x-portal-body {
    padding: 0 8px 0 8px;
    overflow-y:auto;
    overflow-x:hidden !important;
    background-color: #f7f7f7 !important;
}
.x-portal .x-portal-column {
    padding: 8px 8px 0 0;
}
.x-portlet {
    padding: 0px;
    border-width: 1px;
    border-style: solid;
}
.x-portlet .x-panel-body {
    background: #fff;
}
.portlet-content {
    padding: 10px;
    font-size: 11px;
}

#app-options .portlet-content {
    padding: 5px;
    font-size: 12px;
}
.settings {
    background-image:url(../shared/icons/fam/folder_wrench.png);
}
.nav {
    background-image:url(../shared/icons/fam/folder_go.png);
}
.info {
    background-image:url(../shared/icons/fam/information.png);
}
 .x-grid-cell-topic b {
            display: block;
        }
        .x-grid-cell-topic .x-grid-cell-inner {
            white-space: normal;
        }
        .x-grid-row .x-grid-cell a {
           // color: #385F95;
            text-decoration: none;
        }
        .x-grid-row .x-grid-cell a:hover {
           text-decoration: none;
        }
		.x-grid-cell-topic .x-grid-cell-innerf {
			padding: 5px;
		}
.task-portal .x-grid-row{
	vertical-align:middle;
}

.readbutton{
	background: #3384FF !important;
	font-weight:bold !important;
	text-align: center;
	color: white !important;	
}
.x-window-body-default{
	background-color: white;
}
.x-btn-default-small .x-btn-inner .x-btn-default-toolbar-small {
	color: white !important;
}
.x-btn-default-toolbar-small .x-btn-inner{
	color: white !important;
}
.x-form-display-field {
    font-size: 13px;
    color: black;
}
#paneldetail-body{
	background-color: white
}
.Wincontext {
    background-color: white;
    padding-bottom: 5px;
    margin-left: 6px !important;
    left: 0px !important;
}
.Windetail{
	background-color: white;
}
</style>
<script type="text/javascript"
	src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript"
	src="<%=basePath %>resource/ux/PreviewPlugin.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">
var hasReminded ='<%=session.getAttribute("hasReminded")%>';
var WHeight=window.innerHeight;
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'common.DeskTop'
    ],
    launch: function() {
        Ext.create('erp.view.common.DeskTop.ViewPort');
    }
});
var em_id ='<%=session.getAttribute("em_id")%>';
var em_defaulthsid = '<%=session.getAttribute("em_defaulthsid")%>';
var em_type = '<%=session.getAttribute("em_type")%>';

function openTmpUrl(parent,url, newMaster ,winId){
	var window = Ext.getCmp('infoRemindwindow');
	var readStatus = Ext.getCmp('readGrid').readStatusData;
	/* var Grid;
	if(title=='已读'){
		console.log('已读');
		Grid = Ext.getCmp('readGrid');
	}else{
		console.log('未读');
		Grid = Ext.getCmp('unreadGrid');
	} */
	
	if(readStatus){
		Grid = Ext.getCmp('readGrid');
	}else{
		Grid = Ext.getCmp('unreadGrid');
	}
	var toMaster=Grid.currentmaster;
	if(parent){
		openUrl(url);
	}else{
		if(toMaster){
			openMessageUrl(url,'',winId,toMaster)
		}else{
			openUrl(url);
		}
	}
	if(window){
		window.close();
	}
 	if(Grid.readStatusData){
 		Ext.getCmp('inforemindportal').updateReadstatus(Grid.readStatusData,Grid);
	}
 	Grid.readStatusData = null;
 	Grid.currentmaster = null;
}

function openTmpMessageUrl(url,appurl,panelId,newMaster){
	var window = Ext.getCmp('infoRemindwindow');
	var readStatus = Ext.getCmp('readGrid').readStatusData;
	if(readStatus){
		Grid = Ext.getCmp('readGrid');
	}else{
		Grid = Ext.getCmp('unreadGrid');
	}
	var toMaster=Grid.currentmaster;
	if(toMaster){
		openMessageUrl(url,appurl,panelId,toMaster)
	}else{
		openUrl(url);
	}
	if(window){
		window.close();
	}
	if(Grid.readStatusData){
		Ext.getCmp('inforemindportal').updateReadstatus(Grid.readStatusData,Grid);
	}
	Grid.readStatusData = null;
 	Grid.currentmaster = null;
}




function openTable(id, caller, title, link, key, detailKey, condition,relateMaster,limit){	
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
				    			//反馈编号  2018050391
				    			/* buttons: [{
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
								}] */
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
function parseUrl(url){
    var id = url.substring(url.lastIndexOf('?') + 1); //将作为新tab的id
    if (id == null) {
        id = url.substring(0, url.lastIndexOf('.'));
    }
    if (contains(url, 'session:em_uu', true)) { //对url中session值的处理
        url = url.replace(/session:em_uu/g, em_uu);
    }
    if (contains(url, 'session:em_code', true)) { //对url中em_code值的处理
        url = url.replace(/session:em_code/g, "'" + em_code + "'");
    }
    if (contains(url, 'sysdate', true)) { //对url中系统时间sysdate的处理
        url = url.replace(/sysdate/g, "to_date('" + Ext.Date.toString(new Date()) + "','yyyy-mm-dd')");
    }
    if (contains(url, 'session:em_name', true)) {
        url = url.replace(/session:em_name/g, "'" + em_name + "'");
    }
    if (contains(url, 'session:em_type', true)) {
        url = url.replace(/session:em_type/g, "'" + em_type + "'");
    }
    if (contains(url, 'session:em_id', true)) {
        url = url.replace(/session:em_id/g,em_id);
    }
    if (contains(url, 'session:em_depart', true)) {
        url = url.replace(/session:em_depart/g,em_id);
    }
    if (contains(url, 'session:em_defaulthsid', true)) {
        url = url.replace(/session:em_defaulthsid/g,em_defaulthsid);
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

function showWin(numId,mainId,insId,title,id){ 
	var url='common/charts/mobileCharts.action?numId='+numId+'&mainId='+mainId+'&insId='+insId+'&title='+title;
	if (Ext.getCmp('chwin')) {
		Ext.getCmp('chwin').setTitle(title);
		Ext.getCmp('chwin').insId=insId;
		Ext.getCmp('chwin').body.update('<iframe id="iframech" src="'+basePath+url+'" height="100%" width="100%" frameborder="0" scrolling="auto"  ></iframe>');
		}
	else {var chwin = new Ext.window.Window({
	   id : 'chwin',
	   title: title,
	   height: "100%",
	   width: "80%",
	   insId:insId,	   
	   maximizable : true,
	   resizable:false,
	   modal:true,
	   buttonAlign : 'center',
	   layout : 'anchor',
	   listeners:{
		   afterrender: function(th) {
			   th.on('resize', function(){
				   var iframe = document.getElementById('iframech');
				   var src = iframe.src;
				   if(src.indexOf('time_')>=0){
					   iframe.src=src.substring(0,src.indexOf('time_')-1)+'&time_='+new Date().getTime();
				   }else{
					   iframe.src=iframe.src+'&time_='+new Date().getTime();
				   }
			   })
		   }
	   },
	   items: [{
		   tag : 'iframe',
		   frame : true,
		   anchor : '100% 100%',
		   layout : 'fit',
		   html : '<iframe id="iframech" src="'+basePath+url+'" height="100%" width="100%" frameborder="0" scrolling="auto"  ></iframe>'
	   }],
	   buttons : [{
		   text : '上一条',		  
		   cls: 'x-btn-gray',
		   handler : function(btn){
			   prev(btn,id,btn.ownerCt.ownerCt.insId);
		   }
	   },{
		   text : '下一条',	
		   cls: 'x-btn-gray',
		   handler : function(btn){
			   next(btn,id,btn.ownerCt.ownerCt.insId);
		   }
	   },{
		   text : '关  闭',
		   iconCls: 'x-button-icon-close',
		   cls: 'x-btn-gray',
		   handler : function(){
			   Ext.getCmp('chwin').close();
			   Ext.getCmp('subsportal').items.items[0].activeTab.getStore().load();
			   
			 
		   }
	   
	   }]
   });

	chwin.on('close',function(btn){
		 Ext.getCmp('subsportal').items.items[0].activeTab.getStore().load();

	});
	   
	chwin.show();}}

function prev(btn,tabId,insId,index){
	//递归查找下一条，并取到数据
	var grid=Ext.getCmp(tabId);
	var record =index?grid.store.getAt(index):grid.store.findRecord('ID_', insId, 0, false, false, true);
    var fIndex=index||record.index;
    if(fIndex-1 >=0){
    	var d = grid.store.getAt(fIndex - 1);
    	if(d){
    		if(d.data['ID_']==insId){       		
    			prev(btn,tabId,insId,d.index);//过滤因合计数据重复显示的记录
    			}
    		else {showWin(d.data['NUM_ID_'],d.data['INSTANCE_ID_'],d.data['ID_'],d.data['TITLE_'],tabId);}
    	}}
    else alert('暂无上一条数据');//btn.setDisabled(true);
}
function next(btn,tabId,insId,index){
	//递归查找下一条，并取到数据
	var grid=Ext.getCmp(tabId);
	var record =index?grid.store.getAt(index):grid.store.findRecord('ID_', insId, 0, false, false, true);
    var fIndex=index||record.index;
    if(fIndex+1 < grid.store.data.items.length){
    	var d = grid.store.getAt(fIndex + 1);
    	if(d){
    		if(d.data['ID_']==insId){       		
    			next(btn,tabId,insId,d.index);
    			}
    		else {showWin(d.data['NUM_ID_'],d.data['INSTANCE_ID_'],d.data['ID_'],d.data['TITLE_'],tabId);}
    	}}
    else alert('暂无下一条数据');//btn.setDisabled(true);
}
function getRemind(){
	if(hasReminded=='null'){	
		Ext.Ajax.request({
	    	url : basePath + 'oa/note/getRemindItem.action',
			method : 'post',
			callback : function(opt, s, res){
				 var r = new Ext.decode(res.responseText);
				 if(r.exceptionInfo){
					   showError(r.exceptionInfo);return;
				 } else if(r.success && r.data){
				    var remindtype=r.data.no_remindtype;
				    var title=r.data.no_title;
				    var content=r.data.no_content;
				    var isrepeat=r.data.no_isrepeat;
				    var approver=r.data.no_approver;
				    var apptime=r.data.no_apptime;
					apptime=apptime.substr(0,10);
				    if(isrepeat==-1){
						   Ext.create('erp.view.core.window.MajorItemWarn',{title:title,itemContent:content,approver:approver,apptime:apptime,titlevalue:title});
				    }else{
				    	   if(r.data!="readed"){
				    	    	Ext.create('erp.view.core.window.MajorItemWarn',{title:title,itemContent:content,approver:approver,apptime:apptime,titlevalue:title});							 								
						    } 			
				    }	 							
				}
			}
		});	
	}	
}
</script>
</head>
<body onload="getRemind()">
</body>
</html>