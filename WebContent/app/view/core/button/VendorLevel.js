/**
 * 修改供应商等级按钮
 */
Ext.define('erp.view.core.button.VendorLevel', {
	extend : 'Ext.Button',
	alias : 'widget.erpVendorLevelButton',
	iconCls : 'x-button-icon-submit',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpVendorLevelButton,
	style : {
		marginLeft : '10px'
	},
	width : 120,
	initComponent : function() {
		this.callParent(arguments);
	},
	listeners: {
		afterrender: function(btn) {
			var status = Ext.getCmp('ve_auditstatuscode');
			if(status && status.value == 'ENTERING'){
				btn.hide();
			}
		}
	},
	handler: function() {
		var me = this, win = Ext.getCmp('vendorlevel-win');
		if(!win) {
			var f = Ext.getCmp('ve_level'),
				val = f ? f.value : '';			
			win = Ext.create('Ext.Window', {
				id: 'vendorlevel-win',
				title: '设置供应商 ' + Ext.getCmp('ve_code').value + ' 的级别',
				height: 200,
				width: 400,
				items: [{
					margin: '30 0 0 0',
					xtype: 'combobox',
					allowBlank: false,
					editable: false,
					fieldLabel: '供应商级别',					
					store:f.store,								
					displayField: 'display',
					valueField: 'value',
					queryMode: 'local',
					value:val
					
					
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
						var tx = btn.ownerCt.ownerCt.down('combobox');
						if(tx.isDirty() && !Ext.isEmpty(tx.value)) {
							me.updateVendorUU(Ext.getCmp('ve_id').value, tx.value);
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
	updateVendorUU: function(id, val) {
		Ext.Ajax.request({
			url: basePath + 'scm/vendor/updateLevel.action',
			params: {
				id: id,
				ve_level: val
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