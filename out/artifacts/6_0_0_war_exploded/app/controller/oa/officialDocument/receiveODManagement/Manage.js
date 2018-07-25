Ext.QuickTips.init();
Ext.define('erp.controller.oa.officialDocument.receiveODManagement.Manage', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
     		'oa.officialDocument.receiveODManagement.manage.Viewport','oa.officialDocument.receiveODManagement.manage.GridPanel','oa.officialDocument.receiveODManagement.manage.Form',
     		'core.trigger.DbfindTrigger','core.form.ConDateField','oa.mail.MailPaging'
     	],
    init:function(){
    	this.control({
    		'erpRODManageGridPanel': { 
    			itemclick: this.onGridItemClick 
//    		},
//    		'erpSynergyManageFormPanel button[name=confirm]': {
//    			click: function(btn){
//    				
//    			}
    		}
    	});
    },
    onGridItemClick: function(selModel, record){//grid行选择
    	console.log(record);
    	var me = this;
    	if(record.data.rod_statuscode == 'OVERED'){
    		var id = record.data.rod_id;
    		var panel = Ext.getCmp("mrod" + id); 
    		var main = parent.Ext.getCmp("content-panel");
    		if(!panel){ 
    			var title = "收文分发";
    			panel = { 
    					title : title,
    					tag : 'iframe',
    					tabConfig:{tooltip: record.data['rod_title']},
    					frame : true,
    					border : false,
    					layout : 'fit',
    					iconCls : 'x-tree-icon-tab-tab1',
    					html : '<iframe id="iframe_' + id + '" src="' + basePath + "jsps/oa/officialDocument/receiveODManagement/rodDetail.jsp?flag=manage&id=" + id + '" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>',
    					closable : true,
    					listeners : {
    						close : function(){
    							main.setActiveTab(main.getActiveTab().id); 
    						}
    					} 
    			};
    			me.FormUtil.openTab(panel, "mrod" + id); 
    		}else{ 
    			main.setActiveTab(panel); 
    		}   		
    	} else {
    		return;
    	}
//    	var win = new Ext.window.Window({
//			id : 'win',
//			title: "协同查看",
//			height: "80%",
//			width: "80%",
//			maximizable : false,
//			buttonAlign : 'left',
//			layout : 'anchor',
//			items: [{
//				tag : 'iframe',
//				frame : true,
//				anchor : '100% 100%',
//				layout : 'fit',
//				html : '<iframe id="iframe_' + id + '" src="' + basePath + 'jsps/oa/myProcess/synergy/seeSynergy.jsp?id=' + id + '" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>'
//			}]
//		});
//		win.show();	
    }

});