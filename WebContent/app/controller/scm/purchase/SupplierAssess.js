Ext.QuickTips.init();
Ext.define('erp.controller.scm.purchase.SupplierAssess', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : [ 'scm.purchase.SupplierAssess', 'core.form.Panel',
			'core.form.FileField', 'core.form.MultiField',
			'core.button.TurnProject',// erpTurnProject
			'core.button.Save', 'core.button.Add', 'core.button.Submit',
			'core.button.Print', 'core.button.Upload', 'core.button.ResAudit',
			'core.button.Audit', 'core.button.Close', 'core.button.Delete',
			'core.button.Update', 'core.button.ResSubmit',
			'core.button.TurnCustomer', 'core.button.Flow','core.button.Refresh',
			'core.button.DownLoad', 'core.button.Scan',
			'common.datalist.Toolbar', 'core.trigger.DbfindTrigger',
			'core.trigger.TextAreaTrigger', 'core.form.YnField',
			'core.trigger.AutoCodeTrigger' ],
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
					// 保存之前的一些前台的逻辑判定
					this.FormUtil.beforeSave(this);
				}
			},
			'erpDeleteButton' : {
				click : function(btn) {
					me.FormUtil.onDelete(Ext.getCmp('sa_id').value);
				}
			},
			'erpUpdateButton' : {
				click : function(btn) {
					this.FormUtil.onUpdate(this);
				}
			},
			'erpSubmitButton' : {
				afterrender : function(btn) {
					var statu = Ext.getCmp('sa_statuscode');
					if (statu && statu.value != 'ENTERING') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onSubmit(Ext.getCmp('sa_id').value);
				}
			},
			'erpResSubmitButton' : {
				afterrender : function(btn) {
					var statu = Ext.getCmp('sa_statuscode');
					if (statu && statu.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResSubmit(Ext.getCmp('sa_id').value);
				}
			},
			'erpAuditButton' : {
				afterrender : function(btn) {
					var statu = Ext.getCmp('sa_statuscode');
					if (statu && statu.value != 'COMMITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onAudit(Ext.getCmp('sa_id').value);
				}
			},
			'erpRefreshButton':{
				click:function(){
					window.location.reload();
				}
			},
			'erpResAuditButton' : {
				afterrender : function(btn) {
					var statu = Ext.getCmp('sa_statuscode');
					if (statu && statu.value != 'AUDITED') {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onResAudit(Ext.getCmp('sa_id').value);
				}
			},
			'erpAddButton' : {
				click : function() {
					me.FormUtil.onAdd('addSupplierAssess', '新增物料调查评估表',
							'jsps/scm/purchase/supplierAssess.jsp');
				}
			},
			'erpTurnCustomerButton':{
				beforerender:function(btn){
					btn.setText('转新供应商引进');
					btn.setWidth(140);
				},
				afterrender:function(btn){
					var statu = Ext.getCmp('sa_statuscode');
					if (statu && statu.value != 'AUDITED') {
						btn.hide();
					}
				},
				click:function(){
					
				}
			},
			'erpCloseButton' : {
				click : function(btn) {
					me.FormUtil.beforeClose(me);
				}
			},
			'erpPrintButton': {
    			click: function(btn){
    				me.FormUtil.onPrint(Ext.getCmp('sa_id').value);
    			}
    		}
		});
	},
	getForm : function(btn) {
		return btn.ownerCt.ownerCt;
	}
});