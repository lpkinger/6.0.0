Ext.define('erp.view.ma.logic.Periods',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'grid',
				id: 'check-Periods',
				anchor: '100% 60%',
				tbar: [{
					cls: 'x-btn-blue',
					id: 'check',
					text: '检查',
					width: 80,
					margin: '0 0 0 200'
				},{
					cls: 'x-btn-blue',
					id: 'close',
					text: $I18N.common.button.erpCloseButton,
					width: 80,
					margin: '0 0 0 5'
				},'->'],
				columns: [{
					text: '',
					dataIndex: 'check',
					flex: 1,
					renderer: function(val, meta, record) {
						meta.tdCls = val;
						return '';
					}
				},{
					text: '检测项',
					dataIndex: 'value',
					flex: 10,
					renderer: function(val, meta, record) {
						if(record.get('check') == 'error') {
							meta.style = 'color: gray';
						}
						return val;
					}
				},{
					text: '',
					dataIndex: 'link',
					flex: 1,
					renderer: function(val, meta, record) {
						if(record.get('check') == 'error') {
							meta.tdCls = 'detail';
							return '详细情况';
						}
						return '';
					}
				}],
				columnLines: true,
				store: Ext.create('Ext.data.Store',{
					fields: [{name: 'action', type: 'string'}, {name: 'type', type: 'string'}, {name: 'value', type: 'string'}],
					data: [{
						action: 'ma/logic/per_chk.action',
						type: 'ar_chk_a',
						value: '应收账款期间 与 总账期间一致'
					},{
						action: 'ma/logic/per_chk.action',
						type: 'ar_chk_b',
						value: '当月的 出货单、退货单、发票、其它应收单、发出商品、收退款单、预收单、结算冲账单已过账'
					},{
						action: 'ma/logic/per_chk.action',
						type: 'ar_chk_c',
						value: '当月的 发票、其它应收单、发出商品、收退款单、预收单、结算单已制作凭证'
					},{
						action: 'ma/logic/per_chk.action',
						type: 'ar_chk_d',
						value: '当月的 出货单、退货单已全部转开票或发出商品'
					},{
						action: 'ma/logic/per_chk.action',
						type: 'ar_chk_e',
						value: '当月的 发票、发出商品(全部未开票的) 的销售单价、成本单价与 出货单、退货单 的一致'
					},{
						action: 'ma/logic/per_chk.action',
						type: 'ar_chk_f',
						value: '当月凭证中，应收款科目有手工录入的(来源为空的)'
					},{
						action: 'ma/logic/per_chk.action',
						type: 'ar_chk_g',
						value: '当月的 总的开票数量与 出货单、退货单 的开票数量一致'
					},{
						action: 'ma/logic/per_chk.action',
						type: 'ar_chk_h',
						value: '当月的 总的发出商品数量是否与 出货单、退货单 的发出商品数量一致'
					},{
						action: 'ma/logic/per_chk.action',
						type: 'ar_chk_i',
						value: '当月开票数据中 涉及发出商品的 总的开票数量与 发出商品的开票数量一致'
					},{
						action: 'ma/logic/per_chk.action',
						type: 'ar_chk_j',
						value: '当月预收款、预收退款与应收总账里本期预收的一致'
					}]
				})
			},{
				xtype: 'fieldcontainer',
				margin: '30 0 0 100',
				fieldLabel: '初始账期',
				anchor: '100% 40%',
				layout:'hbox',
				labelWidth: 70,
				items: [{
					xtype: 'monthdatefield',
			    	allowBlank: false,
			    	id: 'date',
			    	name: 'date'
				},{
					xtype: 'erpConfirmButton',
					cls: 'x-btn-blue',
					id:'confirm',
					width: 80,
					disabled:true
				}]
			}] 
		}); 
		me.callParent(arguments); 
	}
});