Ext.define('erp.view.co.cost.BeforeAccount',{ 
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
					margin: '0 0 0 10'
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
						action: 'co/cost/chk_before_a.action',
						detno: 1,
						type: 'co_chk_before_a',
						value: '同成本期间的库存期间是否已经冻结'
					},{
						action: 'co/cost/chk_before_b.action',
						detno: 2,
						type: 'co_chk_before_b',
						value: '当月发生的领退补完工验收报废是否有工单号+序号不存在的'
					},{
						action: 'co/cost/chk_before_c.action',
						detno: 3,
						type: 'co_chk_before_c',
						value: '成本表：期初成本结余金额是否等于上个月的期末成本结余金额'
					},{
						action: 'co/cost/chk_before_d.action',
						detno: 4,
						type: 'co_chk_before_d',
						value: '成本表：报废期初结余金额是否等于上个月的期末报废结余金额'
					},{
						action: 'co/cost/chk_before_e.action',
						detno: 5,
						type: 'co_chk_before_e',
						value: '成本表：期初完工数是否等于上个月的期末完工数'
					},{
						action: 'co/cost/chk_before_f.action',
						detno: 6,
						type: 'co_chk_before_f',
						value: '月结表：期初数量与上个月期末结余数量比较'
					},{
						action: 'co/cost/chk_before_g.action',
						detno: 7,
						type: 'co_chk_before_g',
						value: '月结表：期初金额与上个月期末结余金额比较'
					},{
						action: 'co/cost/chk_before_h.action',
						detno: 8,
						type: 'co_chk_before_h',
						value: '月结表：报废期初余额与上个月期末报废结余金额比较'
					},{
						action: 'co/cost/chk_before_i.action',
						detno: 9,
						type: 'co_chk_before_i',
						value: '当月有完工数的制造单没工时的'
					},{
						action: 'co/cost/chk_before_j.action',
						detno: 10,
						type: 'co_chk_before_j',
						value: '当期是否有未过账的出入库单'
					},{
						action: 'co/cost/chk_before_k.action',
						detno: 11,
						type: 'co_chk_before_k',
						value: '是否有非无值仓原材料单价为0'
					},{
						action: 'co/cost/chk_before_l.action',
						detno: 12,
						type: 'co_chk_before_l',
						value: '拨出拨入是否平衡'
					},{
						action: 'co/cost/chk_before_m.action',
						detno: 13,
						type: 'co_chk_before_m',
						value: '销售拨出拨入是否平衡'
					},{
						action: 'co/cost/chk_before_n.action',
						detno: 14,
						type: 'co_chk_before_n',
						value: '当月出入库是否做了凭证'
					},{
						action: 'co/cost/chk_before_o.action',
						detno: 15,
						type: 'co_chk_before_o',
						value: '当月出入库有凭证编号但是凭证在当月不存在'
					},{
						action: 'co/cost/chk_before_p.action',
						detno: 16,
						type: 'co_chk_before_p',
						value: '出入库单据中文状态'
					},{
						action: 'co/cost/chk_before_q.action',
						detno: 17,
						type: 'co_chk_before_q',
						value: '是否有工单的成品物料编号不存在'
					},{
						action: 'co/cost/chk_before_r.action',
						detno: 18,
						type: 'co_chk_before_r',
						value: '是否有工单用料表的物料不存在'
					},{
						action: 'co/cost/chk_before_s.action',
						detno: 19,
						type: 'co_chk_before_s',
						value: '是否有委外工单有加工单价但是没有维护币别'
					},{
						action: 'co/cost/chk_before_s1.action',
						detno: 20,
						type: 'co_chk_before_s1',
						value: '当月委外验收、验退加工价跟委外单是否一致'
					},{
						action: 'co/cost/chk_before_s2.action',
						detno: 21,
						type: 'co_chk_before_s2',
						value: '当月委外验收、验退单税率跟委外单是否一致'
					},{
						action: 'co/cost/chk_before_t.action',
						detno: 22,
						type: 'co_chk_before_t',
						value: '成本表期初完工数是否正确'
					},{
						action: 'co/cost/chk_before_u.action',
						detno: 23,
						type: 'co_chk_before_u',
						value: '当月出入库单里料号不存在'
					},{
						action: 'co/cost/chk_before_v.action',
						detno: 24,
						type: 'co_chk_before_v',
						value: '当期是否有未审核的生产报废单'
					},{
						action: 'co/cost/chk_before_w.action',
						detno: 25,
						type: 'co_chk_before_w',
						value: '当月采购验退单、委外验收单、委外验退单生成应付暂估/应付发票并制作了凭证的'
					},{
						action: 'co/cost/chk_before_x.action',
						detno: 26,
						type: 'co_chk_before_x',
						value: '当月出货单、销售退货单生成应收发票并制作了结转主营业务成本凭证的'
					},{
						action: 'co/cost/chk_before_y.action',
						detno: 27,
						type: 'co_chk_before_y',
						value: '当月发出商品制作了凭证的'
					}]
				})
			}] 
		}); 
		me.callParent(arguments); 
	} 
});