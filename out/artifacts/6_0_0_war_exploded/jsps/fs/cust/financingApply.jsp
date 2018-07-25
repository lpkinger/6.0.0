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
<style>
 #win-body{text-align:center;border:none}  
 .x-window-body-default {
    border-color: #bcb1b0;
    border-width: 1px;
    background: none; 
    color: black;
}
</style>
<script type="text/javascript">

/**
 * 明细帐
 */
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'fs.cust.FinancingApply'
    ],
    launch: function() {
        Ext.create('erp.view.fs.cust.FinancingApply');
    }
});

var caller = 'FinancingApply';
var formCondition = getUrlParam('formCondition');
var gridCondition = '';

function showWindow(id){
	var win = Ext.getCmp(id);
	var title = id=="inforList"?"需提供资料清单":"客户信息保密协议、企业征信查询及系统数据获取授权书";
	var url = "jsps/fs/cust/"+(id=="inforList"?"inforList.jsp":"grantGetDatas.jsp");
	if(!win){
		win = new Ext.window.Window({
			id : id,
			title: title,
			height: "85%",
			width: "70%",
			maximizable : true,
			buttonAlign : 'center',
			layout : 'fit',
			modal: true,
			items: [{
				tag : 'iframe',
				frame : true,
				layout : 'fit',
				html : '<iframe src="' + basePath + url + '" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>'
			}],
			buttons:[{
		    	text : $I18N.common.button.erpCloseButton,
		    	iconCls: 'x-button-icon-close',
		    	cls: 'x-btn-gray',
		    	handler : function(btn){
		    		btn.ownerCt.ownerCt.close();
		    	}
		    }]
		});
	}
	win.show();    				
}

</script>
</head>
<body >
</body>
</html>