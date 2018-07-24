Ext.QuickTips.init();
Ext.define('erp.controller.oa.fee.CarAllowance', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : [ 'oa.fee.CarAllowance', 'core.form.Panel', 'core.button.Add',
			'core.button.Submit', 'core.button.Audit', 'core.button.Save',
			'core.button.Close', 'core.button.Print', 'core.button.Upload',
			'core.button.Update', 'core.button.Delete', 'core.button.ResAudit',
			'core.button.ResSubmit', 'core.form.YnField',
			'core.trigger.DbfindTrigger', 'core.form.MultiField',
			'core.form.FileField', 'core.button.Confirm' ],
	init : function() {
		var me = this;
		this.control({
			'erpSaveButton' : {
				click : function(btn) {
					var form = me.getForm(btn);
					if (Ext.getCmp(form.codeField).value == null
							|| Ext.getCmp(form.codeField).value == '') {
						me.BaseUtil.getRandomNumber();// 自动添加编号
					}
					this.FormUtil.beforeSave(this);
				}
			},
			'erpCloseButton' : {
				click : function(btn) {
					this.FormUtil.beforeClose(this);
				}
			},
			'erpAddButton' : {
				click : function(btn) {
					me.FormUtil.onAdd('CarAllowance', '新增购车补贴申请',
							'jsps/oa/fee/carAllowance.jsp');
				}
			},
			'erpUpdateButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('ca_statuscode');
					if (status && status.value != 'ENTERING') {
						btn.hide();
					}
				},
				click : function(btn) {
					this.FormUtil.onUpdate(this);
				}
			},
			'erpDeleteButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('ca_statuscode');
					if (status && status.value != 'ENTERING') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onDelete((Ext.getCmp('ca_id').value));
				}
			},
			'erpAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('ca_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					this.FormUtil.onAudit(Ext.getCmp('ca_id').value);
				}
			},
			'erpResAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('ca_statuscode');
					if (status && status.value != 'AUDITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					this.FormUtil.onResAudit(Ext.getCmp('ca_id').value);
				}
			},
			'erpSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('ca_statuscode');
					if (status && status.value != 'ENTERING') {
						btn.hide();
					}
				},
				click : function(btn) {
					this.FormUtil.onSubmit(Ext.getCmp('ca_id').value);

				}
			},
			'erpResSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('ca_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					this.FormUtil.onResSubmit(Ext.getCmp('ca_id').value);
				}
			},
			'erpConfirmButton' : {
				afterrender : function(btn) {
					var statu = Ext.getCmp('ca_statuscode');
					if (statu && statu.value != 'AUDITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.onConfirm(Ext.getCmp('ca_id').value);

				}
			}
		});
	},
	getForm : function(btn) {
		return btn.ownerCt.ownerCt;
	},
	onConfirm : function(id) {
		var form = Ext.getCmp('form');
		Ext.Ajax.request({
			url : basePath + form.confirmUrl,
			params : {
				id : id,
				caller : caller
			},
			method : 'post',
			callback : function(options, success, response) {
				// me.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if (localJson.success) {
					// audit成功后刷新页面进入可编辑的页面
					// auditSuccess(function(){
					showMessage("提示", '确认成功');
					window.location.reload();
					// });
				} else {
					if (localJson.exceptionInfo) {
						var str = localJson.exceptionInfo;
						if (str.trim().substr(0, 12) == 'AFTERSUCCESS') {// 特殊情况:操作成功，但是出现警告,允许刷新页面
							str = str.replace('AFTERSUCCESS', '');
							showMessage("提示", '确认成功');
							// auditSuccess(function(){
							window.location.reload();
							// });
						} else {
							showError(str);
							return;
						}
					}
				}
			}
		});
	}
});