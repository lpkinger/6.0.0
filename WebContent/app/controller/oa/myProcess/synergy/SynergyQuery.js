Ext.QuickTips.init();
Ext.define('erp.controller.oa.myProcess.synergy.SynergyQuery', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
     		'oa.myProcess.synergy.synergyQuery.Viewport','oa.myProcess.synergy.synergyQuery.GridPanel','oa.myProcess.synergy.synergyQuery.Form',
     		'core.trigger.DbfindTrigger','core.form.ConDateField','oa.mail.MailPaging'
     	],
    init:function(){
    	this.control({
    		'erpSynergyQueryGridPanel': { 
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
    	var id = record.data.sy_id;
    	var win = new Ext.window.Window({
			id : 'win',
			title: "协同查看",
			height: "80%",
			width: "80%",
			maximizable : false,
			buttonAlign : 'left',
			layout : 'anchor',
			items: [{
				tag : 'iframe',
				frame : true,
				anchor : '100% 100%',
				layout : 'fit',
				html : '<iframe id="iframe_' + id + '" src="' + basePath + 'jsps/oa/myProcess/synergy/seeSynergy.jsp?id=' + id + '" height="100%" width="100%" frameborder="0" scrolling="yes"></iframe>'
			}]
		});
		win.show();	
    }

});