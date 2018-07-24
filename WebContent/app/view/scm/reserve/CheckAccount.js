Ext.define('erp.view.scm.reserve.CheckAccount',{ 
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
						action: 'scm/reserve/chk_a.action',
						detno: 1,
						type: 'scm_chk_a',
						value: '是否有未制作凭证的单据'
					},{
						action: 'scm/reserve/chk_b.action',
						detno: 2,
						type: 'scm_chk_b',
						value: '是否有出入库单凭证号异常的单据'
					},{
						action: 'scm/reserve/chk_c.action',
						detno: 3,
						type: 'scm_chk_c',
						value: '拨出拨入是否平衡'
					},{
						action: 'scm/reserve/chk_d.action',
						detno: 4,
						type: 'scm_chk_d',
						value: '销售拨出拨入是否平衡'
					},{
						action: 'scm/reserve/chk_e.action',
						detno: 5,
						type: 'scm_chk_e',
						value: '应付发票中成本单价跟出入库成本单价是否一致'
					},{
						action: 'scm/reserve/chk_f.action',
						detno: 6,
						type: 'scm_chk_f',
						value: '暂估单成本单价跟出入库单成本单价是否一致'
					},{
						action: 'scm/reserve/chk_g.action',
						detno: 7,
						type: 'scm_chk_g',
						value: '验收单据数量与当月开票+暂估是否一致'
					},{
						action: 'scm/reserve/chk_h.action',
						detno: 8,
						type: 'scm_chk_h',
						value: '应收发票成本单价跟出入库单成本单价是否一致'
					},{
						action: 'scm/reserve/chk_i.action',
						detno: 9,
						type: 'scm_chk_i',
						value: '发出商品成本价跟出入库单成本单价是否一致'
					},{
						action: 'scm/reserve/chk_j.action',
						detno: 10,
						type: 'scm_chk_j',
						value: '出货单据数量与当月开票+发出商品是否一致'
					}
					,{
						action: 'scm/reserve/chk_k.action',
						detno: 11,
						type: 'scm_chk_k',
						value: '其它出入库单据的单据类型+小类+部门是否设置了对方科目'
					},{
						action: 'scm/reserve/chk_l.action',
						detno: 12,
						type: 'scm_chk_l',
						value: '其它出入库基础科目设置是否有重复的'
					},{
						action: 'scm/reserve/chk_m.action',
						detno: 13,
						type: 'scm_chk_m',
						value: '是否有出入库单据出、入数量都不为0'
					},{
						action: 'scm/reserve/chk_n.action',
						detno: 14,
						type: 'scm_chk_n',
						value: '是否有料号不存在'
					},{
						action: 'scm/reserve/chk_o.action',
						detno: 15,
						type: 'scm_chk_o',
						value: '是否有物料的存货科目没有设置'
					},{
						action: 'scm/reserve/chk_p.action',
						detno: 16,
						type: 'scm_chk_p',
						value: '当期是否有未过账的出入库单据'
					},{
						action: 'scm/reserve/chk_q.action',
						detno: 17,
						type: 'scm_chk_q',
						value: '当期是否有无值仓有成本单价单据'
					},{
						action: 'scm/reserve/chk_r.action',
						detno: 18,
						type: 'scm_chk_r',
						value: '库存月结表金额与存货科目金额是否一致'
					},{
						action: 'scm/reserve/chk_t.action',
						detno: 19,
						type: 'scm_chk_t',
						value: '存货模块金额与总账模块金额是否一致'
					},{
						action: 'scm/reserve/chk_u.action',
						detno: 20,
						type: 'scm_chk_u',
						value: '应付暂估与存货科目金额是否一致'
					},{
						action: 'scm/reserve/chk_v.action',
						detno: 21,
						type: 'scm_chk_v',
						value: '应付发票（当月验收验退当月开票）与存货科目金额是否一致'
					},{
						action: 'scm/reserve/chk_w.action',
						detno: 22,
						type: 'scm_chk_w',
						value: '应收发出商品与存货科目金额是否一致'
					},{
						action: 'scm/reserve/chk_x.action',
						detno: 23,
						type: 'scm_chk_x',
						value: '应收发票（当月出货退货当月开票）与存货科目金额是否一致'
					}]
				})
			}] 
		}); 
		me.callParent(arguments); 
	} 
});