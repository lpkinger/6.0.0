Ext.QuickTips.init();
Ext.define('erp.controller.common.JProcessBaseSet', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'core.form.Panel','common.JProcess.JProcessBaseSet',
    		'core.button.Save','core.button.Close','core.button.Update','core.button.Delete',
    		'core.trigger.DbfindTrigger'
    	],
    init:function(){
    	var me = this;    	
    	this.control({
    		'erpDeleteButton' : {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('ps_id').value);
    			}
    		},    		
    		'erpUpdateButton': {
    			click: function(btn){
    				me.beforeUpdate();
    			}
    		},    		
    		'erpCloseButton': {
    			click: function(btn){
    				me.FormUtil.beforeClose(me);
    			}
    		}
    		
    });},
	beforeUpdate: function(){
		if(Ext.getCmp('ps_id').value)this.FormUtil.onUpdate(this);
		else this.FormUtil.beforeSave(this);
	}             
});