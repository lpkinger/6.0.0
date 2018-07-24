Ext.QuickTips.init();
Ext.define('erp.controller.hr.kbi.checkKBIAssess', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'hr.kbi.checkKBIAssess','core.form.Panel',
    		'core.button.Add','core.button.Save','core.button.Close',
    		'core.button.Update','core.button.Delete','core.form.YnField','core.form.MultiField','core.form.FileField',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger','core.trigger.MultiDbfindTrigger','core.trigger.AddDbfindTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpSaveButton': {
    			click: function(btn){
    				this.FormUtil.beforeSave(this);
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});