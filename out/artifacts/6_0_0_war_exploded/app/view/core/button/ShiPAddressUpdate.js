/**
 * 更新交货地点按钮
 */	
Ext.define('erp.view.core.button.ShiPAddressUpdate',{ 
	extend : 'Ext.Button',
	alias : 'widget.erpShiPAddressUpdateButton',
	iconCls : 'x-button-icon-submit',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpShiPAddressUpdateButton,
	style : {
		marginLeft : '10px'
	},
	width : 140,
	initComponent : function() {
		this.callParent(arguments);
	},
	listeners: {
		afterrender: function(btn) {
			var me = this;
		}
	},
	handler: function() {
		var me = this, win = Ext.getCmp('ShiPAddress-win');
		if(!win) {
			var address=Ext.getCmp('ma_shipaddresscode'),
			val1 = address ? address.value:'';
			
			win = Ext.create('Ext.Window', {
				id: 'ShiPAddress-win',
				title: '更新委外单 ' + Ext.getCmp('ma_code').value + ' 的交货地点',
				height: 100,
				width: 400,
				buttonAlign: 'center',
				items: [{
					xtype: 'form',
					height: '100%',
					width: '100%',
					bodyStyle: 'background:#f1f2f5;',
					items: [{
						margin: '10 0 0 0',
						xtype: 'dbfindtrigger',
						fieldLabel: '交货地点',
						name:'ma_shipaddresscode',
						allowBlank: false,
						value: val1,
						width: 350
					}],
					buttons: [{
						text: $I18N.common.button.erpConfirmButton,
						cls: 'x-btn-blue',
						handler: function(btn) {
							var form = btn.ownerCt.ownerCt,
								a = form.down('dbfindtrigger[name=ma_shipaddresscode]');
							if(form.getForm().isDirty()) {
								me.updateShiPAddress(Ext.getCmp('ma_id').value, a.value);
							}
						}
					},{
						text: $I18N.common.button.erpCloseButton,
						cls: 'x-btn-blue',
						handler: function(btn) {
							btn.up('window').hide();
						}
					}]
				}]
			});
		}
		win.show();
	},
	updateShiPAddress: function(id, val1, i) {
		Ext.Ajax.request({
			url: basePath + 'pm/make/updateShiPAddress.action',
			params: {
				id: id,
				address: val1,
				caller: caller
			},
			callback: function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				} else {
					Ext.Msg.alert("提示","更新成功！");
					window.location.reload();
				}
			}
		});
	}
});