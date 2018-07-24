/**
 * pmc回复日期修改按钮
 */	
Ext.define('erp.view.core.button.PMCUpdate',{ 
	extend : 'Ext.Button',
	alias : 'widget.erpPMCUpdateButton',
	iconCls : 'x-button-icon-submit',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpPMCUpdateButton,
	style : {
		marginLeft : '10px'
	},
	width : 140,
	initComponent : function() {
		this.callParent(arguments);
	},
	listeners: {
		afterrender: function(btn) {
			var status = Ext.getCmp('ve_statuscode');
			if(status && status.value == 'ENTERING'){
				btn.hide();
			}
		}
	},
	handler: function() {
		var me = this, win = Ext.getCmp('PMC-win');
		if(!win) {
			var f = Ext.getCmp('ve_makedate'),
				val = f ? f.value : '';
			win = Ext.create('Ext.Window', {
				id: 'PMC-win',
				title: '更新客户验货单 ' + Ext.getCmp('ve_code').value + ' 的PMC回复日期',
				height: 200,
				width: 400,
				items: [{
					margin: '30 0 0 0',
					xtype: 'datefield',
					fieldLabel: 'PMC回复日期',
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
			url: basePath + 'scm/qc/updatePMC.action',
			params: {
				id: id,
				pmc: val
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