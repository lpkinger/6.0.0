/**
 * 更改开票日期按钮
 */	
Ext.define('erp.view.core.button.BillDateUpdate',{ 
	extend : 'Ext.Button',
	alias : 'widget.erpBillDateUpdateButton',
	iconCls : 'x-button-icon-submit',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpBillDateUpdateButton,
	style : {
		marginLeft : '10px'
	},
	width : 120,
	initComponent : function() {
		this.callParent(arguments);
	},
	listeners: {
		afterrender: function(btn) {
			var status = Ext.getCmp('ab_statuscode');
			if(status && status.value == 'POSTED'){
				btn.show();
			}else{
				btn.hide();
			}
		}
	},
	handler: function() {
		var me = this, win = Ext.getCmp('Complaint-win');
		if(!win) {
			var olddate = Ext.getCmp('ab_date'),
				val = olddate ? olddate.value : '';
			win = Ext.create('Ext.Window', {
				id: 'Complaint-win',
				title: '更新发票 ' + Ext.getCmp('ab_code').value + ' 的开票日期',
				height: 200,
				width: 400,
				items: [{
					margin: '10 0 0 0',
					xtype: 'datefield',
					fieldLabel: '原开票日期',
					readOnly:true,
					name:'ab_date',
					value: val
				},{
					margin: '3 0 0 0',
					xtype: 'datefield',
					fieldLabel: '新开票日期',
					name:'ab_datenew',
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
						var form = btn.ownerCt.ownerCt,
							nd = form.down('datefield[name=ab_datenew]');
						if(nd.isDirty() && !Ext.isEmpty(nd.value)) {
							me.updateBillDate(Ext.getCmp('ab_id').value, 
									Ext.Date.format(nd.value,'Y-m-d'), Ext.Date.format(nd.value,'Ym'));
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
	updateBillDate: function(id, val1, val2) {
		Ext.Ajax.request({
			url: basePath + 'fa/arp/updateBillDate.action',
			params: {
				id: id,
				date: val1,
				yearmonth: val2
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