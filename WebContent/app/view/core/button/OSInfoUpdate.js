/**
 * 委外信息修改按钮
 */	
Ext.define('erp.view.core.button.OSInfoUpdate',{ 
	extend : 'Ext.Button',
	alias : 'widget.erpOSInfoUpdateButton',
	iconCls : 'x-button-icon-submit',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpOSInfoUpdateButton,
	style : {
		marginLeft : '10px'
	},
	width : 140,
	initComponent : function() {
		this.callParent(arguments);
		this.BaseUtil = Ext.create('erp.util.BaseUtil');
	},
	listeners: {
		afterrender: function(btn) {
			var me = this;
			var status = Ext.getCmp('ma_statuscode'),
			    checkstatus = Ext.getCmp('ma_checkstatuscode');
			if(status && (status.value == 'ENTERING' || status.value == 'FINISH')){
				btn.hide();
			}
			me.BaseUtil.getSetting('Make', 'updateInfoNeedApprove', function(bool) {
				if(bool) {
					if(checkstatus && checkstatus.value == 'APPROVE'){
						btn.hide();
					}
				}
            });
		}
	},
	handler: function() {
		var me = this, win = Ext.getCmp('Complaint-win');
		me.BaseUtil.getSetting('Make', 'osInfoEditable', function(editable) {
			if(!win) {
				var vc = Ext.getCmp('ma_vendcode'), vn = Ext.getCmp('ma_vendname'),
					pr = Ext.getCmp('ma_price'), cr = Ext.getCmp('ma_currency'),tr = Ext.getCmp('ma_taxrate'),
					pac = Ext.getCmp('ma_paymentscode'), pan = Ext.getCmp('ma_payments'),ser=Ext.getCmp('ma_servicer'),
					remark = Ext.getCmp('ma_remark'), apvendcode = Ext.getCmp('ma_apvendcode'), apvendname = Ext.getCmp('ma_apvendname'),
					val1 = vc ? vc.value : '', val2 =  vn ? vn.value : '', val3 =  pr ? pr.value : '',
					val4 = cr ? cr.value : '', val5 =  tr ? tr.value : '', val6 = pac ? pac.value : '',
					val7 = pan ? pan.value : '',val8 = ser ? ser.value : '',val9 = remark ? remark.value : ''
				    val10 = apvendcode ? apvendcode.value : val1, val11 = apvendname ? apvendname.value : val2;
				win = Ext.create('Ext.Window', {
					id: 'Complaint-win',
					title: '更新委外单 ' + Ext.getCmp('ma_code').value + ' 的委外信息',
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
							fieldLabel: '委外商号',
							name:'ma_vendcode',
							allowBlank: false,
							value: val1,
							listeners:{
								aftertrigger:function(t, d){
									t.ownerCt.down('textfield[name=ma_vendname]').setValue(d.data.ve_name);
									t.ownerCt.down('textfield[name=ma_apvendcode]').setValue(d.data.ve_apvendcode);
									t.ownerCt.down('textfield[name=ma_apvendname]').setValue(d.data.ve_apvendname);
									t.ownerCt.down('textfield[name=ma_currency]').setValue(d.data.ve_currency);
									t.ownerCt.down('textfield[name=ma_paymentscode]').setValue(d.data.ve_paymentcode);
									t.ownerCt.down('textfield[name=ma_payments]').setValue(d.data.ve_payment);
									t.ownerCt.down('textfield[name=ma_taxrate]').setValue(d.data.ve_taxrate);
								}
							}
						},{
							margin: '3 0 0 0',
							xtype: 'textfield',
							fieldLabel: '委外商名',
							readOnly:true,
							name:'ma_vendname',
							allowBlank: false,
							value: val2
						},{
							margin: '10 0 0 0',
							xtype: 'dbfindtrigger',
							fieldLabel: '应付供应商号',
							name:'ma_apvendcode',
							allowBlank: false,
							value: val10,
							listeners:{
								aftertrigger:function(t, d){
									t.ownerCt.down('textfield[name=ma_apvendname]').setValue(d.data.ve_name);
								}
							}
						},{
							margin: '3 0 0 0',
							xtype: 'textfield',
							fieldLabel: '应付供应商名',
							readOnly:true,
							name:'ma_apvendname',
							allowBlank: false,
							value: val11
						},{
							margin: '3 0 0 0',
							xtype: 'dbfindtrigger',
							fieldLabel: '币别',
							name:'ma_currency',
							readOnly:false,
							allowBlank: false,
							value: val4,
							listeners:{
								aftertrigger:function(t, d){
									t.ownerCt.down('textfield[name=ma_taxrate]').setValue(d.data.cr_taxrate);
								}
							}
						},{
							margin: '3 0 0 0',
							xtype: 'textfield',
							fieldLabel: '税率',
							name:'ma_taxrate',
							readOnly:true,
							allowBlank: false,
							value: val5,
							listeners:{
								afterrender:function(t, d){
								  t.ownerCt.down('textfield[name=ma_taxrate]').setReadOnly(!editable);
								}
							}
						},{
							margin: '3 0 0 0',
							xtype: 'textfield',
							fieldLabel: '加工单价',
							name:'ma_price',
							readOnly:false,
							allowBlank: false,
							value: val3,
							listeners:{
								afterrender:function(t, d){
								  t.ownerCt.down('textfield[name=ma_price]').setReadOnly(!editable);
								}
							}
						},{
							margin: '3 0 0 0',
							xtype: 'erpYnField',
							fieldLabel: '是否免费加工',
							name:'ma_servicer',
							value: val8,
							hidden:!ser
						},{
							margin: '3 0 0 0',
							xtype: 'dbfindtrigger',
							fieldLabel: '付款方式编号',
							name:'ma_paymentscode',
							allowBlank: false,
							readOnly:false,
							value: val6,
							listeners:{
								afterrender:function(t, d){
								  t.ownerCt.down('textfield[name=ma_paymentscode]').setReadOnly(!editable);
								},
								aftertrigger:function(t, d){
									t.ownerCt.down('textfield[name=ma_payments]').setValue(d.data.pa_name);
								}
							}
						},{
							margin: '3 0 0 0',
							xtype: 'textfield',
							fieldLabel: '付款方式',
							name:'ma_payments',
							allowBlank: false,
							readOnly:true,
							value: val7
						},{
							margin: '3 0 0 0',
							xtype: 'textfield',
							name:'ma_remark',
							fieldLabel: '备注',
							value: val9
						}],
						closeAction: 'hide',
						buttonAlign: 'center',
						layout: {
							type: 'vbox',
							align: 'center'
						},
						buttons: [{
							text: "按供应商取价",
							cls: 'x-btn-blue',
							listeners:{
								afterrender:function(btn){
									me.BaseUtil.getSetting('Make', 'osInfoGetPriceByVendor', function(bool) {
										if(!bool) {
											btn.hide();
										}
				                    });
								}
							},
							handler: function(btn) {
								Ext.Ajax.request({
									url: basePath + '/scm/purchase/getVendorPrice.action',
									params: {
										id: Ext.getCmp('ma_id').value,
										vendcode: Ext.getCmp('ma_vendcode').value,
										curr: Ext.getCmp('ma_currency').value
									},
									callback: function(opt, s, r) {
										var rs = Ext.decode(r.responseText);
										if(rs.exceptionInfo) {
											showError(rs.exceptionInfo);
										} else {
											Ext.Msg.alert("提示","获取成功！");
											window.location.reload();
										}
									}
								});
							}
						},{
							text: $I18N.common.button.erpConfirmButton,
							cls: 'x-btn-blue',
							formBind: true,//form.isValid() == false时,按钮disabled
							listeners:{
								afterrender:function(btn){
										if(!editable) {
											btn.hide();
										}
								}
							},
							handler: function(btn) {
								var form = btn.ownerCt.ownerCt,
									a = form.down('dbfindtrigger[name=ma_vendcode]'),
									b = form.down('dbfindtrigger[name=ma_currency]'),
									c = form.down('textfield[name=ma_taxrate]'),
									d = form.down('textfield[name=ma_price]'),
									e = form.down('textfield[name=ma_paymentscode]'),
									f = form.down('textfield[name=ma_payments]'),
									g = form.down('textfield[name=ma_servicer]'),
									h = form.down('textfield[name=ma_remark]'),
									i = form.down('dbfindtrigger[name=ma_apvendcode]');
									if(!Ext.isNumeric(c.value)){
										showError("税率必须是数字");
										return ;
									}
									if(!Ext.isNumeric(d.value)){
										showError("单价必须是数字");
										return ;
									}
								if(form.getForm().isDirty()) {
									me.updateOSInfoVendor(Ext.getCmp('ma_id').value, a.value, b.value, c.value, d.value, e.value, f.value,g.value,h.value,i.value);
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
        });
		
	},
	updateOSInfoVendor: function(id, val1, val2, val3, val4, val5, val6, ser, h, i) {
		Ext.Ajax.request({
			url: basePath + 'pm/make/updateOSInfoVendor.action',
			params: {
				caller : caller,
				id: id,
				vend: val1,
				curr: val2,
				taxr: val3,
				price: val4,
				paymc: val5,
				paym: val6,
				ma_servicer: ser,
				remark: h,
				apvend: i
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