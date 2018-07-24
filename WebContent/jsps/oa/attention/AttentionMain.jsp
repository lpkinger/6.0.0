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
<script type="text/javascript" src="<%=basePath %>jsps/oa/attention/AttentionManage.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/RowExpander.js"></script>
<script type="text/javascript" src="<%=basePath %>app/util/BaseUtil.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/grid/GridHeaderFilters.js"></script>
<script type="text/javascript">	
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
var caller = 'AttentionManage';
var condition='1=1 ';
var page = 1;
	var value = 0;
	var total = 0;
	var dataCount = 0;//结果总数
	var msg = '';
	var height = window.innerHeight;
	var pageSize = parseInt(height*0.7/21);
var formCondition = '';
var emid=<%=session.getAttribute("em_id")%>;
var gridCondition = '1=1';
     Ext.application({
        name: 'erp',//为应用程序起一个名字,相当于命名空间
        appFolder: basePath+'app',//app文件夹所在路径
        controllers: [//声明所用到的控制层
        'oa.attention.AttentionMain'
       ],
       launch: function() {
    	Ext.create('erp.view.oa.attention.AttentionMain');//创建视图
        }
    });   
     function openUrl(value, keyField, url, title) {
		url = url + '?attentedemid=' + value;
		var panel = Ext.getCmp(keyField + "=" + value);
		var main = parent.Ext.getCmp("content-panel");
		if (!panel) {
			if (title.toString().length > 4) {
				title = title.toString().substring(title.toString().length - 4);
			}
			panel = {
				title : title,
				tag : 'iframe',
				tabConfig : {
					tooltip : title + '(' + keyField + "=" + value + ')'
				},
				frame : true,
				border : false,
				layout : 'fit',
				iconCls : 'x-tree-icon-tab-tab',
				html : '<iframe id="iframe_maindetail_'+ keyField+ "_"+ value+ '" src="'+ basePath+ url+ '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>',
				closable : true,
				listeners : {
					close : function() {
						main.setActiveTab(main.getActiveTab().id);
					}
				}
			};
			openTab(panel, keyField + "=" + value);
		} else {
			main.setActiveTab(panel);
		}
	}
	function openTab(panel, id) {
		var o = (typeof panel == "string" ? panel : id || panel.id);
		var main = parent.Ext.getCmp("content-panel");
		/*var tab = main.getComponent(o); */
		if (!main) {
			main = parent.parent.Ext.getCmp("content-panel");
		}
		var tab = main.getComponent(o);
		if (tab) {
			main.setActiveTab(tab);
		} else if (typeof panel != "string") {
			panel.id = o;
			var p = main.add(panel);
			main.setActiveTab(p);
		}
	}    
</script>
</head>
<body >
<div id='mygrid'></div>
<div id='employeedata'></div>
<div id='details'></div>
</body>
</html>