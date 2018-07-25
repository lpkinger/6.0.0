Ext.QuickTips.init();
Ext.define('erp.controller.hr.wage.wageStandard', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : [ 'core.form.Panel', 'hr.wage.wageStandard', 'core.grid.Panel2',
			'core.toolbar.Toolbar', 'core.form.MultiField', 'core.button.Save',
			'core.button.Add', 'core.button.Submit', 'core.button.Print',
			'core.button.Upload', 'core.button.ResAudit', 'core.button.Audit',
			'core.button.Close', 'core.button.Delete', 'core.button.Update',
			'core.button.DeleteDetail', 'core.button.ResSubmit',
			'core.button.TurnStorage', 'core.button.TurnCheck',
			'core.trigger.DbfindTrigger', 'core.trigger.TextAreaTrigger',
			'core.trigger.MultiDbfindTrigger' ],
	init : function() {
		var me = this;
		me.allowinsert = true;
		this.control({
			'erpGridPanel2' : {
				itemclick : this.onGridItemClick
			},

			'erpSaveButton' : {
				click : function(btn) {
					var form = me.getForm(btn);
					if (Ext.getCmp(form.codeField).value == null
							|| Ext.getCmp(form.codeField).value == '') {
						me.BaseUtil.getRandomNumber();// 自动添加编号
					}
					// 保存之前的一些前台的逻辑判定
					this.beforeSave();
				}
			},
			'erpDeleteButton' : {
				click : function(btn) {
					me.FormUtil.onDelete(Ext.getCmp('ws_id').value);
				}
			},
			'erpUpdateButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('ws_statuscode');
					if (status && status.value != 'ENTERING'
							&& status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.beforeUpdate();
				}
			},
			'erpAddButton' : {
				click : function() {
					me.FormUtil.onAdd('addWageStandard', '新增薪资标准',
							'jsps/hr/wage/wagestandard.jsp');
				}
			},
			'erpSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('ws_statuscode');
					if (status && status.value != 'ENTERING') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onSubmit(Ext.getCmp('ws_id').value);
				}
			},
			'erpResSubmitButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('ws_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResSubmit(Ext.getCmp('ws_id').value);
				}
			},
			'erpAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('ws_statuscode');
					if (status && status.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onAudit(Ext.getCmp('ws_id').value);
				}
			},
			'erpResAuditButton' : {
				afterrender : function(btn) {
					var status = Ext.getCmp('ws_statuscode');
					if (status && status.value != 'AUDITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResAudit(Ext.getCmp('ws_id').value);
				}
			},
			'erpCloseButton' : {
				click : function(btn) {
					me.FormUtil.beforeClose(me);
				}
			}
		});
	},
	onGridItemClick : function(selModel, record) {// grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
	getForm : function(btn) {
		return btn.ownerCt.ownerCt;
	},

	beforeSave : function() {
		var bool = true;
		// var grid = Ext.getCmp('grid');
		// var items = grid.store.data.items, whcode =
		// Ext.getCmp('va_whcode').value;
		// Ext.each(items, function(item){
		// if(item.dirty && item.data[grid.necessaryField] != null &&
		// item.data[grid.necessaryField] != ""){
		// if(item.data['vad_whcode'] == null){
		// item.set('vad_whcode', whcode);
		// }
		// if(item.data['vad_qty'] == null || item.data['vad_qty'] == '' ||
		// item.data['vad_qty'] == '0' || item.data['vad_qty'] == 0){
		// bool = false;
		// showError('明细表第' + item.data['vad_detno'] + '行的数量为空或0');return;
		// }
		// }
		// });
		if (bool) {
			this.FormUtil.beforeSave(this);
		}
	},
	beforeUpdate : function() {
		var bool = true;
		// var grid = Ext.getCmp('grid');
		// var items = grid.store.data.items, whcode =
		// Ext.getCmp('va_whcode').value;
		// Ext.each(items, function(item){
		// if(item.dirty && item.data[grid.necessaryField] != null &&
		// item.data[grid.necessaryField] != ""){
		// if(item.data['vad_whcode'] == null){
		// item.set('vad_whcode', whcode);
		// }
		// if(item.data['vad_qty'] == null || item.data['vad_qty'] == '' ||
		// item.data['vad_qty'] == '0' || item.data['vad_qty'] == 0){
		// bool = false;
		// showError('明细表第' + item.data['vad_detno'] + '行的数量为空或0');return;
		// }
		// }
		// });
		if (bool) {
			this.FormUtil.onUpdate(this);
		}
	}
});