/**
 * 确认收入按钮
 */	
Ext.define('erp.view.core.button.ConfirmIncome',{ 
	extend : 'Ext.Button',
	alias : 'widget.erpConfirmIncomeButton',
	iconCls : 'x-button-icon-submit',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpConfirmIncomeButton,
	style : {
		marginLeft : '10px'
	},
	width : 110,
	initComponent : function() {
		this.callParent(arguments);
		this.BaseUtil = Ext.create('erp.util.BaseUtil');
	},
	listeners: {
		afterrender: function(btn) {
			var me = this;
			var status = Ext.getCmp('pfr_statuscode');
			if(status && (status.value == 'ENTERING' || status.value == 'FINISH')){
				btn.hide();
			}
		}
	},
	handler: function() {
		var me = this, win = Ext.getCmp('Complaint-win');
		if(!win) {
			var prjcode = Ext.getCmp('pfr_prjcode'), prjname = Ext.getCmp('pfr_prjname'),
			    cukind = Ext.getCmp('prj_cukind'),
				val1 = prjcode ? prjcode.value : '', val2 =  prjname ? prjname.value : '',
				val3 = cukind ? cukind.value : '';
			win = Ext.create('Ext.Window', {
				id: 'Complaint-win',
				title: '确认收入',
				height: 400,
				width: 400,
				items: [{
					xtype: 'form',
					height: '100%',
					width: '100%',
					bodyStyle: 'background:#f1f2f5;',
					items: [{
						margin: '10 0 0 0',
						xtype: 'dbfindtrigger',
						fieldLabel: '项目编号',
						name:'pc_prjcode',
						allowBlank: false,
						value: val1,
						listeners:{
							aftertrigger:function(t, d){
								t.ownerCt.down('textfield[name=pc_prjname]').setValue(d.get('prj_name'));
								t.ownerCt.down('textfield[name=pc_cukind]').setValue(d.get('prj_cukind'));
							}
						}
					},{
						margin: '3 0 0 0',
						xtype: 'textfield',
						fieldLabel: '项目名称',
						readOnly:true,
						name:'pc_prjname',
						allowBlank: false,
						value: val2
					},{
						margin: '3 0 0 0',
						xtype: 'textfield',
						fieldLabel: '客户类别',
						name:'pc_cukind',
						readOnly:true,
						value: val3
					},{
						margin: '3 0 0 0',
						xtype: 'textfield',
						fieldLabel: '确认收入金额',
						name:'pc_incomeamount',
						readOnly:false,
						allowBlank: false,
						value: 0
					},{
						margin: '3 0 0 0',
						xtype: 'textfield',
						fieldLabel: '已开票金额',
						name:'pc_billamount',
						readOnly:false,
						allowBlank: false,
						value: 0
					},{
						margin: '3 0 0 0',
						xtype: 'textfield',
						fieldLabel: '退预收金额',
						name:'pc_prebackamount',
						readOnly:false,
						allowBlank: false,
						value: 0
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
							var form = btn.ownerCt.ownerCt;
								/*a = form.down('dbfindtrigger[name=ma_vendcode]'),
								b = form.down('dbfindtrigger[name=ma_currency]'),
								c = form.down('textfield[name=ma_taxrate]'),
								d = form.down('textfield[name=ma_price]'),
								e = form.down('textfield[name=ma_paymentscode]'),
								f = form.down('textfield[name=ma_payments]');
								g = form.down('textfield[name=ma_servicer]');
								h = form.down('textfield[name=ma_remark]');		
								if(!Ext.isNumeric(c.value)){
									showError("税率必须是数字");
									return ;
								}
								if(!Ext.isNumeric(d.value)){
									showError("单价必须是数字");
									return ;
								}*/
							if(form.getForm().isDirty()) {
								me.updateOSInfoVendor(Ext.getCmp('ma_id').value, a.value, b.value, c.value, d.value, e.value, f.value,g.value,h.value);
							}
						}
					}, {
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
	updateOSInfoVendor: function(id, val1, val2, val3, val4, val5, val6, ser, h, i) {
		Ext.Ajax.request({
			url: basePath + 'pm/make/updateOSInfoVendor.action',
			params: {
				id: id,
				vend: val1,
				curr: val2,
				taxr: val3,
				price: val4,
				paymc: val5,
				paym: val6,
				ma_servicer: ser,
				remark: h
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