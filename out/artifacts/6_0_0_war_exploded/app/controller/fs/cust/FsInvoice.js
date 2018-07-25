Ext.QuickTips.init();
Ext.define('erp.controller.fs.cust.FsInvoice', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : ['core.form.Panel', 'fs.cust.FsInvoice', 'core.grid.Panel2','core.toolbar.Toolbar',
			'core.button.Save', 'core.button.Add','core.button.Submit', 'core.button.Upload','core.button.ResAudit',
			'core.button.Audit','core.button.Close','core.button.Delete','core.button.Update','core.button.ResSubmit',
			'core.button.Export',
			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField', 'core.grid.YnColumn',
			'core.form.StatusField','core.form.FileField','core.form.MultiField','core.trigger.AddDbfindTrigger'],
	init : function() {
		var me = this;
		this.control({
			'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('FsInvoice', '发票', 'jsps/fs/cust/fsInvoice.jsp');
    			}
        	},
			'erpSaveButton': {
    			click: function(btn){
					this.FormUtil.beforeSave(this);				
    			}
        	},
			'erpDeleteButton' : {
				click : function(btn) {
					me.FormUtil.onDelete(Ext.getCmp('in_id').value);
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
					var status = Ext.getCmp('in_statuscode');
					if (status && status.value != 'ENTERING') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onSubmit(Ext.getCmp('in_id').value);
				}
			},
			'erpResSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('in_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResSubmit(Ext.getCmp('in_id').value);
				}
			},
			'erpAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('in_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onAudit(Ext.getCmp('in_id').value);
				}
			},
			'erpResAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('in_statuscode');
					if (status && status.value != 'AUDITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResAudit(Ext.getCmp('in_id').value);
				}
			}
		})
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});