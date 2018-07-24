Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.DocumentSet', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'scm.sale.DocumentSet','core.form.Panel',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.Print',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit',
    			//'core.button.ResSubmit','core.button.Banned','core.button.ResBanned',
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
    				Ext.getCmp('cu_id').show();
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
    		}/*,
    		'erpAuditButton': {
    			click: function(btn){
    				this.FormUtil.onAudit(Ext.getCmp('cu_id').value);
    			}
    		},
    		'erpResAuditButton': {
    			click: function(btn){
    				this.FormUtil.onResAudit(Ext.getCmp('cu_id').value);
    			}
    		},
    		'erpSubmitButton': {
    			click: function(btn){
    				this.FormUtil.onSubmit(Ext.getCmp('cu_id').value);
    			}
    		},
    		'erpResSubmitButton': {
    			click: function(btn){
    				this.FormUtil.onResSubmit(Ext.getCmp('cu_id').value);
    			}
    		},
    		'erpBannedButton': {
    			click: function(btn){
    				this.FormUtil.onBanned(Ext.getCmp('cu_id').value);
    			}
    		},
    		'erpResBannedButton': {
    			click: function(btn){
    				this.FormUtil.onResBanned(Ext.getCmp('cu_id').value);
    			}
    		}*/
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});