Ext.define('erp.view.fa.gs.CheckGSAccount',{ 
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
						action: 'fa/gs/chk_a.action',
						detno: 1,
						type: 'gs_chk_a',
						value: '当月银行现金单据是否全部记账'
					},{
						action: 'fa/gs/chk_b.action',
						detno: 2,
						type: 'gs_chk_b',
						value: '当月银行现金单据是否全部做了凭证'
					},{
						action: 'fa/gs/chk_c.action',
						detno: 3,
						type: 'gs_chk_c',
						value: '银行现金余额是否出现负数'
					},{
						action: 'fa/gs/chk_d.action',
						detno: 4,
						type: 'gs_chk_d',
						value: '票据资金系统期间与总账期间是否一致'
					},{
						action: 'fa/gs/chk_e.action',
						detno: 5,
						type: 'gs_chk_e',
						value: '预收款、预收退款类型的银行登记关联的预收款、预收退款单是否存在、是否已过账'
					},{
						action: 'fa/gs/chk_f.action',
						detno: 6,
						type: 'gs_chk_f',
						value: '应收款、应收退款类型的银行登记关联的收款单、收款退款单是否存在、是否已过账'
					},{
						action: 'fa/gs/chk_g.action',
						detno: 7,
						type: 'gs_chk_g',
						value: '预付款、预付退款类型的银行登记关联的预付款、预付退款单是否存在、是否已过账'
					},{
						action: 'fa/gs/chk_h.action',
						detno: 8,
						type: 'gs_chk_h',
						value: '应付款、应付退款类型的银行登记关联的付款单、付款退款单是否存在、是否已过账'
					},{
						action: 'fa/gs/chk_i.action',
						detno: 9,
						type: 'gs_chk_i',
						value: '转存类型的银行登记是否平衡'
					},{
						action: 'fa/gs/chk_k.action',
						detno: 10,
						type: 'gs_chk_k',
						value: '所有应付票据是否已审核'
					},{
						action: 'fa/gs/chk_l.action',
						detno: 11,
						type: 'gs_chk_l',
						value: '所有应付票据异动单是否已过账'
					},{
						action: 'fa/gs/chk_m.action',
						detno: 12,
						type: 'gs_chk_m',
						value: '所有应收票据是否已审核'
					},{
						action: 'fa/gs/chk_n.action',
						detno: 13,
						type: 'gs_chk_n',
						value: '所有应收票据异动单是否已过账'
					},{
						action: 'fa/gs/chk_o.action',
						detno: 14,
						type: 'gs_chk_o',
						value: '应收票据是否有关联的收款单或预收单，是否已过账'
					},{
						action: 'fa/gs/chk_p.action',
						detno: 15,
						type: 'gs_chk_p',
						value: '应付票据是否有关联的付款单或预付单，是否已过账'
					},{
						action: 'fa/gs/chk_q.action',
						detno: 16,
						type: 'gs_chk_q',
						value: '应收票据异动类型为收款、贴现的，是否有关联的银行登记，是否已记账'
					},{
						action: 'fa/gs/chk_r.action',
						detno: 17,
						type: 'gs_chk_r',
						value: '应收票据异动类型为背书转让的，是否有关联的付款单或预付单，是否已记账'
					},{
						action: 'fa/gs/chk_s.action',
						detno: 18,
						type: 'gs_chk_s',
						value: '应付票据异动类型为兑现的，是否有关联的银行登记，是否已记账'
					},{
						action: 'fa/gs/chk_v.action',
						detno: 19,
						type: 'gs_chk_v',
						value: '应付票据异动类型为退票、作废的,是否有关联的应付退款单、预付退款单，是否已记账'
					},{
						action: 'fa/gs/chk_w.action',
						detno: 20,
						type: 'gs_chk_w',
						value: '应收票据异动类型为退票、作废的，是否有关联的应收退款单、预收退款单，是否已记账'
					},{
						action: 'fa/gs/chk_t.action',
						detno: 21,
						type: 'gs_chk_t',
						value: '当月应付票据科目余额与应付票据票面余额是否一致'
					},{
						action: 'fa/gs/chk_u.action',
						detno: 22,
						type: 'gs_chk_u',
						value: '当月应收票据科目余额与应收票据票面余额是否一致'
					},{
						action: 'fa/gs/chk_j.action',
						detno: 23,
						type: 'gs_chk_j',
						value: '当月银行现金类科目余额与银行存款总账查询界面期末余额是否一致'
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