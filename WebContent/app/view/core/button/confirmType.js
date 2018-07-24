/**
 * 银行登记-确认类型按钮
 */	
Ext.define('erp.view.core.button.confirmType',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpConfirmTypeButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id: 'erpConfirmTypeButton',
    	text: $I18N.common.button.erpConfirmTypeButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		listeners: {
			afterrender: function(btn) {
				var me = this;
				var status = Ext.getCmp('ar_statuscode'), artype = Ext.getCmp('ar_type');
				if(status && status.value == 'POSTED'){
					btn.hide();
				}
				var sourcetype = Ext.getCmp('ar_sourcetype');
				if(sourcetype && !Ext.isEmpty(sourcetype.value)){
					if(sourcetype.value =='费用报销单' || sourcetype.value =='总务申请单'){
						btn.show();
					} else {
						btn.hide();
					}
				}
 				if(artype.value=='应收票据收款' || artype.value=='应付票据付款' || artype.value=='保理收款' || artype.value=='保理付款'){
 					btn.hide();
 				}
			}
		},
		handler:function(url){
			var me = this, win = Ext.getCmp('confirmType-win');
			var states = Ext.create('Ext.data.Store', {
			    fields: ['abbr', 'name'],
			    data : [
			        {"abbr":"暂收款", "name":"暂收款"},
			        {"abbr":"应收退款", "name":"应收退款"},
			        {"abbr":"预付款", "name":"预付款"},
			        {"abbr":"应付款", "name":"应付款"},
			        {"abbr":"预收款", "name":"预收款"},
			        {"abbr":"其它收款", "name":"其它收款"},
			        {"abbr":"其它付款", "name":"其它付款"},
			        {"abbr":"应收款", "name":"应收款"},
			        {"abbr":"费用", "name":"费用"},
			        {"abbr":"转存", "name":"转存"},
			        {"abbr":"应付退款", "name":"应付退款"},
			        {"abbr":"预付退款", "name":"预付退款"},
			        {"abbr":"预收退款", "name":"预收退款"}
			    ]
			});
			if(!win) {
				var custcode = Ext.getCmp('ar_custcode'), 
					custname = Ext.getCmp('ar_custname'),
					sellercode = Ext.getCmp('ar_sellercode'),
					sellername = Ext.getCmp('ar_sellername'), 
					arapcurrency = Ext.getCmp('ar_arapcurrency'),
					araprate = Ext.getCmp('ar_araprate'),
					aramount = Ext.getCmp('ar_aramount'),
					vendcode = Ext.getCmp('ar_vendcode'),
					vendname = Ext.getCmp('ar_vendname'),
					category = Ext.getCmp('ar_category'),
					description = Ext.getCmp('ca_description'),
					precurrency = Ext.getCmp('ar_precurrency'),
					prerate = Ext.getCmp('ar_prerate'),
					preamount = Ext.getCmp('ar_preamount'),
					type = Ext.getCmp('ar_type'),
					deposit = Ext.getCmp('ar_deposit'),
					payment = Ext.getCmp('ar_payment'),
					errstring = Ext.getCmp('ar_errstring'),
					
					custcode = custcode ? custcode.value : '', 
					custname =  custname ? custname.value : '',
					sellercode =  sellercode ? sellercode.value : '',
					sellername =  sellername ? sellername.value : '',
					arapcurrency =  arapcurrency ? arapcurrency.value : '',
					araprate =  araprate ? araprate.value : 0,
					aramount =  aramount ? aramount.value : 0,
					vendcode =  vendcode ? vendcode.value : '',
					vendname =  vendname ? vendname.value : '',
					category =  category ? category.value : '',
					description =  description ? description.value : '',
					precurrency =  precurrency ? precurrency.value : '',
					prerate =  prerate ? prerate.value : 0,
					preamount =  preamount ? preamount.value : 0,
					deposit = deposit ? deposit.value : 0,
					payment = payment ? payment.value : 0,
				    type = type ? type.value : '',
				    errstring = errstring ? errstring.value : '';		
					
				win = Ext.create('Ext.Window', {
					id: 'confirmType-win',
					title: '确认单据编号 ' + Ext.getCmp('ar_code').value + ' 的类型',
					height: 400,
					width: 400,
					layout: 'anchor',
					items: [{
						xtype: 'form',
						cls: 'form-custom',
						anchor: '100% 100%',
						autoScroll:true,
						bodyStyle: 'background:#f1f2f5;',
						items: [{
							margin: '3 0 0 0',
							xtype: 'combo',
							id:'war_type',
							name:'war_type',
							fieldLabel: '类型',
						    store: states,
						    queryMode: 'local',
						    displayField: 'name',
						    valueField: 'abbr',
						    value:type,
						    listeners : {
						    	change: function(m){
						    		m.ownerCt.hideColumns(m);
									var f = m.ownerCt, s = f.down('field[name=war_sellercode]');
									if (s) {
										if(m.value == '应收款') {
											s.allowBlank = false;
										} else {
											s.allowBlank = true;
										}
									}
						    	}
						    }
						},{
							margin: '10 0 0 0',
							xtype: 'dbfindtrigger',
							fieldLabel: '客户编号',
							id:'war_custcode',
							name:'war_custcode',
							allowBlank: false,
							value: custcode,
							listeners:{
								
							}
						},{
							margin: '3 0 0 0',
							xtype: 'textfield',
							fieldLabel: '客户名称',
							readOnly:true,
							id:'war_custname',
							name:'war_custname',
							allowBlank: false,
							value: custname
						},{
							margin: '10 0 0 0',
							xtype: 'dbfindtrigger',
							fieldLabel: '业务员编号',
							id:'war_sellercode',
							name:'war_sellercode',
							allowBlank: false,
							value: sellercode,
							listeners:{
								
							}
						},{
							margin: '3 0 0 0',
							xtype: 'textfield',
							fieldLabel: '业务员名称',
							readOnly:true,
							id:'war_sellername',
							name:'war_sellername',
							allowBlank: false,
							value: sellername
						},{
							margin: '3 0 0 0',
							xtype: 'dbfindtrigger',
							fieldLabel: '冲账币别',
							id:'war_arapcurrency',
							name:'war_arapcurrency',
							allowBlank: false,
							value: arapcurrency
						},{
							margin: '3 0 0 0',
							xtype: 'textfield',
							fieldLabel: '冲账汇率',
							id:'war_araprate',
							name:'war_araprate',
							readOnly:true,
							allowBlank: false,
							value: araprate
						},{
							margin: '3 0 0 0',
							xtype: 'textfield',
							fieldLabel: '冲账金额',
							id:'war_aramount',
							name:'war_aramount',
							allowBlank: false,
							value: aramount,
							listeners:{
								change:function(m){
									var f = m.ownerCt;
									me.changecmrate(f);
							    }
							}
						},{
							margin: '3 0 0 0',
							xtype: 'textfield',
							fieldLabel: '收入金额',
							id:'war_deposit',
							name:'war_deposit',
							readOnly:false,
							allowBlank: false,
							value: deposit,
							listeners:{
								change:function(m){
									var f = m.ownerCt;
									me.changecmrate(f);
							    }
							}
						},{
							margin: '3 0 0 0',
							xtype: 'textfield',
							fieldLabel: '支出金额',
							id:'war_payment',
							name:'war_payment',
							readOnly:false,
							allowBlank: false,
							value: payment,
							listeners:{
								change:function(m){
									var f = m.ownerCt;
									me.changecmrate(f);
							    }
							}
						},{
							margin: '10 0 0 0',
							xtype: 'dbfindtrigger',
							fieldLabel: '供应商号',
							id:'war_vendcode',
							name:'war_vendcode',
							allowBlank: false,
							value: vendcode
						},{
							margin: '3 0 0 0',
							xtype: 'textfield',
							fieldLabel: '供应商名',
							readOnly:true,
							id:'war_vendname',
							name:'war_vendname',
							readOnly:true,
							allowBlank: false,
							value: vendname
						},{
							xtype: 'dbfindtrigger',
							fieldLabel: '转存到',
							id:'war_category',
							name:'war_category',
							allowBlank: false,
							value: category
						},{
							xtype: 'textfield',
							fieldLabel: '科目描述',
							readOnly:true,
							id:'wca_description',
							name:'wca_description',
							readOnly:true,
							allowBlank: false,
							value: description
						},{
							xtype: 'dbfindtrigger',
							fieldLabel: '转存币别',
							id:'war_precurrency',
							name:'war_precurrency',
							allowBlank: false,
							value: precurrency
						},{
							xtype: 'textfield',
							fieldLabel: '转存汇率',
							id:'war_prerate',
							name:'war_prerate',
							readOnly:true,
							allowBlank: false,
							value: prerate
						},{
							xtype: 'textfield',
							fieldLabel: '转存金额',
							id:'war_preamount',
							name:'war_preamount',
							allowBlank: false,
							value: preamount
						},{
							xtype: 'textfield',
							fieldLabel: '提示信息',
							id:'war_errstring',
							name:'war_errstring',
							allowBlank: true,
							value: errstring
						}],
						buttonAlign: 'center',
						layout: {
							type: 'vbox',
							align: 'center'
						},
						buttons: [{
							text: $I18N.common.button.erpConfirmButton,
							cls: 'x-btn-blue',
							handler: function(btn) {
								var form = btn.ownerCt.ownerCt;//war_type
								if(form.getForm().isDirty()) {
									warnMsg('是否要保存修改?', function(btn){
										if(btn == 'yes'){
											me.updateType(form);
										} else {
											return;
										}
									});
								} else {
									window.location.reload();
								}
							}
						}, {
							text: $I18N.common.button.erpCloseButton,
							cls: 'x-btn-blue',
							handler: function(btn) {
								btn.up('window').hide();
							}
						}],
						hideColumns: function(m){
							if(!Ext.isEmpty(m.getValue())) {
								var form = m.ownerCt;
								if(m.value == '应付款' || m.value == '预付款' || m.value == '预付退款'|| m.value == '应付退款'){
									form.down('#war_custcode').hide();
									form.down('#war_custname').hide();
									form.down('#war_sellercode').hide();
									form.down('#war_sellername').hide();
									form.down('#war_arapcurrency').show();
									form.down('#war_araprate').show();
									form.down('#war_aramount').show();
									form.down('#war_vendcode').show();
									form.down('#war_vendname').show();
									form.down('#war_category').hide();
									form.down('#wca_description').hide();
									form.down('#war_precurrency').hide();
									form.down('#war_prerate').hide();
									form.down('#war_preamount').hide();
									form.down('#war_errstring').hide();
									if(m.value == '应付款' || m.value == '预付款'){
										form.down('#war_deposit').hide();
										form.down('#war_payment').show();
									} else if(m.value == '预付退款'|| m.value == '应付退款'){
										form.down('#war_deposit').show();
										form.down('#war_payment').hide();
									}
								} else if(m.value == '应收款' || m.value == '预收款' || m.value == '预收退款' || m.value == '应收退款'){
									form.down('#war_custcode').show();
									form.down('#war_custname').show();
									form.down('#war_sellercode').show();
									form.down('#war_sellername').show();
									form.down('#war_arapcurrency').show();
									form.down('#war_araprate').show();
									form.down('#war_aramount').show();
									form.down('#war_vendcode').hide();
									form.down('#war_vendname').hide();
									form.down('#war_category').hide();
									form.down('#wca_description').hide();
									form.down('#war_precurrency').hide();
									form.down('#war_prerate').hide();
									form.down('#war_preamount').hide();
									form.down('#war_errstring').hide();
									if(m.value == '应收款' || m.value == '预收款'){
										form.down('#war_payment').hide();
										form.down('#war_deposit').show();
									} else if(m.value == '预收退款' || m.value == '应收退款'){
										form.down('#war_payment').show();
										form.down('#war_deposit').hide();
									}
								} else if(m.value == '应收票据收款' || m.value == '应付票据付款'){
									form.down('#war_custcode').show();
									form.down('#war_custname').show();
									form.down('#war_sellercode').show();
									form.down('#war_sellername').show();
									form.down('#war_arapcurrency').show();
									form.down('#war_araprate').show();
									form.down('#war_aramount').show();
									form.down('#war_vendcode').hide();
									form.down('#war_vendname').hide();
									form.down('#war_category').hide();
									form.down('#wca_description').hide();
									form.down('#war_precurrency').hide();
									form.down('#war_prerate').hide();
									form.down('#war_preamount').hide();
									form.down('#war_arapcurrency').hide();
									form.down('#war_araprate').hide();
									form.down('#war_aramount').hide();
									form.down('#war_errstring').hide();
									if(m.value == '应收票据收款'){
										form.down('#war_payment').hide();
										form.down('#war_deposit').show();
									} else if (m.value == '应付票据付款'){
										form.down('#war_payment').show();
										form.down('#war_deposit').hide();
									}
								} else if(m.value == '转存'){
									form.down('#war_custcode').hide();
									form.down('#war_custname').hide();
									form.down('#war_sellercode').hide();
									form.down('#war_sellername').hide();
									form.down('#war_arapcurrency').hide();
									form.down('#war_araprate').hide();
									form.down('#war_aramount').hide();
									form.down('#war_vendcode').hide();
									form.down('#war_vendname').hide();
									form.down('#war_category').show();
									form.down('#wca_description').show();
									form.down('#war_precurrency').show();
									form.down('#war_prerate').show();
									form.down('#war_preamount').show();
									form.down('#war_payment').show();
									form.down('#war_deposit').hide();
									form.down('#war_errstring').hide();
								} else if(m.value == '其它付款' || m.value == '其它收款'){
									form.down('#war_custcode').hide();
									form.down('#war_custname').hide();
									form.down('#war_sellercode').hide();
									form.down('#war_sellername').hide();
									form.down('#war_arapcurrency').hide();
									form.down('#war_araprate').hide();
									form.down('#war_aramount').hide();
									form.down('#war_vendcode').hide();
									form.down('#war_vendname').hide();
									form.down('#war_errstring').hide();
									if(m.value == '其它付款'){
										form.down('#war_deposit').hide();
										form.down('#war_payment').show();
									}
									if(m.value == '其它收款'){
										form.down('#war_deposit').show();
										form.down('#war_payment').hide();
									}
									form.down('#war_category').hide();
									form.down('#wca_description').hide();
									form.down('#war_precurrency').hide();
									form.down('#war_prerate').hide();
									form.down('#war_preamount').hide();
								} else{ 
									form.down('#war_custcode').hide();
									form.down('#war_custname').hide();
									form.down('#war_sellercode').hide();
									form.down('#war_sellername').hide();
									form.down('#war_arapcurrency').hide();
									form.down('#war_araprate').hide();
									form.down('#war_aramount').hide();
									form.down('#war_vendcode').hide();
									form.down('#war_vendname').hide();
									form.down('#war_category').hide();
									form.down('#wca_description').hide();
									form.down('#war_precurrency').hide();
									form.down('#war_prerate').hide();
									form.down('#war_preamount').hide();
									form.down('#war_payment').show();
									form.down('#war_deposit').hide();
									form.down('#war_errstring').hide();
								}
							}
						},
						listeners:{
							afterrender:function(form){
								var t = form.down('#war_type');
								this.hideColumns(t);
							}
						}
					}]
				});
			}
			win.show();
		},
		updateType:function(form){
			var type = form.down("#war_type").value;
			if(type == '应付款' || type == '预付款' || type == '应付退款' || type == '预付退款'){
				form.down('#war_custcode').value = '';
				form.down('#war_custname').value = '';
				form.down('#war_sellercode').value = '';
				form.down('#war_sellername').value = '';
				form.down('#war_category').value = '';
				form.down('#wca_description').value = '';
				form.down('#war_precurrency').value = '';
				form.down('#war_prerate').value = '0';
				form.down('#war_preamount').value = '0';
				form.down('#war_errstring').value = '';
				if(type == '应付款' || type == '预付款'){
					form.down('#war_deposit').value = '0';
				} else if(type == '预付退款'|| type == '应付退款'){
					form.down('#war_payment').value = '0';
				}
			} else if(type == '应收款' || type == '预收款' || type == '预收退款' || type == '应收退款'){
				form.down('#war_vendcode').value = '';
				form.down('#war_vendname').value = '';
				form.down('#war_category').value = '';
				form.down('#wca_description').value = '';
				form.down('#war_precurrency').value = '';
				form.down('#war_prerate').value = '0';
				form.down('#war_preamount').value = '0';
				form.down('#war_errstring').value = '';
				if(type == '应收款' || type == '预收款'){
					form.down('#war_payment').value = '0';
				} else if(type == '预收退款' || type == '应收退款'){
					form.down('#war_deposit').value = '0';
				}
			} else if(type == '应收票据收款' || type == '应付票据付款'){
				form.down('#war_vendcode').value = '';
				form.down('#war_vendname').value = '';
				form.down('#war_category').value = '';
				form.down('#wca_description').value = '';
				form.down('#war_precurrency').value = '';
				form.down('#war_prerate').value = '0';
				form.down('#war_preamount').value = '0';
				form.down('#war_arapcurrency').value = '';
				form.down('#war_araprate').value = '0';
				form.down('#war_aramount').value = '0';
				form.down('#war_errstring').value = '';
				if(type == '应收票据收款'){
					form.down('#war_payment').value = '0';
				} else if (type == '应付票据付款'){
					form.down('#war_deposit').value = '0';
				}
			} else if(type == '转存'){
				form.down('#war_custcode').value = '';
				form.down('#war_custname').value = '';
				form.down('#war_sellercode').value = '';
				form.down('#war_sellername').value = '';
				form.down('#war_arapcurrency').value = '';
				form.down('#war_araprate').value = '0';
				form.down('#war_aramount').value = '0';
				form.down('#war_vendcode').value = '';
				form.down('#war_vendname').value = '';
				form.down('#war_errstring').value = '';
				if(type == '应收票据收款'){
					form.down('#war_payment').value = '0';
				} else if (type == '应付票据付款'){
					form.down('#war_deposit').value = '0';
				}
			} else if(type == '其它付款' || type == '其它收款'){
				form.down('#war_custcode').value = '';
				form.down('#war_custname').value = '';
				form.down('#war_sellercode').value = '';
				form.down('#war_sellername').value = '';
				form.down('#war_arapcurrency').value = '';
				form.down('#war_araprate').value = '0';
				form.down('#war_aramount').value = '0';
				form.down('#war_vendcode').value = '';
				form.down('#war_vendname').value = '';
				form.down('#war_errstring').value = '';
				if(type == '其它收款'){
					form.down('#war_payment').value = '0';
				} else if (type == '其它付款'){
					form.down('#war_deposit').value = '0';
				}
			} else{ 
				form.down('#war_custcode').value = '';
				form.down('#war_custname').value = '';
				form.down('#war_sellercode').value = '';
				form.down('#war_sellername').value = '';
				form.down('#war_arapcurrency').value = '';
				form.down('#war_araprate').value = '0';
				form.down('#war_aramount').value = '0';
				form.down('#war_vendcode').value = '';
				form.down('#war_vendname').value = '';
				form.down('#war_category').value = '';
				form.down('#wca_description').value = '';
				form.down('#war_precurrency').value = '';
				form.down('#war_prerate').value = 0;
				form.down('#war_preamount').value = 0;
				form.down('#war_deposit').value = '0';
				form.down('#war_errstring').value = '';
			}
		var custcode = form.down('#war_custcode').value,
			custname = form.down('#war_custname').value,
			sellercode = form.down('#war_sellercode').value,
			sellername = form.down('#war_sellername').value,
			arapcurrency = form.down('#war_arapcurrency').value,
			araprate = form.down('#war_araprate').value
			aramount = form.down('#war_aramount').value,
			vendcode = form.down('#war_vendcode').value,
			vendname = form.down('#war_vendname').value,
			category = form.down('#war_category').value,
			description = form.down('#wca_description').value,
			precurrency = form.down('#war_precurrency').value,
			prerate = form.down('#war_prerate'),
			preamount = form.down('#war_preamount').value;
			payment = form.down('#war_payment').value,
			deposit = form.down('#war_deposit').value;
		
		var ar_accountcurrency = Ext.getCmp('ar_accountcurrency').value,
			ar_arapcurrency = arapcurrency,
			ar_araprate = araprate,	//冲账汇率
			f = form.down('#war_aramount');
		var grid = Ext.getCmp('grid'), items = grid.store.data.items;
		var debit = 0, credit = 0;
		Ext.each(items,function(item,index){
			if(!Ext.isEmpty(item.data['ard_explanation'])|| !Ext.isEmpty(item.data['ard_catecode'])){
				debit= debit + Number(item.data['ard_debit']);
				credit= credit + Number(item.data['ard_credit']);
			}
		});
		if(type != null && type != '' ){
			if(type == '应收款' || type == '预收款' ||  type == '应付退款'  || type == '其它收款'){
				if(deposit == 0 || deposit == null || deposit == '' ){
					showError('收入金额不能为空!');
					return;
				}
			} else if (type == '预付款' || type == '应付款' ||  type == '应收退款'  || type == '其它付款'|| type == '费用'){
				if(payment == 0 || payment == null || payment == '' ){
					showError('支出金额不能为空!');
					return;
				}
			}
			if(type != '应收票据收款'  && type != '应付票据付款'){
				if(ar_accountcurrency == ar_arapcurrency){
					if(ar_araprate != 1){
						showError('冲账汇率不正确,不能提交!');
						return;
					}
				}
			}
			if(type == '应收票据收款'){
				if(Ext.Number.toFixed(Ext.Number.from(deposit,0), 2)!= Ext.Number.toFixed(Ext.Number.from(credit,0)-Ext.Number.from(debit,0), 2)){
					showError('收入金额' + deposit + '与从表金额' + (Ext.Number.from(credit,0)-Ext.Number.from(debit,0)) + '不一致，不能提交!');
					return;
				}
			}
			if(type == '应付票据付款'){
				if(Ext.Number.toFixed(Ext.Number.from(payment,0), 2)!= Ext.Number.toFixed(Ext.Number.from(debit,0)-Ext.Number.from(credit,0), 2)){
					showError('支出金额' + payment + '与从表金额' + (Ext.Number.from(debit,0)-Ext.Number.from(credit,0)) + '不一致，不能提交!');
					return;
				}
			}
			if(type == '转存'){
				if(!Ext.isEmpty(payment) && payment != 0){
					var rate = Ext.Number.toFixed((preamount+debit)/payment,12);
					if(prerate.value != rate ){
						prerate.setValue(rate);
					}
				}
			}
		}

			var params = {
					custcode:custcode,
					custname:custname,
					sellercode:sellercode,
					sellername:sellername,
					arapcurrency:arapcurrency,
					araprate:araprate,
					aramount:aramount,
					vendcode:vendcode,
					vendname:vendname,
					category:category,
					description:description,
					precurrency:precurrency,
					prerate:prerate.value,
					preamount:preamount,
					payment:payment,
					deposit:deposit,
					id:Ext.getCmp('ar_id').value,
					type:type
			};
			Ext.Ajax.request({
				url: basePath + 'fa/gs/updateType.action',
				params: params,
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
		},
		//冲账汇率计算  = 冲账金额/预收金额
	    changecmrate: function(form){
	    	var type = form.down("#war_type").value;
	    	var cmamount = Ext.Number.from(form.down('#war_aramount').value, 0);
	    	var rbamount = 0.0;
			if(type == '应收款' || type == '预收款' || type == '预收退款' || type == '应收退款'){
				var rbamount = 0.0;
				if(type == '应收款' || type == '预收款'){
					rbamount = Ext.Number.from(form.down('#war_deposit').value, 0);
				}
				if(type == '预收退款' || type == '应收退款'){
					rbamount = Ext.Number.from(form.down('#war_payment').value, 0);
				}
			}
			if(type == '应付款' || type == '预付款' || type == '预付退款' || type == '应付退款'){
				if(type == '应付款' || type == '预付款'){
					rbamount = Ext.Number.from(form.down('#war_payment').value, 0);
				}
				if(type == '预付退款' || type == '应付退款'){
					rbamount = Ext.Number.from(form.down('#war_deposit').value, 0);
				}
			}
			if(rbamount != 0){
    			if(cmamount !=0 ){
    				form.down('#war_araprate').setValue(Ext.Number.toFixed(cmamount/rbamount, 8));
    			}
    		}
	    }
	});