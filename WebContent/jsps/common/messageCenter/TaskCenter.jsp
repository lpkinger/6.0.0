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
<link rel="stylesheet" href="<%=basePath %>jsps/common/messageCenter/css/centerform.css"  type="text/css"></link>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/grid/GridHeaderFilters.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/CheckColumn.js"></script>
<style>
.form .x-btn-default-medium{
	width:74px!important
}

.x-scroller-vertical{
	border-style:none!important;
	border-color:white!important;
	border-image:none!important;
}

.x-scroller-ct{
	border-style:none!important;
	border-color:white!important;
	border-image:none!important;	
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
        'common.messageCenter.TaskCenter'
    ],
    launch: function() {
    	Ext.create('erp.view.common.messageCenter.TaskCenter');//创建视图
    }
});

var emname = '<%=session.getAttribute("em_name")%>';
var emuu = '<%=session.getAttribute("em_uu")%>';
var emcode='<%=session.getAttribute("em_code")%>';

var caller = 'TaskCenter';

function openTable(id, caller, title, link, key, detailKey, condition){
	var main = parent.Ext.getCmp("content-panel");
	var panel = Ext.getCmp('' + id);
	var url = link;
	if(caller){
		panel = Ext.getCmp(caller);
		if(link.indexOf("?")>-1){
			url = link + '&whoami=' + caller;
		}else{
			url = link + '?whoami=' + caller;
		}
	}
	if(id){
		if(caller){
			panel = Ext.getCmp(caller + id);
			if(link.indexOf("?")>-1){
				url = link + '&whoami=' + caller + '&formCondition=' + key + '=' + id + '&gridCondition=' + detailKey + '=' + id;
			}else{
				url = link + '?whoami=' + caller + '&formCondition=' + key + '=' + id + '&gridCondition=' + detailKey + '=' + id;
			}
		}
	} else {
		if(condition != null){
			url += '&urlcondition=' + condition;
		}
	}
	if(!panel){ 
		panel = { 
			title : title.substring(0, title.toString().length > 5 ? 5 : title.toString().length),
			tag : 'iframe',
			tabConfig:{tooltip: title},
			frame : true,
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

</script>
</head>
<body>
</body>
</html>