Ext.define('erp.view.fa.gla.CheckGLAAccount',{ 
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
						action: 'fa/gla/chk_j.action',
						detno: 1,
						type: 'gla_chk_j',
						value: '总账系统当月子模块是否已全部结账'
					},{
						action: 'fa/gla/chk_a.action',
						detno: 2,
						type: 'gla_chk_a',
						value: '总账系统当月所有凭证是否全部记账'
					},{
						action: 'fa/gla/chk_b.action',
						detno: 3,
						type: 'gla_chk_b',
						value: '总账系统当月凭证是否有重号的'
					},{
						action: 'fa/gla/chk_c.action',
						detno: 4,
						type: 'gla_chk_c',
						value: '总账系统当月凭证是否有断号的'
					},{
						action: 'fa/gla/chk_d.action',
						detno: 5,
						type: 'gla_chk_d',
						value: '是否有摘要为结转制造费用的凭证'
					},{
						action: 'fa/gla/chk_e.action',
						detno: 6,
						type: 'gla_chk_e',
						value: '是否有摘要为汇兑损益的凭证'
					},{
						action: 'fa/gla/chk_f.action',
						detno: 7,
						type: 'gla_chk_f',
						value: '是否有摘要为结转损益的凭证'
					},{
						action: 'fa/gla/chk_g.action',
						detno: 8,
						type: 'gla_chk_g',
						value: '科目类型为损益类的科目，当月余额是否全部为0'
					},{
						action: 'fa/gla/chk_h.action',
						detno: 9,
						type: 'gla_chk_h',
						value: '科目余额表中科目余额的方向与科目属性中对应的科目余额方向是否一致'
					},{
						action: 'fa/gla/chk_i.action',
						detno: 10,
						type: 'gla_chk_i',
						value: '当月损益类科目/制造费用科目在凭证中的借贷方向是否和科目性质一致（除结转损益、结转制造费用凭证）'
					},{
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
						action: 'fa/ars/chk_r.action',
						detno: 14,
						type: 'ar_chk_r',
						value: '当月的 发票收入总额 与主营业务收入 的贷方一致'
					},{
						action: 'fa/ars/chk_s.action',
						detno: 15,
						type: 'ar_chk_s',
						value: '当月的 发票成本总额 与主营业务成本 的借方一致'
					},{
						action: 'fa/arp/chk_r.action',
						detno: 16,
						type: 'ap_chk_r',
						value: '当月应付账款科目余额与应付总账应付期末余额是否一致'
					},{
						action: 'fa/arp/chk_s.action',
						detno: 17,
						type: 'ap_chk_s',
						value: '当月预付账款科目余额与预付总账预付期末余额是否一致'
					},{
						action: 'fa/arp/chk_t.action',
						detno: 18,
						type: 'ap_chk_t',
						value: '当月应付暂估科目余额与应付总账应付暂估余额(采购价除税)是否一致'
					},{
						action: 'fa/gs/chk_t.action',
						detno: 19,
						type: 'gs_chk_t',
						value: '当月应付票据科目余额与应付票据票面余额是否一致'
					},{
						action: 'fa/gs/chk_u.action',
						detno: 20,
						type: 'gs_chk_u',
						value: '当月应收票据科目余额与应收票据票面余额是否一致'
					},{
						action: 'fa/gs/chk_j.action',
						detno: 21,
						type: 'gs_chk_j',
						value: '当月银行现金类科目余额与银行存款总账查询界面期末余额是否一致'
					},{
						action: 'fa/fix/chk_h.action',
						detno: 22,
						type: 'fix_chk_h',
						value: '当前期间固定资产所有卡片的原值金额合计与总账固定资产科目的本期余额是否一致'
					},{
						action: 'fa/fix/chk_i.action',
						detno: 23,
						type: 'fix_chk_i',
						value: '当前期间固定资产所有卡片的累计折旧金额合计与总账累计折旧科目的本期余额是否一致'
					},{
						action: 'co/cost/chk_c.action',
						detno: 24,
						type: 'co_chk_c',
						value: '总账生产成本科目余额与成本表上工单类型是制造单的期末结余金额+期末报废结余金额合计是否一致'
					},{
						action: 'co/cost/chk_d.action',
						detno: 25,
						type: 'co_chk_d',
						value: '总账委托加工物资科目余额与成本表上工单类型是委外加工单的期末结余金额+期末报废结余金额合计是否一致'
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