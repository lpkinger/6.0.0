Ext.define('erp.view.fa.ars.CheckARAccount',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'grid',
				id: 'account-check',
				anchor: '100% 100%',
				tbar: [{
					xtype: 'tbtext',
					text: '当前期间：',
					margin: '0 0 0 20'
				},{
					xtype: 'tbtext',
					id: 'yearmonth',
					text: '201305',
					margin: '0 0 0 2'
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
					text: '序号',
					dataIndex: 'detno',
					flex: 0.7,
					renderer: function(val, meta, record) {
						return val;
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
					fields: [{name: 'action', type: 'string'}, {name: 'detno', type : 'number'}, {name: 'type', type: 'string'}, {name: 'value', type: 'string'}],
					data: [{
						action: 'fa/ars/chk_a.action',
						detno: 1,
						type: 'ar_chk_a',
						value: '应收账款期间 与 总账期间一致'
					},{
						action: 'fa/ars/chk_b.action',
						detno: 2,
						type: 'ar_chk_b',
						value: '当月的应收系统当月所有单据是否全部已过账'
					},{
						action: 'fa/ars/chk_c.action',
						detno: 3,
						type: 'ar_chk_c',
						value: '当月的应收系统当月所有单据是否已全部制作凭证'
					},{
						action: 'fa/ars/chk_d.action',
						detno: 4,
						type: 'ar_chk_d',
						value: '当月的 出货单、退货单已全部转开票或发出商品'
					},{
						action: 'fa/ars/chk_t.action',
						detno: 5,
						type: 'ar_chk_t',
						value: '当月应收发票中来源的发出商品日期年月是否有大于等于应收发票期间的'
					},{
						action: 'fa/ars/chk_u.action',
						detno: 6,
						type: 'ar_chk_u',
						value: '当月应收发票中来源的出入库日期是否存在非当月的'
					},{
						action: 'fa/ars/chk_e.action',
						detno: 7,
						type: 'ar_chk_e',
						value: '当月的发票的销售单价、成本单价与来源发出商品/出货单、销售退货单一致'
					},{
						action: 'fa/ars/chk_f.action',
						detno: 8,
						type: 'ar_chk_f',
						value: '当月凭证中，应收款科目有手工录入的(来源为空的)'
					},{
						action: 'fa/ars/chk_g.action',
						detno: 9,
						type: 'ar_chk_g',
						value: '当月的 总的开票数量与 出货单、退货单 的开票数量一致'
					},{
						action: 'fa/ars/chk_h.action',
						detno: 10,
						type: 'ar_chk_h',
						value: '当月的 总的发出商品数量是否与 出货单、退货单 的发出商品数量一致'
					}/*,{
						action: 'fa/ars/chk_i.action',
						type: 'ar_chk_i',
						value: '当月开票数据中 涉及发出商品的 总的开票数量与 发出商品的开票数量一致'
					}*/,{
						action: 'fa/ars/chk_v.action',
						detno: 11,
						type: 'ar_chk_v',
						value: '当月应收账款科目余额与应收总账应收期末余额一致'
					},{
						action: 'fa/ars/chk_w.action',
						detno: 12,
						type: 'ar_chk_w',
						value: '当月预收账款科目余额与应收总账预收期末余额一致'
					},{
						action: 'fa/ars/chk_x.action',
						detno: 13,
						type: 'ar_chk_x',
						value: '当月发出商品科目余额与应收总账（成本）发出商品期末余额一致'
					},{
						action: 'fa/ars/chk_y.action',
						detno: 14,
						type: 'ar_chk_y',
						value: '预收和应收都有余额的客户'
					}]
				}),
				bbar: [{
					xtype: 'checkbox',
					boxLabel: '知道错误了，我要继续结账',
					id : 'allow',
					hidden : true,
					margin: '0 5 0 20'
				},'->',{
					cls: 'x-btn-blue',
					id: 'check',
					text: '结账检查',
					width: 80,
					margin: '0 0 0 50'
				},{
					cls: 'x-btn-blue',
					id: 'accoutover',
					text: '结  账',
					width: 80,
					disabled : true,
					margin: '0 0 0 5'
				},{
					cls: 'x-btn-blue',
					id: 'resaccoutover',
					text: '反结账',
					width: 80,
					margin: '0 0 0 5'
				},{
					cls: 'x-btn-blue',
					id: 'close',
					text: $I18N.common.button.erpCloseButton,
					width: 80,
					margin: '0 175 0 5'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});