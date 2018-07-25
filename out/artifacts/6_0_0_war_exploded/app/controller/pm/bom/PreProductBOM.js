Ext.QuickTips.init();
Ext.define('erp.controller.pm.bom.PreProductBOM', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'pm.bom.PreProductBOM','core.form.Panel',
    		'core.button.Add','core.button.Submit','core.button.Audit','core.button.Save','core.button.Close','core.button.ResSubmit',
    			'core.button.Upload','core.button.Update','core.button.Delete','core.button.ResAudit','core.form.YnField',
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
        				me.FormUtil.onDelete(Ext.getCmp('pre_id').value);
        			}
        		},
        		'erpAddButton': {
        			click: function(){
        				me.FormUtil.onAdd('addPreProductBOM', '新增新物料申请', 'jsps/pm/bom/preProductBOM.jsp');
        			}
        		},
        		'erpSubmitButton': {
        			afterrender: function(btn){
        				var status = Ext.getCmp('pre_statuscode');
        				if(status && status.value != 'ENTERING'){
        					btn.hide();
        				}
        			},
        			click: function(btn){
        				me.FormUtil.onSubmit(Ext.getCmp('pre_id').value);
        			}
        		},
        		'erpResSubmitButton': {
        			afterrender: function(btn){
        				var status = Ext.getCmp('pre_statuscode');
        				if(status && status.value != 'COMMITED'){
        					btn.hide();
        				}
        			},
        			click: function(btn){
        				me.FormUtil.onResSubmit(Ext.getCmp('pre_id').value);
        			}
        		},
        		'erpAuditButton': {
        			afterrender: function(btn){
        				var status = Ext.getCmp('pre_statuscode');
        				if(status && status.value != 'COMMITED'){
        					btn.hide();
        				}
        			},
        			click: function(btn){
        				me.FormUtil.onAudit(Ext.getCmp('pre_id').value);
        			}
        		},
        		'erpResAuditButton': {
        			afterrender: function(btn){
        				var status = Ext.getCmp('pre_statuscode');
        				if(status && status.value != 'AUDITED'){
        					btn.hide();
        				}
        			},
        			click: function(btn){
        				me.FormUtil.onResAudit(Ext.getCmp('pre_id').value);
        			}
        		},
        		
        	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});