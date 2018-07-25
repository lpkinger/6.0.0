Ext.QuickTips.init();
Ext.define('erp.controller.fa.gs.AccountRegisterImport', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : [ 'fa.gs.AccountRegisterImport', 'core.form.Panel', 'core.grid.Panel2', 'core.toolbar.Toolbar', 
	          'core.button.Scan', 'core.button.Export', 'core.button.Save', 'core.button.Add',  'core.button.Upload', 
	          	'core.button.Close', 'core.button.Delete', 'core.button.CleanFailed', 'core.button.Update', 
	          	'core.button.DeleteDetail','core.button.ImportAll', 'core.button.CleanDetail',
	          'core.trigger.MultiDbfindTrigger','core.trigger.TextAreaTrigger', 'core.trigger.DbfindTrigger','core.form.YnField' ],
	init : function() {
		var me = this;
		this.control({
			'erpGridPanel2' : {
				itemclick : this.onGridItemClick
			},
			'erpSaveButton' : {
				click : function(btn) {
					this.FormUtil.beforeSave(this);
				}
			},
			'erpUpdateButton' : {
				click : function(btn) {
 				   	this.FormUtil.onUpdate(this);					
				}
			},
			'erpDeleteButton' : {
				click : function(btn) {
					me.FormUtil.onDelete(Ext.getCmp('em_id').value);			
				}
			},
			'erpAddButton': {
				click: function(){
					me.FormUtil.onAdd('addAccountRegisterImport', '新增银行登记批量导入', 'jsps/fa/gs/accountRegisterImport.jsp');
				}
			},
			'erpCloseButton' : {
				click : function(btn) {
					me.FormUtil.beforeClose(me);
				}
			},
			//清除明细
			'erpCleanDetailButton' : {
				click : function(btn) {
					me.cleanDetail();
				}
			},
			'erpCleanFailedButton' : {
				click : function(btn) {
					me.cleanFailed();
				}
			},
			'erpImportAllButton' : {
				click : function(btn) {			
					me.importAll();
				}
			}
		});
	},
	cleanDetail : function() {
		var grid = Ext.getCmp('grid');
		grid.setLoading(true);
		Ext.Ajax.request({
			url : basePath + 'fa/gs/cleanAccountRegisterImport.action',
			params : {
				id : Ext.getCmp('em_id').value
			},
			method : 'post',
			callback : function(options, success, response) {
				grid.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if (localJson.success) {
					showMessage('提示', '操作成功!', 1000);
					//update成功后刷新页面进入可编辑的页面
					window.location.reload();
				} else if (localJson.exceptionInfo) {
					showError(str);
					return;
				}
			}
		});

	},
	cleanFailed : function() {
		var grid = Ext.getCmp('grid');
		grid.setLoading(true);
		Ext.Ajax.request({
			url : basePath + 'fa/gs/cleanFailed.action',
			params : {
				id : Ext.getCmp('em_id').value
			},
			method : 'post',
			callback : function(options, success, response) {
				grid.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if (localJson.success) {
					showMessage('提示', '操作成功!', 1000);
					//update成功后刷新页面进入可编辑的页面
					window.location.reload();
				} else if (localJson.exceptionInfo) {
					showError(str);
					return;
				}
			}
		});

	},
	importAll : function() {
		//form里面数据
		var me = this;	
		Ext.Ajax.request({
			url : basePath + 'fa/gs/accountRegisterImport.action',
			params : {
				id : Ext.getCmp('em_id').value
			},
			method : 'post',
			callback : function(options, success, response) {
				me.FormUtil.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if (localJson.success) {
					showMessage('提示', '操作成功!', 1000);
					window.location.reload();
				} else if (localJson.exceptionInfo) {
					var str = localJson.exceptionInfo;
					showError(str);
					return;
				}
			}
		});
	},
	onGridItemClick : function(selModel, record) {//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
	getForm : function(btn) {
		return btn.ownerCt.ownerCt;
	}
});