Ext.QuickTips.init();
Ext.define('erp.controller.pm.make.MakeDelete', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'pm.make.MakeDelete','core.form.Panel',
    		'core.button.Add','core.button.Save','core.button.Close',
    		'core.button.Submit','core.button.ResSubmit','core.button.Audit','core.button.ResAudit',
    		'core.button.Update','core.button.Delete','core.form.YnField',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
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
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete(Ext.getCmp('ma_id').value);
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addMakeDelete', '新增整批制造单删除', 'jsps/pm/make/makeDelete.jsp');
    			}
    		},
    		'erpSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ma_checkstatuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onSubmit(Ext.getCmp('ma_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ma_checkstatuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResSubmit(Ext.getCmp('ma_id').value);
    			}
    		},
    		'erpAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ma_checkstatuscode');
    				if(status && status.value != 'COMMITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onAudit(Ext.getCmp('ma_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			afterrender: function(btn){
    				var status = Ext.getCmp('ma_checkstatuscode');
    				if(status && status.value != 'AUDITED'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.FormUtil.onResAudit(Ext.getCmp('ma_id').value);
    			}
    		},
    		
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});