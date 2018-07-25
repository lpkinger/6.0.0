/**
 * 修改供应商UU按钮
 */
Ext.define('erp.view.core.button.UpdateMakeRemark', {
	extend : 'Ext.Button',
	alias : 'widget.erpUpdateMakeRemarkButton',
	iconCls : 'x-btn-uu-medium',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpUpdateMakeRemarkButton,
	style : {
		marginLeft : '10px'
	},
	width : 120,
	initComponent : function() {
		this.callParent(arguments);
	},
	listeners: {
		afterrender: function(btn) {
			var status = Ext.getCmp('ma_statuscode');
			if(status && status.value == 'ENTERING'){
				btn.hide();
			}
		}
	},
	handler: function() {
		var me = this, win = Ext.getCmp('vendoruu-win');
		if(!win) {
			var f = Ext.getCmp('ma_remark'),
				val = f ? f.value : '';
			win = Ext.create('Ext.Window', {
				id: 'vendoruu-win',
				title: '更新备注',
				height: 200,
				width: 400,
				items: [{
					margin: '30 0 0 0',
					xtype: 'textfield',
					fieldLabel: '更新备注',
					value: val
				}],
				closeAction: 'hide',
				buttonAlign: 'center',
				layout: {
					type: 'vbox',
					align: 'center'
				},
				buttons: [{
					text: $I18N.common.button.erpConfirmButton,
					cls: 'x-btn-blue',
					handler: function(btn) {
						var tx = btn.ownerCt.ownerCt.down('textfield');
						if(tx.isDirty() && !Ext.isEmpty(tx.value)) {
							me.updateProductStatus(Ext.getCmp('ma_id').value, tx.value);
						}
					}
				}, {
					text: $I18N.common.button.erpCloseButton,
					cls: 'x-btn-blue',
					handler: function(btn) {
						btn.ownerCt.ownerCt.hide();
					}
				}]
			});
		}
		win.show();
	},
	updateProductStatus: function(id, val) {
		Ext.Ajax.request({
			url: basePath + 'pm/make/updateRemark.action',
			params: {
				id: id,
				value: val
			},
			callback: function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				} else {
					alert('设置成功!');
					window.location.reload();
				}
			}
		});
	}
});