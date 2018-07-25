Ext.QuickTips.init();
Ext.define('erp.controller.crm.marketmgr.resourcemgr.ResourceDistr', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : [ 'crm.marketmgr.resourcemgr.ResourceDistr', 'core.form.Panel',
			'core.grid.Panel2', 'core.toolbar.Toolbar', 'core.grid.YnColumn',
			 'core.button.Add','core.button.Sync',
			 'core.button.Save', 'core.button.Close',
			 'core.button.Upload', 'core.button.Update',
			'core.button.Delete', 
			 'core.trigger.DbfindTrigger',
			'core.form.YnField', 'core.button.DeleteDetail',
			'core.button.Upload', 'core.form.FileField',
			'core.trigger.MultiDbfindTrigger' ],
	init : function() {
		var me = this;
		this.control({
			'erpGridPanel2' : {
				itemclick : this.onGridItemClick
			},
			'erpSaveButton' : {
				click : function(btn) {
					me.GridUtil.onSave(Ext.getCmp('grid'));
				}
			},
			/*'dbfindtrigger[name=cd_sellercode]' : {
				focus : function(t) {
					t.setHideTrigger(false);
					t.setReadOnly(false);//用disable()可以，但enable()无效
					 //   				var record = Ext.getCmp('grid').selModel.getLastSelected();
					var pr = record.data['cd_sellercode'];
					if (pr == null || pr == '') {
						showError("请先选择客户!");
						t.setHideTrigger(true);
						t.setReadOnly(true);
					} else {
						t.dbBaseCondition = "cu_code='" + pr + "'";
					}
				}
			},*/
			'field[name=pr_id]' : {
				change : function(f) {
					if (f.value != null && f.value != '') {
						window.location.href = window.location.href
								.toString().split('?')[0]
								+ '?formCondition=pr_id='
								+ f.value
								+ '&gridCondition=rd_prid=' + f.value;
					} else {
						Ext.getCmp('deletebutton').hide();
						Ext.getCmp('updatebutton').hide();
					}
				}

			},
			'erpAddButton' : {
				click : function(btn) {
					me.FormUtil.onAdd('addAsign', '新增客户分配',
							'jsps/crm/marketmgr/resourcemgr/resourceDistr.jsp');
				}
			},
			'erpCloseButton' : {
				click : function(btn) {
					me.FormUtil.beforeClose(me);
				}
			},
			'erpUpdateButton' : {
				afterrender : function(btn) {
					if (Ext.getCmp('pr_id').value != null
							&& Ext.getCmp('pr_id').value != '') {
						btn.show();
					} else {
						btn.hide();
					}
				},
				click : function(btn) {
					/*var grid = Ext.getCmp('grid');
					Ext.each(grid.store.data.items, function(item) {
						if (item.dirty == true) {
							item.set('cd_sellercode', Ext.getCmp('cu_code').value);
						}
					});
					me.FormUtil.onUpdate(me);*/
					this.FormUtil.onUpdate(this);
				}
			},
			'erpDeleteButton' : {
				afterrender : function(btn) {
					if (Ext.getCmp('pr_id').value != null
							&& Ext.getCmp('pr_id').value != '') {
						btn.show();
					} else {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onDelete(Ext.getCmp('pr_id').value);
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