Ext.QuickTips.init();
Ext.define('erp.controller.oa.check.Outapply', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.check.Outapply','core.form.Panel','core.button.Add','core.button.Submit',
    		'core.button.Audit','core.button.Save','core.button.Close',
    		'core.button.Update','core.button.Delete','core.button.ResAudit',
    		'core.button.ResSubmit','core.form.YnField','core.trigger.DbfindTrigger',
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
    		'erpAddButton': {
    			click: function(btn){
    				me.FormUtil.onAdd('addOutapply', '新增外出申请单', 'jsps/oa/check/outapply.jsp');
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete((Ext.getCmp('oa_id').value));
    			}
    		},
    		'erpAuditButton': {
    			click: function(btn){
    				this.FormUtil.onAudit(Ext.getCmp('oa_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			click: function(btn){
    				this.FormUtil.onResAudit(Ext.getCmp('oa_id').value);
    			}
    		},
    		'erpSubmitButton': {
    			click: function(btn){
    				this.FormUtil.onSubmit(Ext.getCmp('oa_id').value);
    				
    			}
    		},
    		'erpResSubmitButton': {
    			click: function(btn){
    				this.FormUtil.onResSubmit(Ext.getCmp('oa_id').value);
    			}
    		},
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});