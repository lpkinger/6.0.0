<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css" type="text/css"></link>
<!--[if IE]>
	<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-ie-scoped.css" type="text/css"></link>
<![endif]-->
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript">	
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'hr.kpi.KpiResult'
    ],
    launch: function() {
    	Ext.create('erp.view.hr.kpi.KpiResult');//创建视图
    }
});
var caller = 'KpiResult';
var formCondition = '';
var gridCondition = '';
function scoreFrom(ktd_kiid){
	var kt_bemanid=Ext.getCmp('kt_bemanid').value;
	var kt_kdbid=Ext.getCmp('kt_kdbid').value;
	Ext.define('model-score', {
		    extend: 'Ext.data.Model',
		    fields: [{name:'type', 		type:'string'},
				     {name:'percent', 	type:'string'},
				     {name:'count',		type:'string'},
				     {name:'avg',		type:'string'}]
	});
	var param = {kt_kdbid:kt_kdbid,kt_bemanid: kt_bemanid,ktd_kiid: ktd_kiid};
	Ext.Ajax.request({
    	url : basePath + 'hr/kpi/getScorefrom.action',
    	params: param,
    	method : 'post',
    	callback : function(options,success,response){
    		if (!response) return;
    		var res = new Ext.decode(response.responseText);
    		var data = Ext.decode(res.data.replace(/,}/g, '}').replace(/,]/g, ']'));
    		var cal=res.cal;
    		var g_store=Ext.create('Ext.data.Store', {
    			model:'model-score',
    			proxy :{type: 'memory'},
    			data:data
    		     });
    		var win = Ext.create('Ext.window.Window', {
    			id: 'win-scorefrom',
    			title: '分数来源',
    			height: 400,
    			width: 400,
    			maximizable: false,
    			buttonAlign: 'center',
    			layout: 'anchor' ,
    			items: [{
    				xtype:'form',
    				anchor:'100% 30%',
    				cls: 'u-form-default',
    				bodyStyle: 'background-color:#f1f1f1;',
    				items:[{
	    					xtype:'textfield',
	    					style:'text-align:center', 
	    					fieldStyle: 'background:#E0E0FF;color:#515151',
	    					width:350,
	    					readOnly:true,
	    					fieldLabel: '计算方法',
	    					value: 'sum（平均分*权重）/sum(权重)'
    					},{
	    					xtype:'textareafield',
	    					width:350,
	    					style:'text-align:center', 
	    					fieldStyle: 'background:#E0E0FF;color:#515151',
	    					readOnly:true,
	    					fieldLabel: '分数计算',
	    					value: cal
    					}]
    				},{
    					xtype:'grid',
    					anchor:'100% 70%',
    					store:g_store,
    					columns: [
    					          { header: '类型',     dataIndex: 'type' ,width: 100 },
    					          { header: '权重(%)',  dataIndex: 'percent',width: 80},
    					          { header: '数量(条)', dataIndex: 'count' },
    					          { header: '平均分',   dataIndex: 'avg' }
    				    ],
    				    bodyStyle: 'background-color:#f1f1f1;'	  
    			}], 
    			buttons: [{
    				text: '关  闭',
    				iconCls: 'x-button-icon-close',
    				cls: 'x-btn-gray',
    				handler: function() {
    					Ext.getCmp('win-scorefrom').close();
    				}
    			}]
    		});
    		win.show();	    		
    	}
	});
}
</script>
</head>
<body >
</body>
</html>