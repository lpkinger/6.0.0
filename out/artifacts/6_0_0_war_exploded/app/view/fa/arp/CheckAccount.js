Ext.define('erp.view.fa.arp.CheckAccount',{ 
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
						action: 'fa/arp/chk_a.action',
						detno: 1,
						type: 'ap_chk_a',
						value: '应付账款期间 与 总账期间一致'
					},{
						action: 'fa/arp/chk_b.action',
						detno: 2,
						type: 'ap_chk_b',
						value: '当月的 验收单、验退单、发票、其它应付单、暂估、预付单、预付退款单、结算冲账单等已过账'
					},{
						action: 'fa/arp/chk_c.action',
						detno: 3,
						type: 'ap_chk_c',
						value: '当月的 发票、其它应付单、暂估、付退款单、预付单、结算单已制作凭证'
					},{
						action: 'fa/arp/chk_d.action',
						detno: 4,
						type: 'ap_chk_d',
						value: '当月的 验收单、验退单已全部转开票或暂估'
					},{
						action: 'fa/arp/chk_e.action',
						detno: 5,
						type: 'ap_chk_e',
						value: '当月的 发票、暂估(全部未开票的) 的采购单价、成本单价与 验收单、验退单 的一致'
					},{
						action: 'fa/arp/chk_f.action',
						detno: 6,
						type: 'ap_chk_f',
						value: '当月凭证中，应付款科目有手工录入的(来源为空的)'
					},{
						action: 'fa/arp/chk_g.action',
						detno: 7,
						type: 'ap_chk_g',
						value: '当月的 总的开票数量与 验收单、验退单 的开票数量一致'
					},{
						action: 'fa/arp/chk_h.action',
						detno: 8,
						type: 'ap_chk_h',
						value: '当月的 总的暂估数量是否与 验收单、验退单 的暂估数量一致'
					}/*,{
						action: 'fa/arp/chk_i.action',
						type: 'ap_chk_i',
						value: '当月开票数据中 涉及暂估的 总的开票数量与 暂估的开票数量一致'
					}*/,{
						action: 'fa/arp/chk_j.action',
						detno: 9,
						type: 'ap_chk_j',
						value: '当月预付款、预付退款与应付总账里本期预付的一致'
					},{
						action: 'fa/arp/chk_k.action',
						detno: 10,
						type: 'ap_chk_k',
						value: '当月预付冲账与应付总账里本期预付冲账的一致'
					},{
						action: 'fa/arp/chk_l.action',
						detno: 11,
						type: 'ap_chk_l',
						value: '当月暂估与应付总账里本期应付暂估增加一致'
					},
					/*{
						action: 'fa/arp/chk_m.action',
						type: 'ap_chk_m',
						value: '当月暂估(采购价)与应付总账里本期暂估的一致'
					},*/
					{
						action: 'fa/arp/chk_n.action',
						detno: 12,
						type: 'ap_chk_n',
						value: '当月开票数据中涉及暂估的与应付总账里本期应付暂估减少一致'
					},
					/*{
						action: 'fa/arp/chk_o.action',
						type: 'ap_chk_o',
						value: '当月开票数据中 涉及暂估的（采购价）与应付总账里本期暂估转开票的一致'
					},*/
					{
						action: 'fa/arp/chk_p.action',
						detno: 13,
						type: 'ap_chk_p',
						value: '当月的 发票、其它应付单 的总额与 应付总账本期应付一致'
					},{
						action: 'fa/arp/chk_q.action',
						detno: 14,
						type: 'ap_chk_q',
						value: '当月的 付款单、退款单、结算单 的总额与 应付总账的本期付款一致'
					},{
						action: 'fa/arp/chk_u.action',
						detno: 15,
						type: 'ap_chk_u',
						value: '当月的 预付和应付同时有余额'
					}/*,{
						action: 'fa/arp/chk_r.action',
						type: 'ap_chk_r',
						value: '当月的 发票采购总额 与 主营业务付入 的借方一致'
					},{
						action: 'fa/arp/chk_s.action',
						type: 'ap_chk_s',
						value: '当月的 发票成本总额 与 主营业务成本 的贷方发生一致'
					}*/]
				})
			}] 
		}); 
		me.callParent(arguments); 
	} 
});