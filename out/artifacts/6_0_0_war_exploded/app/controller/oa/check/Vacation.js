Ext.QuickTips.init();
Ext.define('erp.controller.oa.check.Vacation', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'oa.check.Vacation','core.form.Panel','core.button.Add','core.button.Submit',
    		'core.button.Audit','core.button.Save','core.button.Close',
    		'core.button.Update','core.button.Delete','core.button.ResAudit',
    		'core.button.ResSubmit','core.form.YnField','core.trigger.DbfindTrigger','core.button.CallProcedureByConfig'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpSaveButton': {
    			click: function(btn){
    				var form = me.getForm(btn);
    				if(Ext.getCmp(form.codeField).value == null || Ext.getCmp(form.codeField).value == ''){
    					me.BaseUtil.getRandomNumber();//自动添加编号
    				}
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
    				me.FormUtil.onAdd('addVacation', '新增请假申请单', 'jsps/oa/check/vacation.jsp');
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				me.FormUtil.onDelete((Ext.getCmp('va_id').value));
    			}
    		},
    		'erpAuditButton': {
    			click: function(btn){
    				this.FormUtil.onAudit(Ext.getCmp('va_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			click: function(btn){
    				this.FormUtil.onResAudit(Ext.getCmp('va_id').value);
    			}
    		},
    		'erpSubmitButton': {
    			click: function(btn){
    				this.FormUtil.onSubmit(Ext.getCmp('va_id').value);
    				
    			}
    		},
    		'erpResSubmitButton': {
    			click: function(btn){
    				this.FormUtil.onResSubmit(Ext.getCmp('va_id').value);
    			}
    		},
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});