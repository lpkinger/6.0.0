Ext.QuickTips.init();
Ext.define('erp.controller.fs.credit.CreditRatings', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : ['core.form.Panel', 'fs.credit.CreditRatings', 'core.grid.Panel2','core.toolbar.Toolbar', 'core.form.MultiField','core.button.TurnProject', 
			'core.button.Save', 'core.button.Add','core.button.Submit', 'core.button.Upload','core.button.ResAudit','core.button.Audit',
			'core.button.Close','core.button.Delete','core.button.Update','core.button.DeleteDetail','core.button.ResSubmit', 'core.button.Export',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField', 'core.grid.YnColumn', 'core.form.StatusField',
			'core.form.FileField','core.button.CopyAll','core.button.ResetSync', 'core.button.RefreshSync','core.button.ChangeResponsible','core.form.MultiField'],
	init : function() {
		var me = this;
		this.control({
			'field[name=cr_scorebegin]' : {
				afterrender : function(f) {
					f.maxValue = 100;
				}
			},
			'field[name=cr_storeend]' : {
				afterrender : function(f) {
					f.maxValue = 100;
				}
			},
			'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('CreditRatings', '信用级别设置', 'jsps/fs/credit/creditRatings.jsp');
    			}
        	},
			'erpSaveButton': {
    			click: function(btn){				
					this.FormUtil.beforeSave(this);				
    			}
        	},
			'erpDeleteButton' : {
				click : function(btn) {
					me.FormUtil.onDelete(Ext.getCmp('cr_id').value);
				}
			},
			'erpUpdateButton' : {
				click : function(btn) {
					me.FormUtil.onUpdate(this);
				}
			},
			'erpCloseButton' : {
				click : function(btn) {
					me.FormUtil.beforeClose(me);
				}
			},
			'erpSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cr_statuscode');
					if (status && status.value != 'ENTERING') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onSubmit(Ext.getCmp('cr_id').value);
				}
			},
			'erpResSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cr_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResSubmit(Ext.getCmp('cr_id').value);
				}
			},
			'erpAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cr_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onAudit(Ext.getCmp('cr_id').value);
				}
			},
			'erpResAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('cr_statuscode');
					if (status && status.value != 'AUDITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResAudit(Ext.getCmp('cr_id').value);
				}
			}
		})
	}
});