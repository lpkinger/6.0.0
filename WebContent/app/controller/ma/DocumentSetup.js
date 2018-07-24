Ext.QuickTips.init();
Ext.define('erp.controller.ma.DocumentSetup', {
    extend: 'Ext.app.Controller',
    requires: ['erp.util.FormUtil', 'erp.util.BaseUtil'],
    views:[
   		'ma.DocumentSetup','core.form.Panel',
   		'core.button.Add','core.button.Save','core.button.Close','core.form.YnField',
			'core.button.Update','core.button.Delete','core.button.ResAudit',
		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
   	],
    init:function(){
    	var me = this;
    	me.FormUtil = Ext.create('erp.util.FormUtil');
    	me.BaseUtil = Ext.create('erp.util.BaseUtil');
    	this.control({ 
    		'erpSaveButton': {
    			click: function(btn){
    				me.FormUtil.beforeSave(me);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				me.FormUtil.onUpdate(me);
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});