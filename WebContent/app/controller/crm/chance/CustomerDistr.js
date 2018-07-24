Ext.QuickTips.init();
Ext.define('erp.controller.crm.chance.CustomerDistr', {
	extend : 'Ext.app.Controller',
	FormUtil : Ext.create('erp.util.FormUtil'),
	GridUtil : Ext.create('erp.util.GridUtil'),
	BaseUtil : Ext.create('erp.util.BaseUtil'),
	views : [ 'crm.chance.CustomerDistr', 'core.form.Panel',
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
			'field[name=cu_id]' : {
				change : function(f) {
					if (f.value != null && f.value != '') {
						window.location.href = window.location.href
								.toString().split('?')[0]
								+ '?formCondition=cu_id='
								+ f.value
								+ '&gridCondition=cd_cuid=' + f.value;
					} else {
						Ext.getCmp('deletebutton').hide();
						Ext.getCmp('updatebutton').hide();
					}
				}

			},
			'erpAddButton' : {
				click : function(btn) {
					me.FormUtil.onAdd('addAsign', '新增客户分配',
							'jsps/crm/chance/customerDistr.jsp');
				}
			},
			'erpCloseButton' : {
				click : function(btn) {
					me.FormUtil.beforeClose(me);
				}
			},
			'erpUpdateButton' : {
				afterrender : function(btn) {
					if (Ext.getCmp('cu_id').value != null
							&& Ext.getCmp('cu_id').value != '') {
						btn.show();
					} else {
						btn.hide();
					}
				},
				click : function(btn) {
					var grid = Ext.getCmp('grid'), items = grid.store.data.items;
					var i=0;
					var bool=false;
					Ext.Array.each(items, function(item){
						if(item.data['cd_remark']!=null){
							bool=true
						}
				    	if (item.data['cd_remark'] == '是') {
							i++;
						}
					});
					if (i > 1&&bool) {
						showError('默认业务员只能选择一个,请重新选择!');
						return;
					}
				    if(i == 0&&bool){
				    	Ext.Msg.alert("提示","请选择默认业务员!");
				    	return;
				    }
					this.FormUtil.onUpdate(this);
				}
			},
			'erpDeleteButton' : {
				afterrender : function(btn) {
					if (Ext.getCmp('cu_id').value != null
							&& Ext.getCmp('cu_id').value != '') {
						btn.show();
					} else {
						btn.hide();
					}
				},
				click : function(btn) {
					me.FormUtil.onDelete(Ext.getCmp('cu_id').value);
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