Ext.QuickTips.init();
Ext.define('erp.controller.oa.officialDocument.sendODManagement.Query', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
     		'oa.officialDocument.sendODManagement.query.Viewport','oa.officialDocument.sendODManagement.query.GridPanel','oa.officialDocument.sendODManagement.query.Form',
     		'core.trigger.DbfindTrigger','core.form.ConDateField','core.form.WordSizeField','oa.mail.MailPaging'
     	],
    init:function(){
    	this.control({
    		'erpSODQueryGridPanel': { 
    			itemclick: this.onGridItemClick 
    		},
//    		'erpSynergyQueryFormPanel button[name=confirm]': {
//    			click: function(btn){
//    				
//    			}
//    		}
    	});
    },
    onGridItemClick: function(selModel, record){//grid行选择
    	console.log(record);
    	var me = this;
    	var id = record.data.sod_id;
    	var panel = Ext.getCmp("qsod" + id); 
    	var main = parent.Ext.getCmp("content-panel");
    	if(!panel){ 
    		var title = "发文查看";
	    	panel = { 
	    			title : title,
	    			tag : 'iframe',
	    			tabConfig:{tooltip: record.data['sod_title']},
	    			frame : true,
	    			border : false,
	    			layout : 'fit',
	    			iconCls : 'x-tree-icon-tab-tab1',
	    			html : '<iframe id="iframe_' + id + '" src="' + basePath + "jsps/oa/officialDocument/sendODManagement/sodDetail.jsp?flag=query&id=" + id + '" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>',
	    			closable : true,
	    			listeners : {
	    				close : function(){
	    			    	main.setActiveTab(main.getActiveTab().id); 
	    				}
	    			} 
	    	};
	    	me.FormUtil.openTab(panel, "qsod" + id); 
    	}else{ 
	    	main.setActiveTab(panel); 
    	} 
//    	var win = new Ext.window.Window({
//			id : 'win',
//			title: "收文查看",
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
//				html : '<iframe id="iframe_' + id + '" src="' + basePath + 'jsps/oa/officialDocument/receiveODManagement/rodDetail.jsp?id=' + id + '" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>'
//			}]
//		});
//		win.show();	
    }

});