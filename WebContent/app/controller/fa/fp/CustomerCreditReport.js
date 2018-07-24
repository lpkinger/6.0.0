Ext.QuickTips.init();
Ext.define('erp.controller.fa.fp.CustomerCreditReport', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'fa.fp.CustomerCreditReport','core.form.Panel','core.form.MultiField','core.form.FileField',
      		'core.button.Save','core.button.Add','core.button.Submit','core.button.Upload','core.button.ResAudit',
  			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit',
  			'core.button.Sync','core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.button.FormsDoc'
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
			'erpDeleteButton' : {
				click: function(btn){
					me.FormUtil.onDelete(Ext.getCmp('ccr_id').value);
				}
			},
			'erpUpdateButton': {
				afterrender: function(btn){
    				var status = Ext.getCmp('ccr_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				me.beforeUpdate();
    			}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addCustomerCreditReport', '新增客户信用报告上传', 'jsps/fa/fp/CustomerCreditReport.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ccr_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('ccr_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ccr_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('ccr_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ccr_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('ccr_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('ccr_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('ccr_id').value);
				}
			}
		});
	},
    beforeSubmit:function(btn){
    	var me = this;
    	me.FormUtil.onSubmit(Ext.getCmp('ccr_id').value);
    },
	beforeUpdate: function(){
		var bool = true;
		if(bool)
			this.FormUtil.onUpdate(this);
	},   
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});