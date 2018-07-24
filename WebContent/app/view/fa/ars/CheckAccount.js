Ext.define('erp.view.fa.ars.CheckAccount',{ 
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
					text: '会计期间：',
					margin: '0 0 0 20'
				},{
					xtype: 'tbtext',
					id: 'yearmonth',
					text: '201305',
					margin: '0 0 0 2'
				},{
					cls: 'x-btn-blue',
					id: 'check',
					text: '检查',
					width: 80,
					margin: '0 0 0 50'
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
						value: '当月的 出货单、退货单、发票、其它应收单、发出商品、收退款单、预收单、结算冲账单已过账'
					},{
						action: 'fa/ars/chk_c.action',
						detno: 3,
						type: 'ar_chk_c',
						value: '当月的 发票、其它应收单、发出商品、收退款单、预收单、结算单已制作凭证'
					},{
						action: 'fa/ars/chk_d.action',
						detno: 4,
						type: 'ar_chk_d',
						value: '当月的 出货单、退货单已全部转开票或发出商品'
					},{
						action: 'fa/ars/chk_e.action',
						detno: 5,
						type: 'ar_chk_e',
						value: '当月的发票的销售单价、成本单价与来源发出商品/出货单、销售退货单一致'
					},{
						action: 'fa/ars/chk_f.action',
						detno: 6,
						type: 'ar_chk_f',
						value: '当月凭证中，应收款科目有手工录入的(来源为空的)'
					},{
						action: 'fa/ars/chk_g.action',
						detno: 7,
						type: 'ar_chk_g',
						value: '当月的 总的开票数量与 出货单、退货单 的开票数量一致'
					},{
						action: 'fa/ars/chk_h.action',
						detno: 8,
						type: 'ar_chk_h',
						value: '当月的 总的发出商品数量是否与 出货单、退货单 的发出商品数量一致'
					},/*{
						action: 'fa/ars/chk_i.action',
						type: 'ar_chk_i',
						value: '当月开票数据中 涉及发出商品的 总的开票数量与 发出商品的开票数量一致'
					},*/{
						action: 'fa/ars/chk_j.action',
						detno: 9,
						type: 'ar_chk_j',
						value: '当月预收款、预收退款与应收总账里本期预收的一致'
					},{
						action: 'fa/ars/chk_k.action',
						detno: 10,
						type: 'ar_chk_k',
						value: '当月预收冲账与应收总账里本期预收冲账的一致'
					},{
						action: 'fa/ars/chk_l.action',
						detno: 11,
						type: 'ar_chk_l',
						value: '当月发出商品(成本价)与应收总账里本期发出商品的一致'
					},{
						action: 'fa/ars/chk_m.action',
						detno: 12,
						type: 'ar_chk_m',
						value: '当月发出商品(销售价)与应收总账里本期发出商品的一致'
					},{
						action: 'fa/ars/chk_n.action',
						detno: 13,
						type: 'ar_chk_n',
						value: '当月开票数据中 涉及发出商品的（成本价）与应收总账里本期发出商品转开票的一致'
					},{
						action: 'fa/ars/chk_o.action',
						detno: 14,
						type: 'ar_chk_o',
						value: '当月开票数据中 涉及发出商品的（销售价）与应收总账里本期发出商品转开票的一致'
					},{
						action: 'fa/ars/chk_p.action',
						detno: 15,
						type: 'ar_chk_p',
						value: '当月的 发票、其它应收单 的总额与 应收总账本期应收一致'
					},{
						action: 'fa/ars/chk_q.action',
						detno: 16,
						type: 'ar_chk_q',
						value: '当月的 收款单、退款单、结算单 的总额与 应收总账的本期收款一致'
					},{
						action: 'fa/ars/chk_r.action',
						detno: 17,
						type: 'ar_chk_r',
						value: '当月的 发票收入总额 与主营业务收入 的贷方一致'
					},{
						action: 'fa/ars/chk_s.action',
						detno: 18,
						type: 'ar_chk_s',
						value: '当月的 发票成本总额 与主营业务成本 的借方一致'
					},{
						action: 'fa/ars/chk_y.action',
						detno: 19,
						type: 'ar_chk_y',
						value: '当月的 预收和应收同时有余额'
					}]
				})
			}] 
		}); 
		me.callParent(arguments); 
	} 
});