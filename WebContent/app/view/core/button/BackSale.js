/**
 * 驳回订单按钮
 */
Ext.define('erp.view.core.button.BackSale', {
	extend : 'Ext.Button',
	alias : 'widget.erpBackSaleButton',
	iconCls : 'x-button-icon-submit',
	cls : 'x-btn-gray',
	text : '驳回订单',
	style : {
		marginLeft : '10px'
	},
	width : 90,
	initComponent : function() {
		this.callParent(arguments);
	},
	listeners: {
		afterrender: function(btn) {
			var status = Ext.getCmp('sa_statuscode');
			if(status && status.value != 'ENTERING'){
				btn.hide();
			}
		}
	},
	handler: function() {
		var me = this, win = Ext.getCmp('win');
		if(!win) {
			var f = Ext.getCmp('pr_level'),
				val = f ? f.value : ''; 
			win = Ext.create('Ext.Window', {
				id: 'win',
				title: '驳回订单',
				height: 200,
				width: 400,
				items: [{
					margin: '10 0 0 0',
					xtype: 'textareafield',
					name:'sa_backreason',
					height: 90,
					width: 350,
					fieldLabel: '<font color="red">驳回原因</font>', 
					readOnly:false,
					allowBlank: false,
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
						var reason=btn.ownerCt.ownerCt.down('textfield[name=sa_backreason]').value;
	 					if (reason==null || reason==''){
	 						showError('驳回原因必须填写。');
	 						return;
	 					}else{
	 						me.updateSaleStatus(Ext.getCmp('sa_id').value, reason);
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
	updateSaleStatus: function(id, val) {
		Ext.Ajax.request({
			url: basePath + 'scm/sale/updateSaleStatus.action',
			params: {
				id: id,
				value: val,
				caller : caller
			},
			callback: function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo) {
					showError(rs.exceptionInfo);
				} else {
					alert('驳回成功!');
					window.location.reload();
				}
			}
		});
	},
});