/**
 * 委外信息修改按钮
 */	
Ext.define('erp.view.core.button.UpdateProdInoutrate',{ 
	extend : 'Ext.Button',
	alias : 'widget.UpdateProdInoutrateButton',
	iconCls : 'x-button-icon-submit',
	cls : 'x-btn-gray',
	//text : $I18N.common.button.erpOSVendorUpdateButton,
	style : {
		marginLeft : '10px'
	},
	width : 140,
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
		var me = this, win = Ext.getCmp('Complaint-win');
		if(!win) {
			var vc = Ext.getCmp('ma_vendcode'), vn = Ext.getCmp('ma_vendname'),
				pr = Ext.getCmp('ma_price'), cr = Ext.getCmp('ma_currency'),tr = Ext.getCmp('ma_taxrate'),
				pac = Ext.getCmp('ma_paymentscode'), pan = Ext.getCmp('ma_payments'),
				val1 = vc ? vc.value : '', val2 =  vn ? vn.value : '', val3 =  pr ? pr.value : '',
				val4 = cr ? cr.value : '', val5 =  tr ? tr.value : '', val6 = pac ? pac.value : '',
				val7 = pan ? pan.value : '';
			win = Ext.create('Ext.Window', {
				id: 'Complaint-win',
				title: '更新委外单 ' + Ext.getCmp('ma_code').value + ' 的委外信息',
				height: 300,
				width: 400,
				items: [{
					margin: '10 0 0 0',
					xtype: 'dbfindtrigger',
					fieldLabel: '委外商号',
					name:'ma_vendcode',
					value: val1,
					listeners:{
						aftertrigger:function(t, d){
							t.ownerCt.down('textfield[name=ma_vendname]').setValue(d.get('ve_name'));
						}
					}
				},{
					margin: '3 0 0 0',
					xtype: 'textfield',
					fieldLabel: '委外商名',
					readOnly:true,
					name:'ma_vendname',
					value: val2
				},{
					margin: '3 0 0 0',
					xtype: 'dbfindtrigger',
					fieldLabel: '币别',
					name:'ma_currency',
					value: val4
				},{
					margin: '3 0 0 0',
					xtype: 'textfield',
					fieldLabel: '税率',
					name:'ma_taxrate',
					value: val5
				},{
					margin: '3 0 0 0',
					xtype: 'textfield',
					fieldLabel: '加工单价',
					name:'ma_price',
					value: val3
				},{
					margin: '3 0 0 0',
					xtype: 'dbfindtrigger',
					fieldLabel: '付款方式编号',
					name:'ma_paymentscode',
					value: val6,
					listeners:{
						aftertrigger:function(t, d){
							t.ownerCt.down('textfield[name=ma_payments]').setValue(d.get('pa_name'));
						}
					}
				},{
					margin: '3 0 0 0',
					xtype: 'textfield',
					fieldLabel: '付款方式',
					name:'ma_payments',
					readOnly:true,
					value: val7
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
							a = form.down('dbfindtrigger[name=ma_vendcode]'),
							b = form.down('dbfindtrigger[name=ma_currency]'),
							c = form.down('textfield[name=ma_taxrate]'),
							d = form.down('textfield[name=ma_price]'),
							e = form.down('textfield[name=ma_paymentscode]'),
							f = form.down('textfield[name=ma_payments]');
						if((a.isDirty() && !Ext.isEmpty(a.value)) || (b.isDirty() && !Ext.isEmpty(b.value)) || (
								c.isDirty() && !Ext.isEmpty(c.value)) ||(d.isDirty() && !Ext.isEmpty(d.value))) {
							me.updateOSVendor(Ext.getCmp('ma_id').value, a.value, b.value, c.value, d.value, e.value, f.value);
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
	updateOSVendor: function(id, val1, val2, val3, val4, val5, val6) {
		Ext.Ajax.request({
			url: basePath + 'pm/make/updateOSVendor.action',
			params: {
				id: id,
				vend: val1,
				curr: val2,
				taxr: val3,
				price: val4,
				paymc: val5,
				paym: val6
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