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
<link rel="stylesheet" href="<%=basePath %>jsps/oa/flow/css/centerform.css"  type="text/css"></link>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/ux/css/CheckHeader.css" />
<script type="text/javascript" src="<%=basePath %>resource/ux/grid/GridHeaderFilters.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/CheckColumn.js"></script>
<style>
.search-contain{ 
	float: right;	
}
.search-item {
	font: normal 11px tahoma, arial, helvetica, sans-serif;
	padding: 3px 10px 3px 10px;
	border: 1px solid #fff;
	border-bottom: 1px solid #eeeeee;
	white-space: normal;
	color: #555;
}

.search-item h3 {
	display: block;
	font: inherit;
	font-weight: bold;
	color: #222;
}

.search-item h3 span {
	float: right;
	font-weight: normal;
	margin: 0 0 5px 5px;
	width: 150px;
	display: block;
	clear: none;
}

.msg .x-box-mc {
	font-size: 14px;
}
#msg-div {
	position: absolute;
	left: 50%;
	top: 10px;
	width: 400px;
	margin-left: -200px;
	z-index: 20000;
}

#msg-div .msg {
	border-radius: 8px;
	-moz-border-radius: 8px;
	background: #F6F6F6;
	border: 2px solid #ccc;
	margin-top: 2px;
	padding: 10px 15px;
	color: #555;
}

#msg-div .msg h3 {
	margin: 0 0 8px;
	font-weight: bold;
	font-size: 15px;
}

#msg-div .msg p {
	margin: 0;
}
.x-button-icon-query {
	background-image: url('<%=basePath %>resource/images/query.png')
}

.custom-rest{
	background:  url('<%=basePath %>/resource/images/x.png') no-repeat center
		center !important;
	width: 16px !important;
	background-image: url('<%=basePath %>/resource/ext/4.2/resources/ext-theme-gray/images/form/text-bg.gif'); 
    height: 16px;
    border: 0!important;
    visibility:hidden;
    margin: 2px 0px 0px 0px;
}
.x-grid-checkheader {
    height: 100%;
    background-position: 50% 50%;
}
#addFlow-btnEl{
	height:22px
}
#addFlow-btnIconEl{
    margin-top: 3px;
    margin-left: 2px;
}
#addFlowMenu .x-vertical-box-overflow-body {
	width: 100% !important;
	overflow-x: hidden;
	overflow-y: auto;
}
.form .x-panel-header {
    top: 0px!important;
    padding: 0px;
    margin-bottom:7px !important;
}
.x-panel-body {
    background: #f7f7f7;
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
        'oa.flow.FlowCenter'
    ],
    launch: function() {
    	Ext.create('erp.view.oa.flow.flowCenter.viewport');//创建视图
    }
});

var emname = '<%=session.getAttribute("em_name")%>';
var emuu = '<%=session.getAttribute("em_uu")%>';
var emcode='<%=session.getAttribute("em_code")%>';

var caller = 'FlowCenter';

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
													var grid = Ext.getCmp('jprocessGrid');
													if(grid){
														if(grid.FormUtil){
															var tab = grid.FormUtil.getActiveTab();
															if(tab){
																tab.fireEvent('activate',tab);
															}		
														}
													}
													
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
	if(contains(url, 'session:em_id', true)){
		url = url.replace(/session:em_id/,"'"+em_id+"'" );
	}
	return url;
}
</script>
</head>
<body>
</body>
</html>