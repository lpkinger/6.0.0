Ext.QuickTips.init();
Ext.define('erp.controller.plm.request.SampleMakeApply', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	views : [ 'plm.request.SampleMakeApply', 'core.form.Panel',
			'core.toolbar.Toolbar', 'core.trigger.MultiDbfindTrigger',
			'core.form.MultiField', 'core.button.Add', 'core.button.Save',
			'core.button.Close', 'core.button.Banned', 'core.button.ResBanned',
			'core.button.Update', 'core.button.Delete', 'core.form.YnField',
			'core.button.Sync', 'core.button.ResAudit', 'core.button.Audit',
			'core.button.Submit', 'core.button.ResSubmit',
			'core.button.TurnOtherOut', 'core.trigger.TextAreaTrigger',
			'core.trigger.DbfindTrigger', 'core.button.TurnApplication',
			'core.button.TurnMake' ],
	init : function() {
		var me = this;
		var isTurn = false;//标志是否转过单
		this.control({
			'erpSaveButton' : {
				click : function(btn) {
					var form = me.getForm(btn);
					if (!Ext.isEmpty(form.codeField)
							&& Ext.getCmp(form.codeField)
							&& (Ext.getCmp(form.codeField).value == null || Ext
									.getCmp(form.codeField).value == '')) {
						me.BaseUtil.getRandomNumber(caller);//自动添加编号
					}
					this.FormUtil.beforeSave(this);
				}
			},
			'erpCloseButton' : {
				click : function(btn) {
					this.FormUtil.beforeClose(this);
				}
			},
			'erpUpdateButton' : {
				afterrender : function(btn) {
					var statu = Ext.getCmp('sm_statuscode');
					if (statu && statu.value != 'ENTERING') {
						btn.hide();
					}
				},
				click : function(btn) {
					this.FormUtil.onUpdate(this);
				}
			},
			'erpDeleteButton' : {
				afterrender : function(btn) {
					var statu = Ext.getCmp('sm_statuscode');
					if (statu && statu.value != 'ENTERING') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onDelete(Ext.getCmp('sm_id').value);
				},
			},
			'erpAddButton' : {
				click : function() {
					me.FormUtil.onAdd('addSampleMakeApply', '新增样机制作申请单',
							'jsps/plm/request/SampleMakeApply.jsp');
				}
			},
			'erpSubmitButton' : {
				afterrender : function(btn) {
					var statu = Ext.getCmp('sm_statuscode');
					if (statu && statu.value != 'ENTERING') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onSubmit(Ext.getCmp('sm_id').value);
				}
			},
			'erpResSubmitButton' : {
				afterrender : function(btn) {
					var statu = Ext.getCmp('sm_statuscode');
					if (statu && statu.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResSubmit(Ext.getCmp('sm_id').value);
				}
			},
			'erpAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('sm_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onAudit(Ext.getCmp('sm_id').value);
				}
			},
			'erpResAuditButton' : {
				afterrender : function(btn) {
					var statu = Ext.getCmp('sm_statuscode');
					if (statu && statu.value != 'AUDITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					if (isTurn) {
						showError("该申请单已转单，不能反审核！");
						return;
					} else {
						me.FormUtil.onResAudit(Ext.getCmp('sm_id').value);
					}
				}
			},
			'erpTurnApplicationButton' : { //转请购单按钮
				afterrender : function(btn) {
					var status = Ext.getCmp('sm_statuscode');
					if (status && status.value != 'AUDITED') {
						btn.hide();
					}
				},
				click : function(b) {
					var id = Ext.getCmp("sm_id");
					if (id && id.value != null && id.value != '') {
						me.turnApplication(id.value);
						isTurn = true;
					}
				}
			},
			'erpTurnMakeButton' : { //转制造单按钮
				afterrender : function(btn) {
					var status = Ext.getCmp('sm_statuscode');
					if (status && status.value != 'AUDITED') {
						btn.hide();
					}
				},
				click : function(b) {
					var sm_acceptdept = Ext.getCmp("sm_acceptdept");
					if(sm_acceptdept && sm_acceptdept.value!='生产部'){
						showError("接收部门不是生产部，不能转制造单！");
					}else{
					var id = Ext.getCmp("sm_id");
					if (id && id.value != null && id.value != '') {
						me.turnMake(id.value);
						isTurn = true;
					}
				  }
				}
			},
			'erpTurnOtherOutButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('sm_statuscode');
					if (status && status.value != 'AUDITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					var id = Ext.getCmp("sm_id");
					if (id && id.value != null && id.value != '') {
						me.turnOtherOut(id.value);
						isTurn = true;
					}
				}
			}
		});
	},
	onGridItemClick : function(selModel, record) {//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
	getForm : function(btn) {
		return btn.ownerCt.ownerCt;
	},
	//转请购单
	turnApplication : function(id) {
		var me = this;
		me.FormUtil.setLoading(true);
		Ext.Ajax.request({
			url : basePath + 'plm/request/turnApplication.action',
			params : {
				caller : caller,
				id : id
			},
			method : 'post',
			callback : function(options, success, response) {
				me.FormUtil.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if (localJson.success) {
					showMessage("提示", localJson.data);
				} else if (localJson.exceptionInfo) {
					var str = localJson.exceptionInfo;
					showError(str);
					return;
				}
			}
		});
	},
	//转其他出库单
	turnOtherOut : function(id) {
		var me = this;
		me.FormUtil.setLoading(true);
		Ext.Ajax.request({
			url : basePath + 'plm/request/turnOtherOut.action',
			params : {
				caller : caller,
				id : id
			},
			method : 'post',
			callback : function(options, success, response) {
				me.FormUtil.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if (localJson.success) {
					showMessage("提示", localJson.data);
				} else if (localJson.exceptionInfo) {
					var str = localJson.exceptionInfo;
					showError(str);
					return;
				}
			}
		});
	},
	//转制造单
	turnMake : function(id){
		var me = this;
		me.FormUtil.setLoading(true);
		Ext.Ajax.request({
			url : basePath + 'plm/request/turnMake.action',
			params : {
				caller : caller,
				id : id
			},
			method : 'post',
			callback : function(options, success, response) {
				me.FormUtil.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if (localJson.success) {
					showMessage("提示", localJson.data);
				} else if (localJson.exceptionInfo) {
					var str = localJson.exceptionInfo;
					showError(str);
					return;
				}
			}
		});
	}
});