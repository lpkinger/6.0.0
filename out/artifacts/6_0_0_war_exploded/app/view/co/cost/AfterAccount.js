Ext.define('erp.view.co.cost.AfterAccount',{ 
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
						action: 'co/cost/chk_after_a.action',
						detno: 1,
						type: 'co_chk_after_a',
						value: '成本表：工单工作中心是否与工单一致'
					},{
						action: 'co/cost/chk_after_b.action',
						detno: 2,
						type: 'co_chk_after_b',
						value: '成本表：工单类型是否与工单一致'
					},{
						action: 'co/cost/chk_after_c.action',
						detno: 3,
						type: 'co_chk_after_c',
						value: '成本表：产品编号是否不存在'
					},{
						action: 'co/cost/chk_after_d.action',
						detno: 4,
						type: 'co_chk_after_d',
						value: '成本表：工单工单数量是否与工单一致'
					},{
						action: 'co/cost/chk_after_e.action',
						detno: 5,
						type: 'co_chk_after_e',
						value: '成本表：本期完工数是否与实际关联的本期完工一致'
					},{
						action: 'co/cost/chk_after_f.action',
						detno: 6,
						type: 'co_chk_after_f',
						value: '成本表：本期报废数量、金额是否与用料月结表里一致（下面用料月结表也会跟实际单据比较）'
					},{
						action: 'co/cost/chk_after_g.action',
						detno: 7,
						type: 'co_chk_after_g',
						value: '成本表：本期领料金额是否等于∑（工单关联的用料月结表中本期领料金额+本期补料金额-本期退料金额），是否与工单关联的领退补单据一致'
					},{
						action: 'co/cost/chk_after_h.action',
						detno: 8,
						type: 'co_chk_after_h',
						value: '成本表：分摊费用按工作中心总和是否与费用表一致(按不同工作中心分组依次比较每一项)'
					},{
						action: 'co/cost/chk_after_i.action',
						detno: 9,
						type: 'co_chk_after_i',
						value: '成本表：最终成本是否=总转出成本/本期完工数+加工价+上面几个分摊的单个费用'
					},{
						action: 'co/cost/chk_after_j.action',
						detno: 10,
						type: 'co_chk_after_j',
						value: '成本表：工单的状态是否准确'
					},{
						action: 'co/cost/chk_after_k.action',
						detno: 11,
						type: 'co_chk_after_k',
						value: '成本表：是否当月有发生领退补完工验收验退报废的工单都体现在成本表里'
					},{
						action: 'co/cost/chk_after_l.action',
						detno: 12,
						type: 'co_chk_after_l',
						value: '成本表：检查最终成本是否成功核算到完工入库、委外验收单里'
					},{
						action: 'co/cost/chk_after_m.action',
						detno: 13,
						type: 'co_chk_after_m',
						value: '成本表：委外加工单价跟委外单是否一致'
					},{
						action: 'co/cost/chk_after_ao.action',
						detno: 14,
						type: 'co_chk_after_ao',
						value: '成本表：本期报废转出成本与月结表本期报废转出金额是否一致'
					},{
						action: 'co/cost/chk_after_n.action',
						detno: 15,
						type: 'co_chk_after_n',
						value: '月结表：检查月结表用料是否重复'
					},{
						action: 'co/cost/chk_after_o.action',
						detno: 16,
						type: 'co_chk_after_o',
						value: '月结表：检查单位用量是否与工单用料表里一致'
					},{
						action: 'co/cost/chk_after_p.action',
						detno: 17,
						type: 'co_chk_after_p',
						value: '月结表：检查总用量是否与工单用料表里一致'
					},{
						action: 'co/cost/chk_after_q.action',
						detno: 18,
						type: 'co_chk_after_q',
						value: '月结表：检查本期领料数量、金额'
					},{
						action: 'co/cost/chk_after_r.action',
						detno: 19,
						type: 'co_chk_after_r',
						value: '月结表：检查本期补料数量、金额'
					},{
						action: 'co/cost/chk_after_s.action',
						detno: 20,
						type: 'co_chk_after_s',
						value: '月结表：检查本期退料数量、金额'
					},{
						action: 'co/cost/chk_after_t.action',
						detno: 21,
						type: 'co_chk_after_t',
						value: '月结表：检查单价cdm_price的逻辑(影响到退料核算)'
					},{
						action: 'co/cost/chk_after_u.action',
						detno: 22,
						type: 'co_chk_after_u',
						value: '月结表：检查本期报废数量'
					},{
						action: 'co/cost/chk_after_v.action',
						detno: 23,
						type: 'co_chk_after_v',
						value: '月结表：检查累计报废数量'
					},{
						action: 'co/cost/chk_after_w.action',
						detno: 24,
						type: 'co_chk_after_w',
						value: '月结表：检查本期成品入库数是否等于成本表本期完工数'
					},{
						action: 'co/cost/chk_after_x.action',
						detno: 25,
						type: 'co_chk_after_x',
						value: '月结表：检查实际单位用量，（总用量-前期转出数量）/期初未完工数'
					},{
						action: 'co/cost/chk_after_y.action',
						detno: 26,
						type: 'co_chk_after_y',
						value: '月结表：检查期末数量：期初数量+本期领料数量-本期退料数量+本期补料数量-本期转出数量-本期报废数量'
					},{
						action: 'co/cost/chk_after_z.action',
						detno: 27,
						type: 'co_chk_after_z',
						value: '月结表：期末金额：期初金额+本期领料金额-本期退料金额+本期补料金额-本期转出金额-本期报废金额'
					},{
						action: 'co/cost/chk_after_aa.action',
						detno: 28,
						type: 'co_chk_after_aa',
						value: '月结表：期末报废结余金额：本期报废金额+报废期初余额-本期报废转出金额'
					},{
						action: 'co/cost/chk_after_an.action',
						detno: 29,
						type: 'co_chk_after_an',
						value: '月结表：转出数量是否正确'
					},{
						action: 'co/cost/chk_after_ac.action',
						detno: 30,
						type: 'co_chk_after_ac',
						value: '检查月结表料号不存在'
					},{
						action: 'co/cost/chk_after_ad.action',
						detno: 31,
						type: 'co_chk_after_ad',
						value: '检查成本表领退补跟出入库差异'
					},{
						action: 'co/cost/chk_after_af.action',
						detno: 32,
						type: 'co_chk_after_af',
						value: '检查是否有期末完工数不等于期初完工数+本期完工数'
					},{
						action: 'co/cost/chk_after_ag.action',
						detno: 33,
						type: 'co_chk_after_ag',
						value: '检查是否有期末完工数大于工单数的'
					},{
						action: 'co/cost/chk_after_ah.action',
						detno: 34,
						type: 'co_chk_after_ah',
						value: '当月委外验收单、委外验退单加工单价与委外单上的加工单价是否一致'
					},{
						action: 'co/cost/chk_after_ai.action',
						detno: 35,
						type: 'co_chk_after_ai',
						value: '检查是否有最终成本负数的情况'
					},{
						action: 'co/cost/chk_after_aj.action',
						detno: 36,
						type: 'co_chk_after_aj',
						value: '检查材料成本负数的情况'
					},{
						action: 'co/cost/chk_after_ak.action',
						detno: 37,
						type: 'co_chk_after_ak',
						value: '检查标准工时是否与物料里一致'
					},{
						action: 'co/cost/chk_after_al.action',
						detno: 38,
						type: 'co_chk_after_al',
						value: '检查总工时'
					},{
						action: 'co/cost/chk_after_am.action',
						detno: 39,
						type: 'co_chk_after_am',
						value: '检查库存月结表有期末金额没有期末数量的情况'
					}]
				})
			}] 
		}); 
		me.callParent(arguments); 
	} 
});