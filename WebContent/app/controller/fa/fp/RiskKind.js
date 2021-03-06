Ext.QuickTips.init();
Ext.define('erp.controller.fa.fp.RiskKind', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
    		'fa.fp.RiskKind','core.form.Panel','core.form.MultiField','core.form.FileField',
    		'core.button.Add','core.button.Close','core.button.Delete','core.button.Update','core.button.Upload',
    		'core.button.Save','core.button.Submit','core.button.ResSubmit','core.button.ResAudit','core.button.Audit',
  			'core.button.Sync','core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField',
  			'core.form.SeparNumber'
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
					me.FormUtil.onDelete(Ext.getCmp('rk_id').value);
				}
			},
			'erpUpdateButton': {
				afterrender: function(btn){
    				var status = Ext.getCmp('rk_statuscode');
    				if(status && status.value != 'ENTERING'){
    					btn.hide();
    				}
    			},
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addRiskKind', '新增风险类型设置', 'jsps/fa/fp/RiskKind.jsp');
				}
			},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('rk_statuscode');
					if(status && status.value != 'ENTERING'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onSubmit(Ext.getCmp('rk_id').value);
				}
			},
			'erpResSubmitButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('rk_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResSubmit(Ext.getCmp('rk_id').value);
				}
			},
			'erpAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('rk_statuscode');
					if(status && status.value != 'COMMITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onAudit(Ext.getCmp('rk_id').value);
				}
			},
			'erpResAuditButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('rk_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				},
				click: function(btn){
					me.FormUtil.onResAudit(Ext.getCmp('rk_id').value);
				}
			},
			'erpSyncButton': {
				afterrender: function(btn){
					var status = Ext.getCmp('rk_statuscode');
					if(status && status.value != 'AUDITED'){
						btn.hide();
					}
				}
			}
		});
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});