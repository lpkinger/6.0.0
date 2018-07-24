<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"+ request.getServerName() + ":" + request.getServerPort()+ path + "/";
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet"
	href="<%=basePath%>resource/ext/resources/css/ext-all-gray.css"
	type="text/css"></link>
<link rel="stylesheet" href="<%=basePath%>resource/css/main.css"
	type="text/css"></link>
<style type="text/css">
#sidebar a:link {
	color: #1C86EE;
	text-decoration: none;
}

#sidebar a:visited {
	color: #1C86EE;
	text-decoration: none;
}

#sidebar a:hover {
	color: #CD2626;
	text-decoration: none;
}

#sidebar a:active {
	color: #1C86EE;
	text-decoration: none;
}
</style>
<script type="text/javascript" src="<%=basePath%>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/ux/grid/GridHeaderFilters.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/ux/data/PagingMemoryProxy.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/ux/grid/Export.js"></script>
<script type="text/javascript" src="<%=basePath%>jsps/oa/knowledge/Knowledge.js"></script>
<script type="text/javascript">	
var emid='<%=session.getAttribute("em_id")%>';
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath + 'app',//app文件夹所在路径
		controllers : [//声明所用到的控制层
		'oa.knowledge.KnowledgeMapLink' ],
		launch : function() {
			Ext.create('erp.view.oa.knowledge.KnowledgeMapLink');//创建视图
		}
	});
	var caller = 'KnowledgeMap';
		function Recommend() {
		var win = new Ext.window.Window(
				{
					id : 'win',
					height : '400',
					width : '600',
					maximizable : true,
					buttonAlign : 'center',
					layout : 'anchor',
					items : [{
						tag : 'iframe',
						frame : true,
						anchor : '100% 100%',
						layout : 'fit',
						html : '<iframe id="iframe_'+ caller+ '" src="'+ basePath+ 'jsps/oa/knowledge/KnowledgeForm.jsp?whoami=KnowledgeRecommend&saveUrl=oa/knowledge/SaveRecommend.action'+ '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
					}],

				});
		win.show();
	}
	function Commont() {
		var win = new Ext.window.Window(
				{
					id : 'win',
					height : '300',
					width : '450',
					maximizable : true,
					buttonAlign : 'center',
					layout : 'anchor',
					items : [ {
						tag : 'iframe',
						frame : true,
						anchor : '100% 100%',
						layout : 'fit',
						html : '<iframe id="iframe_'+ caller+ '" src="'+ basePath+ 'jsps/oa/knowledge/KnowledgeForm.jsp?whoami=KnowledgeComment&saveUrl=oa/knowledge/SaveComment.action'+ '" height="100%" width="100%" frameborder="0" scrolling="no"></iframe>'
					} ],

				});
		win.show();
	}
	function Version() {
	var code=Ext.getCmp('kl_code').getValue();
	var win = new Ext.window.Window({
			    	id : 'wingrid',
   				    height: '300',
   				    width: '700',
   				    title:'历史版本',
   				    maximizable : true,
   					buttonAlign : 'center',
   					layout : 'anchor',
   				    items: [{
   				    	  tag : 'iframe',
   				    	  frame : true,
   				    	  anchor : '100% 100%',	
   				         xtype:'erpGridPanel5',
   				         caller:'KnowledgeVersion',
   				         condition:"kl_code='"+code+"'" 
   				    }],
   				    buttons : [ {
   				    	text : $I18N.common.button.erpCloseButton,
   				    	iconCls: 'x-button-icon-close',
   				    	cls: 'x-btn-gray',
   				    	handler : function(){
   				    		Ext.getCmp('wingrid').close();
   				    	}
   				    }]
   				         
    	     });
    	    win.show();	
         }  
	
</script>
</head>
<body>
</body>
</html>