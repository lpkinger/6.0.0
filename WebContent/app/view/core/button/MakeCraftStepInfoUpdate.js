/**
 * 工序委外单信息修改按钮
 */	
Ext.define('erp.view.core.button.MakeCraftStepInfoUpdate',{ 
	extend : 'Ext.Button',
	alias : 'widget.erpMakeCraftStepInfoUpdate',
	iconCls : 'x-button-icon-submit',
	cls : 'x-btn-gray',
	text : $I18N.common.button.erpMakeCraftStepInfoUpdate,
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
			var status = Ext.getCmp('mc_statuscode');
			if(status &&  status.value == 'AUDITED'){
				btn.hide();
			}
		}
	},
	handler: function() {
		var me = this, win = Ext.getCmp('Complaint-win'); 
		if(!win) {
			var vc = Ext.getCmp('mc_vendcode'), vn = Ext.getCmp('mc_vendname'),
				pr = Ext.getCmp('mc_price'), cr = Ext.getCmp('mc_currency'),tr = Ext.getCmp('mc_taxrate'),
				pac = Ext.getCmp('mc_paymentscode'), pan = Ext.getCmp('mc_payments'),ser=Ext.getCmp('mc_servicer'),
				remark = Ext.getCmp('mc_remark'), apvendcode = Ext.getCmp('mc_apvendcode'), apvendname = Ext.getCmp('mc_apvendname'),
				val1 = vc ? vc.value : '', val2 =  vn ? vn.value : '', val3 =  pr ? pr.value : '',
				val4 = cr ? cr.value : '', val5 =  tr ? tr.value : '', val6 = pac ? pac.value : '',
				val7 = pan ? pan.value : '',val8 = ser ? ser.value : '',val9 = remark ? remark.value : ''
			    val10 = apvendcode ? apvendcode.value : val1, val11 = apvendname ? apvendname.value : val2;
			win = Ext.create('Ext.Window', {
				id: 'Complaint-win',
				title: '更新工序委外单 ' + Ext.getCmp('mc_code').value + ' 的委外信息',
				height: 450,
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
						name:'mc_vendcode',
						allowBlank: false,
						labelStyle:'color:#FF0000',
						value: val1,
						listeners:{
							aftertrigger:function(t, d){
								t.ownerCt.down('textfield[name=mc_vendname]').setValue(d.data.ve_name);
								t.ownerCt.down('textfield[name=mc_apvendcode]').setValue(d.data.ve_apvendcode);
								t.ownerCt.down('textfield[name=mc_apvendname]').setValue(d.data.ve_apvendname);
								t.ownerCt.down('textfield[name=mc_currency]').setValue(d.data.ve_currency);
								t.ownerCt.down('textfield[name=mc_paymentscode]').setValue(d.data.ve_paymentcode);
								t.ownerCt.down('textfield[name=mc_payments]').setValue(d.data.ve_payment);
								t.ownerCt.down('textfield[name=mc_taxrate]').setValue(d.data.ve_taxrate);
							}
						}
					},{
						margin: '3 0 0 0',
						xtype: 'textfield',
						fieldLabel: '委外商名',
						readOnly:true,
						name:'mc_vendname',
						allowBlank: false,
						labelStyle:'color:#FF0000',
						value: val2
					},{
						margin: '10 0 0 0',
						xtype: 'dbfindtrigger',
						fieldLabel: '应付供应商号',
						name:'mc_apvendcode',
						allowBlank: false,
						labelStyle:'color:#FF0000',
						value: val10,
						listeners:{
							aftertrigger:function(t, d){
								t.ownerCt.down('textfield[name=mc_apvendname]').setValue(d.data.ve_name);
							}
						}
					},{
						margin: '3 0 0 0',
						xtype: 'textfield',
						fieldLabel: '应付供应商名',
						readOnly:true,
						name:'mc_apvendname',
						labelStyle:'color:#FF0000',
						allowBlank: false,
						value: val11
					},{
						margin: '3 0 0 0',
						xtype: 'dbfindtrigger',
						fieldLabel: '币别',
						name:'mc_currency',
						labelStyle:'color:#FF0000',
						readOnly:false,
						allowBlank: false,
						value: val4
					},{
						margin: '3 0 0 0',
						xtype: 'textfield',
						fieldLabel: '税率',
						name:'mc_taxrate',
						readOnly:true,
						labelStyle:'color:#FF0000',
						allowBlank: false,
						editable:true,
						readOnly:false,
						value: val5
					},{
						margin: '3 0 0 0',
						xtype: 'textfield',
						fieldLabel: '加工单价',
						name:'mc_price',
						readOnly:false,
						allowBlank: false,
						labelStyle:'color:#FF0000',
						value: val3
					},{
						margin: '3 0 0 0',
						xtype: 'erpYnField',
						fieldLabel: '是否免费加工',
						name:'mc_servicer',
						value: val8,
						hidden:!ser
					},{
						margin: '3 0 0 0',
						xtype: 'dbfindtrigger',
						fieldLabel: '付款方式编号',
						name:'mc_paymentscode',
						allowBlank: false,
						readOnly:false,
						labelStyle:'color:#FF0000',
						value: val6,
						listeners:{
							aftertrigger:function(t, d){
								t.ownerCt.down('textfield[name=mc_payments]').setValue(d.data.pa_name);
							}
						}
					},{
						margin: '3 0 0 0',
						xtype: 'textfield',
						fieldLabel: '付款方式',
						labelStyle:'color:#FF0000',
						name:'mc_payments',
						allowBlank: false,
						readOnly:true,
						value: val7
					},{
						margin: '3 0 0 0',
						xtype: 'textfield',
						name:'mc_remark',
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
						text: $I18N.common.button.erpConfirmButton,
						cls: 'x-btn-blue',
						formBind: true,
						handler: function(btn) {
							var form = btn.ownerCt.ownerCt,
								a = form.down('dbfindtrigger[name=mc_vendcode]'),
								b = form.down('dbfindtrigger[name=mc_currency]'),
								c = form.down('textfield[name=mc_taxrate]'),
								d = form.down('textfield[name=mc_price]'),
								e = form.down('textfield[name=mc_paymentscode]'),
								f = form.down('textfield[name=mc_payments]'),
								g = form.down('textfield[name=mc_servicer]'),
								h = form.down('textfield[name=mc_remark]'),
								i = form.down('dbfindtrigger[name=mc_apvendcode]');
								if(!Ext.isNumeric(c.value)){
									showError("税率必须是数字");
									return ;
								}
								if(!Ext.isNumeric(d.value)){
									showError("单价必须是数字");
									return ;
								}
							if(form.getForm().isDirty()) {
								me.updateOSInfoVendor(Ext.getCmp('mc_id').value, a.value, b.value, c.value, d.value, e.value, f.value,g.value,h.value,i.value);
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
			url: basePath + 'pm/mes/updateOSInfoVendor.action',
			params: {
				caller : caller,
				id: id,
				vend: val1,
				curr: val2,
				taxr: val3,
				price: val4,
				paymc: val5,
				paym: val6,
				mc_servicer: ser,
				remark: h,
				apvend: i
			},
			callback: function(opt, s, r) {
				var rs = Ext.decode(r.responseText);
				if(rs.exceptionInfo){
					var str = rs.exceptionInfo;
					if(str.trim().substr(0, 12) == 'AFTERSUCCESS'){//特殊情况:操作成功，但是出现警告,允许刷新页面
						//特殊情况:操作成功，但是出现警告,允许刷新页面
						str = str.replace('AFTERSUCCESS', '');
						var form = Ext.getCmp('form');
						//add成功后刷新页面进入可编辑的页面 
						var value = r[form.keyField];
						var formCondition = form.keyField + "IS" + value ;
						var gridCondition = '';
						var grid = Ext.getCmp('grid');
						if(grid && grid.mainField){
							gridCondition = grid.mainField + "IS" + value;
						}
						if(contains(window.location.href, '?', true) ){
							window.location.href = window.location.href + '&formCondition=' + 
							formCondition + '&gridCondition=' + gridCondition;
						} else {
							window.location.href = window.location.href + '?formCondition=' + 
							formCondition + '&gridCondition=' + gridCondition;
						}
					
						showError(str);
					}else {
						showError(str);
						return;
					}				
				}else{
					Ext.Msg.alert("提示","更新成功！");
					window.location.reload();
				}
			}
		});
	}
});