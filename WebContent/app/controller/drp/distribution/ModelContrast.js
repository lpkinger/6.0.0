Ext.QuickTips.init();
Ext.define('erp.controller.drp.distribution.ModelContrast', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : [ 'core.form.Panel', 'drp.distribution.ModelContrast',
			'core.grid.Panel2', 'core.toolbar.Toolbar', 'core.button.Save',
			'core.button.Close', 'core.button.Update', 'core.button.Add','core.button.Sync',
			'core.button.DeleteDetail', 'core.trigger.DbfindTrigger' ],
	init : function() {
		var me = this;
		me.allowinsert = true;
		this.control({
			'erpGridPanel2' : {
				itemclick : this.onGridItemClick
			/*	afterrender : function(grid) {
					grid.plugins[0].on('beforeedit', function(args){
						var field = args.field, y = args.rowIdx,
							grid = args.grid, record = args.record;
						var x = grid.store.find('cp_isdefault', '是');
						if (x > -1 && x != y) {
							record.set('cp_isdefault', '否');
							if(field == 'cp_isdefault')
								return false;
							return true;
						}
						return true;
					});
				}*/
			},
			'erpAddButton' : {
				click : function() {
					me.FormUtil.onAdd('addModelContrast', '新增型号对照表',
							'jsps/drp/distribution/modelContrast.jsp');
				}
			},
			'erpSaveButton' : {
				click : function(btn) {
					var grid = Ext.getCmp('grid');
					var pr_id = Ext.getCmp('pr_id').value;
					var pr_code=Ext.getCmp('pr_code').value;
					var pr_name=Ext.getCmp('pr_detail').value;
					Ext.Array.each(grid.store.data.items, function(item) {
						item.set('mr_prid', pr_id);
						item.set('mr_prcode', pr_code);
						item.set('mr_prname', pr_name);
					});
					this.FormUtil.onUpdate(this);
				}
			},
			'erpUpdateButton' : {
				click : function(btn) {
					var grid = Ext.getCmp('grid');
					var pr_id = Ext.getCmp('pr_id').value;
					var pr_code=Ext.getCmp('pr_code').value;
					var pr_name=Ext.getCmp('pr_detail').value;
					Ext.Array.each(grid.store.data.items, function(item) {
						item.set('mr_prid', pr_id);
						item.set('mr_prcode', pr_code);
						item.set('mr_prname', pr_name);
					});
					this.FormUtil.onUpdate(this);
				}
			},
			'erpCloseButton' : {
				click : function(btn) {
					me.FormUtil.beforeClose(me);
				}
			},
			'dbfindtrigger[name=pr_code]' : {
				aftertrigger : function() {
					var id = Ext.getCmp('pr_id').value;
					if (id != null & id != '') {
						this.getPaymentsStore('mc_prid=' + id);
					}
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
	getPaymentsStore : function(condition) {
		var me = this;
		var grid = Ext.getCmp('grid');
		grid.store.removeAll(false);
		me.BaseUtil.getActiveTab().setLoading(true);// loading...
		Ext.Ajax.request({// 拿到grid的columns
			url : basePath + "common/singleGridPanel.action",
			params : {
				caller : "ModelContrast",
				condition : condition
			},
			method : 'post',
			callback : function(options, success, response) {
				me.BaseUtil.getActiveTab().setLoading(false);
				var res = new Ext.decode(response.responseText);
				if (res.exceptionInfo) {
					showError(res.exceptionInfo);
					return;
				}
				var data = [];
				if (!res.data || res.data.length == 2) {
					me.GridUtil.add10EmptyItems(grid);
				} else {
					data = Ext.decode(res.data.replace(/,}/g, '}').replace(
							/,]/g, ']'));
					if (data.length > 0) {
						grid.store.loadData(data);
					}
				}
				/*var bool = false;
				grid.store.each(function(item) {
					if (item.get('cp_paymentcode') == paymentcode) {
						bool = true;
					}
				});
				if (!bool) {
					var items = grid.store.data.items, item = null;
					for ( var i in items) {
						item = items[i];
						if (Ext.isEmpty(item.get('cp_paymentcode'))) {
							item.set('cp_paymentcode', paymentcode);
							item.set('cp_payment', payment);
							item.set('cp_isdefault', '是');
							break;
						}
					}
				}*/
			}
		});
	}
});