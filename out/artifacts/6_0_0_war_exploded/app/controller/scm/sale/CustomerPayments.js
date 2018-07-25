Ext.QuickTips.init();
Ext.define('erp.controller.scm.sale.CustomerPayments', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : [ 'core.form.Panel', 'scm.sale.CustomerPayments',
			'core.grid.Panel2', 'core.toolbar.Toolbar', 'core.button.Save',
			'core.button.Close', 'core.button.Update', 'core.button.Add','core.button.Sync',
			'core.button.DeleteDetail', 'core.trigger.DbfindTrigger' ],
	init : function() {
		var me = this;
		me.allowinsert = true;
		this.control({
			'erpGridPanel2' : {
				itemclick : this.onGridItemClick,
				afterrender : function(grid) {
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
				}
			},
			'erpAddButton' : {
				click : function() {
					me.FormUtil.onAdd('addCustomerPayments', '新增客户收款方式',
							'jsps/scm/sale/customerPayments.jsp');
				}
			},
			'erpSaveButton' : {
				click : function(btn) {
					var grid = Ext.getCmp('grid');
					var cuid = Ext.getCmp('cu_id').value;
					var i = 0;
					Ext.Array.each(grid.store.data.items, function(item) {
						item.set('cp_cuid', cuid);
					});
					Ext.Array.each(grid.store.data.items, function(item) {
						if (item.data.cp_isdefault == '是') {
							i++;
						}
					});
					if (i == 0) {
						Ext.Msg.alert("提示", "请选择默认收款方式!");
						return;
					}
					if (i > 1) {
						Ext.Msg.alert("提示", "默认收款方式只能选择一个,请重新选择!");
						return;
					}
					this.FormUtil.onUpdate(this);
				}
			},
			'erpUpdateButton' : {
				click : function(btn) {
					this.FormUtil.onUpdate(this);
				}
			},
			'erpCloseButton' : {
				click : function(btn) {
					me.FormUtil.beforeClose(me);
				}
			},
			'dbfindtrigger[name=cu_code]' : {
				afterrender: function(t){
    				t.setEditable(false);
				},
				aftertrigger : function() {
					var id = Ext.getCmp('cu_id').value;
					var form=Ext.getCmp('form');
    				var grid =Ext.getCmp('grid');
					if (id != null & id != '') {
						var formCondition = form.keyField + "IS" + id ;
						var gridCondition = grid.mainField + "IS" + id;;
	    				window.location.href = basePath+'jsps/scm/sale/customerPayments.jsp'+ '?formCondition=' + 
								formCondition + '&gridCondition=' + gridCondition;
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
	getPaymentsStore : function(condition, paymentcode, payment) {
		var me = this;
		var grid = Ext.getCmp('grid');
		grid.store.removeAll(false);
		me.BaseUtil.getActiveTab().setLoading(true);// loading...
		Ext.Ajax.request({// 拿到grid的columns
			url : basePath + "common/singleGridPanel.action",
			params : {
				caller : "CustomerPayments",
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
				var bool = false;
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
				}
			}
		});
	}
});