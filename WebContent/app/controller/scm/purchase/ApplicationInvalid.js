Ext.QuickTips.init();
Ext.define('erp.controller.scm.purchase.ApplicationInvalid', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'scm.purchase.ApplicationInvalid','core.form.Panel',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Update','core.button.Delete','core.button.ResAudit',
    		'core.trigger.TextAreaTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpSaveButton': {
    			click: function(btn){
    				var form = getForm(btn);
    				var code = Ext.getCmp(form.codeField);
    				if(code.value == null || code.value == '' || code.value == 'null'){
    					code.setValue(me.BaseUtil.getRandomNumber());
    				}
    				this.FormUtil.beforeSave(this);
    				Ext.getCmp('ve_id').show();
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
    				this.FormUtil.onDelete([]);
    			}
    		},
    		'erpAuditButton': {
    			click: function(btn){
    				this.FormUtil.onAudit(Ext.getCmp('ve_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			click: function(btn){
    				this.FormUtil.onResAudit(Ext.getCmp('ve_id').value);
    			}
    		},
    		'erpSubmitButton': {
    			click: function(btn){
    				this.FormUtil.onSubmit(Ext.getCmp('ve_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			click: function(btn){
    				this.FormUtil.onResSubmit(Ext.getCmp('ve_id').value);
    			}
    		},
    		'erpBannedButton': {
    			click: function(btn){
    				this.FormUtil.onBanned(Ext.getCmp('ve_id').value);
    			}
    		},
    		'erpResBannedButton': {
    			click: function(btn){
    				this.FormUtil.onResBanned(Ext.getCmp('ve_id').value);
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});