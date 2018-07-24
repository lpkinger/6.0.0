Ext.QuickTips.init();
Ext.define('erp.controller.oa.fee.feePleaseYZSYSQ', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : ['core.button.PrintByCondition','oa.fee.feePleaseYZSYSQ', 'core.form.Panel', 'core.grid.Panel2',
			'core.toolbar.Toolbar', 'core.button.Scan', 'core.form.FileField',
			'core.button.Save', 'core.button.Add', 'core.button.Submit',
			'core.button.Print', 'core.button.Upload', 'core.button.ResAudit',
			'core.button.Audit', 'core.button.Close', 'core.button.Delete',
			'core.button.Update', 'core.button.DeleteDetail',
			'core.button.TurnFYBX', 'core.form.DateHourMinuteField',
			'core.button.ResSubmit', 'core.button.End', 'core.button.ResEnd',
			'core.button.Confirm', 'core.trigger.MultiDbfindTrigger',
			'core.trigger.DbfindTrigger', 'core.trigger.TextAreaTrigger',
			'core.grid.detailAttach', 'core.form.MultiField',
			'erp.view.core.button.AddDetail',
			'erp.view.core.button.DeleteDetail', 'erp.view.core.button.Copy',
			'erp.view.core.button.Paste', 'erp.view.core.button.Up',
			'erp.view.core.button.Down', 'erp.view.core.button.UpExcel',
			'common.datalist.Toolbar', 'core.trigger.AutoCodeTrigger'],
	init : function() {
		var me = this;
		this.control({
			'erpGridPanel2' : {
				itemclick : function(view, record) {
					this.onGridItemClick(view, record);
				}
			},
			'dbfindtrigger[name=fp_vendcode]':{
			aftertrigger:function(trigger){   				
    			var data = Ext.getCmp('grid').getStore().data;
    			for(var i =0;i<data.getCount();i++){
    				data.getAt(i).set('fpd_d6',Ext.getCmp('fp_vendname').value); }
    			
			}},		
			'autocodetrigger' : {
				aftertrigger : function(trigger, val, path, item) {
					Ext.getCmp('FP_V13').setValue(null);
					Ext.getCmp('FP_V4').setValue(null);
					Ext.getCmp('FP_V5').setValue(null);
					Ext.getCmp('FP_V7').setValue(null);
					Ext.getCmp('FP_V8').setValue(null);
					if (path) {
						if (path[0]) {
							Ext.getCmp('FP_V4').setValue(path[0]);
						}
						if (path[1]) {
							Ext.getCmp('FP_V5').setValue(path[1]);
						}
						if (path[2]) {
							Ext.getCmp('FP_V7').setValue(path[2]);
						}
						if (path[3]) {
							Ext.getCmp('FP_V8').setValue(path[3]);
						}
					}
					if (val) {
						Ext.getCmp('FP_V13').setValue(val);
						Ext.getCmp('FP_V12').setValue(val.substr(0, val.length
										- (item.data['length'])));
						Ext.getCmp('FP_N2').setValue(item.data['length']);
						Ext.getCmp('FP_N1').setValue(val.substr(val.length
										- (item.data['length']), val.length));
					}
				}
			},
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
				click : function() {
					me.FormUtil
							.onAdd('addFeePleaseYZSYSQ', '新增印章申请单',
									'jsps/oa/fee/feePleaseYZSYSQ.jsp?whoami=FeePlease!YZSYSQ');
				}
			},
			'erpUpdateButton' : {
				click : function(btn) {
					this.FormUtil.onUpdate(this);
				}
			},
			'erpDeleteButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('fp_statuscode');
					if (status && status.value != 'ENTERING') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onDelete((Ext.getCmp('fp_id').value));
				}
			},
			'erpAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('fp_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					this.FormUtil.onAudit(Ext.getCmp('fp_id').value);
				}
			},
			'erpResAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('fp_statuscode');
					if (status && status.value != 'AUDITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					this.FormUtil.onResAudit(Ext.getCmp('fp_id').value);
				}
			},
			'erpSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('fp_statuscode');
					if (status && status.value != 'ENTERING') {
						btn.hide();
					}
				},
				click : function(btn) {
					this.FormUtil.onSubmit(Ext.getCmp('fp_id').value);

				}
			},
			'erpResSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('fp_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					this.FormUtil.onResSubmit(Ext.getCmp('fp_id').value);
				}
			},
			'erpPrintButton' : {
				click : function(btn) {
					var reportName = '';
					var kind = Ext.getCmp('fp_kind').value;
					if (kind == "印章申请单") {
						reportName = "AccountRegZW_yzsq";
					}
					var condition = '{FeePlease.fp_id}='
							+ Ext.getCmp('fp_id').value + '';
					var id = Ext.getCmp('fp_id').value;
					me.FormUtil.onwindowsPrint(id, reportName, condition);
				}
			},
			'erpEndButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('fp_statuscode');
					if (status && status.value == 'FINISH') {
						btn.hide();
					}
				},
				click : function(btn) {
					this.FormUtil.onEnd(Ext.getCmp('fp_id').value);

				}
			},
			'erpResEndButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('fp_statuscode');
					if (status && status.value != 'FINISH') {
						btn.hide();
					}
				},
				click : function(btn) {
					this.FormUtil.onResEnd(Ext.getCmp('fp_id').value);
				}
			},
			'erpConfirmButton' : {
				afterrender : function(btn) {
					var statu = Ext.getCmp('fp_statuscode');
					if (statu && statu.value != 'AUDITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.onConfirm(Ext.getCmp('fp_id').value);

				}
			},
			'erpDeleteDetailButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('fp_statuscode');
					if (status && status.value != 'ENTERING') {
						btn.disabled = true;
					}
				}
			},
			'erpTurnFYBXButton' : {
				afterrender : function(btn) {
					var statu = Ext.getCmp('fp_statuscode');
					if (statu && statu.value != 'AUDITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					if (Ext.getCmp('sealTurnFYBXWin')) {
						Ext.getCmp('sealTurnFYBXWin').show();
					} else {
						Ext.create('Ext.window.Window', {
							id : 'sealTurnFYBXWin',
							title : '转费用报销',
							closeAction : 'hide',
							width : 300,
							height : 250,
							maximizable : true,
							buttonAlign : 'center',
							layout : 'anchor',
							buttons : [{
								text : $I18N.common.button.erpConfirmButton,
								height : 26,
								handler : function(b) {
									me.sealTurnFYBX(Ext
											.getCmp('thisturnamount').value);
									var w = b.ownerCt.ownerCt;
									w.close();
								}
							}, {
								text : $I18N.common.button.erpCancelButton,
								height : 26,
								handler : function(b) {
									var w = b.ownerCt.ownerCt;
									w.close();
								}
							}],
							items : [{
								fieldLabel : '申请金额',
								xtype : 'numberfield',
								readOnly : true,
								value : Ext.getCmp('fp_pleaseamount') ? Ext
										.getCmp('fp_pleaseamount').value : 0
							}, {
								fieldLabel : '批准总额',
								xtype : 'numberfield',
								readOnly : true,
								value : Ext.getCmp('fp_n3') ? Ext
										.getCmp('fp_n3').value : 0
							}, {
								fieldLabel : '已转金额',
								xtype : 'numberfield',
								readOnly : true,
								value : Ext.getCmp('fp_n4') ? Ext
										.getCmp('fp_n4').value : 0
							}, {
								id : 'thisturnamount',
								fieldLabel : '本次转金额',
								xtype : 'numberfield',
								allowBlank : false,
								value : (Ext.getCmp('fp_n3').value - Ext
										.getCmp('fp_n4').value).toFixed(3)
							}]
						}).show();
					}

				}
			}
		});
	},

	sealTurnFYBX : function(thisturnamount) {
		var me = this;
		if (me.BaseUtil.numberFormat(thisturnamount, 2)
				+ me.BaseUtil.numberFormat(Ext.getCmp('fp_n4').value, 2) > me.BaseUtil
				.numberFormat(Ext.getCmp('fp_n3').value, 2)) {
			showMessage("警告", '本次转金额超出剩余金额!');
			return;
		}
		if (thisturnamount <= 0) {
			showMessage("警告", '本次转金额应大于0!');
			return;
		}
		//me.FormUtil.getActiveTab().setLoading(true);// loading...
		Ext.Ajax.request({
					url : basePath + 'oa/feeplease/sealTurnFYBX.action',
					params : {
						caller : caller,
						id : Ext.getCmp('fp_id').value,
						thisturnamount : thisturnamount
					},
					method : 'post',
					callback : function(options, success, response) {
						//me.FormUtil.getActiveTab().setLoading(false);
						window.location.reload();
						var localJson = new Ext.decode(response.responseText);
						if (localJson.exceptionInfo) {
							showError(localJson.exceptionInfo);
						}
						if (localJson.success) {
							if (localJson.log) {
								showMessage("提示", localJson.log);
							}
						}
					}
				});

	},
	onGridItemClick : function(selModel, record) {// grid行选择		
		if (Ext.getCmp('fileform')) {
			Ext.getCmp('fileform').setDisabled(false);
		}
		this.GridUtil.onGridItemClick(selModel, record);
	},
	getForm : function(btn) {
		return btn.ownerCt.ownerCt;

	}
});